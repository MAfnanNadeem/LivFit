package life.mibo.hardware;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

import life.mibo.hardware.bluetooth.BleGattManager;
import life.mibo.hardware.core.DataParser;
import life.mibo.hardware.core.Logger;
import life.mibo.hardware.encryption.Encryption;
import life.mibo.hardware.events.BleConnection;
import life.mibo.hardware.events.ChangeColorEvent;
import life.mibo.hardware.events.DeviceSearchEvent;
import life.mibo.hardware.events.SendChannelsLevelEvent;
import life.mibo.hardware.events.SendCircuitEvent;
import life.mibo.hardware.events.SendDevicePlayEvent;
import life.mibo.hardware.events.SendDeviceStartEvent;
import life.mibo.hardware.events.SendDeviceStopEvent;
import life.mibo.hardware.events.SendMainLevelEvent;
import life.mibo.hardware.events.SendProgramChangesHotEvent;
import life.mibo.hardware.events.SendProgramEvent;
import life.mibo.hardware.models.Device;
import life.mibo.hardware.models.DeviceColors;
import life.mibo.hardware.models.program.Circuit;
import life.mibo.hardware.models.program.Program;
import life.mibo.hardware.network.CommunicationListener;
import life.mibo.hardware.network.TCPClient;
import life.mibo.hardware.network.UDPServer;

import static java.lang.Thread.sleep;
import static life.mibo.hardware.constants.CommunicationConstants.ASYNC_PROGRAM_STATUS;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_ASYNC_PAUSE;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_ASYNC_SET_MAIN_LEVEL;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_ASYNC_START;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_DEVICE_STATUS_RESPONSE;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_FIRMWARE_REVISION_RESPONSE;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_PAUSE_CURRENT_CYCLE_RESPONSE;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_PING_RESPONSE;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_RESET_CURRENT_CYCLE_RESPONSE;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_SET_CHANNELS_LEVELS_RESPONSE;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_SET_COMMON_STIMULATION_PARAMETERS_RESPONSE;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_SET_DEVICE_COLOR_RESPONSE;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_SET_MAIN_LEVEL_RESPONSE;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_START_CURRENT_CYCLE_RESPONSE;
import static life.mibo.hardware.constants.CommunicationConstants.MIN_COMMAND_LENGTH;
import static life.mibo.hardware.constants.CommunicationsConfig.TCP_PORT;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_ALARM_DISCONNECTED;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_CONNECTED;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_CONNECTING;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_DISCONNECTED;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_NEUTRAL;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_WAITING;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_WARNING;
import static life.mibo.hardware.models.DeviceTypes.BLE_STIMULATOR;
import static life.mibo.hardware.models.DeviceTypes.HR_MONITOR;
import static life.mibo.hardware.models.DeviceTypes.RXL_WIFI;
import static life.mibo.hardware.models.DeviceTypes.SCALE;
import static life.mibo.hardware.models.DeviceTypes.WIFI_STIMULATOR;

/**
 * Created by Fer on 18/03/2019.
 * updated by Sumeet Kumar on 17/12/2019.
 *
 */

public class CommunicationManager {

    private static CommunicationManager manager;
    private ArrayList<TCPClient> tcpClients = new ArrayList<>();
    private UDPServer udpServer;
    private BluetoothManager bluetoothManager;
    //public ScaleManager scaleManager;
    private ArrayList<Device> mDiscoveredDevices = new ArrayList<>();
    //private Activity activity;
    private boolean commRunning = true;
    private Thread pingThread;
    private boolean isWifi = false;

    private CommunicationManager() {
        //EventBus.getDefault().register(this);
        if (manager != null) {
            throw new RuntimeException("getInstance() to get the instance of this class");
        }
        pingThread = new Thread(new PingThread());
        pingThread.start();
    }

    private CommunicationManager(CommunicationListener listener) {
        this();
        setListener(listener);
    }

    public static CommunicationManager getInstance() {
        if (manager == null) {
            manager = new CommunicationManager();
        }
        return manager;
    }

    public static CommunicationManager getInstance(CommunicationListener listener) {
        if (manager == null) {
            manager = new CommunicationManager(listener);
        }
        if (manager.listener == null)
            manager.listener = listener;
        return manager;
    }

    class PingThread implements Runnable {
        @Override
        public void run() {
            while (commRunning) {
                try {
                    Thread.sleep(4000);
                    for (TCPClient t : tcpClients) {
                        t.sendMessage(DataParser.sendGetStatus());
                        pingSentDevice(t.getUid());
                        // Log.e("commManag", "send ping");
                    }
                    if (bluetoothManager != null) {
                        for (BluetoothDevice d : bluetoothManager.getConnectedBleDevices()) {
                            if (d.getName() != null) {
                                if (d.getName().contains("MIBO-")) {
                                    bluetoothManager.sendPingToBoosterGattDevice(DataParser.sendGetStatus(), d);
                                    pingSentDevice(d.getName().replace("MIBO-", ""));
                                }
                                if ((d.getName().contains("HW") || d.getName().contains("Geonaute"))) {
                                    bluetoothManager.sendPingToBoosterGattDevice(DataParser.sendGetStatus(), d);
                                    pingSentDevice(d.toString());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void pingSentDevice(String uid) {
        for (Device d : SessionManager.getInstance().getSession().getConnectedDevices()) {//for(Device d : mDiscoveredDevices) {
            if (d.getUid().equals(uid)) {
                switch (d.getStatusConnected()) {
                    case DEVICE_NEUTRAL:
                        d.setStatusConnected(DEVICE_WAITING);
                        SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setStatusConnected(DEVICE_WAITING);
                        break;
                    case DEVICE_CONNECTED:
                        d.setStatusConnected(DEVICE_WAITING);
                        SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setStatusConnected(DEVICE_WAITING);
                        break;
                    case DEVICE_WAITING:
                        d.setStatusConnected(DEVICE_WARNING);
                        SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setStatusConnected(DEVICE_WARNING);
                        if (listener != null)
                            listener.onConnectionStatus(d.getUid());
                        //EventBus.getDefault().postSticky(new onConnectionStatus(d.getUid()));
                        break;
                    case DEVICE_WARNING:
                        d.setStatusConnected(DEVICE_DISCONNECTED);
                        SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setStatusConnected(DEVICE_DISCONNECTED);
                        if (listener != null)
                            listener.onConnectionStatus(d.getUid());
                        //EventBus.getDefault().postSticky(new onConnectionStatus(d.getUid()));

                        break;
                    case DEVICE_DISCONNECTED:
                        AlarmManager.getInstance().getAlarms().AddDeviceAlarmByType(DEVICE_ALARM_DISCONNECTED, d.getUid());
                        if (listener != null)
                            listener.onAlarmEvent();

                        //EventBus.getDefault().postSticky(new onAlarmEvent());
                        break;
                    case DEVICE_CONNECTING:

                        break;
                    default:
                        d.setStatusConnected(DEVICE_WAITING);
                        SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setStatusConnected(DEVICE_WAITING);
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public synchronized void reScanning(final Activity context, boolean isWifi) {
        stopScanning();
        udpServer = null;
        bluetoothManager = null;
        startScanning(context, isWifi);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public synchronized void startScanning(final Activity context, boolean isWifi) {
        this.isWifi = isWifi;
        log("Starting scanning...." + isWifi);
        if (isWifi)
            scanWifi(context);
        else
            scanBluetooth(context);
    }

    public void scanWifi(Activity context) {
        if (udpServer == null) {
            mDiscoveredDevices.clear();
            udpServer = new UDPServer(new UDPServer.OnBroadcastReceived() {
                @Override
                public void broadcastReceived(byte[] msg, InetAddress ip) {
                    if (listener != null)
                        listener.udpDeviceReceiver(msg, ip);
                    log("udpDeviceReceiver " + new String(msg) + " IP " + ip);
                    // TODO remove later broadcastConsumer
                    //broadcastConsumer(msg, ip);
                    SessionManager manager = SessionManager.getInstance();
                    if (manager.getSession() != null) {
                        if (manager.getSession().isBoosterMode()) {// true wifi mode
                            broadcastConsumer(msg, ip);
                            log("Session Manager Boosted Mode");
                        } else {
                            log("Session Manager No Boosted Mode " + manager);
                        }
                    } else {
                        log("Session Manager getSession is NULL " + manager);
                    }
                }
            });
        }
        udpServer.start(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scanBluetooth(Context context) {
        //TODO: Change to not overwrite the current one if its initialized and only start discovery
        if (bluetoothManager == null) {
            bluetoothManager = new BluetoothManager(context, new BluetoothManager.OnBleDeviceDiscovered() {
                @Override
                public void bleHrDeviceDiscovered(String uid, String serial) {
                    log("BluetoothManager bleHrDeviceDiscovered " + uid + " IP " + serial);
                    bleHrDiscoverConsumer(uid, serial);

                }

                @Override
                public void bleBoosterDeviceDiscovered(String uid, String serial) {
                    log("BluetoothManager bleBoosterDeviceDiscovered " + uid + " IP " + serial);
                    //if (!SessionManager.getInstance().getSession().isBoosterMode())// true wifi mode, false ble mode
                    bleBoosterDiscoverConsumer(uid, serial);

                }

                @Override
                public void bleScaleDeviceDiscovered(String uid, String serial) {
                    log("BluetoothManager bleScaleDeviceDiscovered " + uid + " IP " + serial);
                    bleScaleDiscoverConsumer(uid, serial);

                }
            }, new BluetoothManager.OnBleCharChanged() {
                @Override
                public void bleHrChanged(int hr, String serial) {
                    log("BluetoothManager bleHrChanged " + hr + " IP " + serial);
                    bleHrConsumer(hr, serial);

                }

                @Override
                public void bleBoosterChanged(byte[] data, String serial) {
                    log("BluetoothManager bleBoosterChanged " + data + " IP " + serial);
                    // bleBoosterConsumer(data, serial);
                    receiveCommands(data, serial, true);
                    //Log.e("commManag","Char booster changed "+data);
                }
            }, new BleGattManager.OnConnection() {
                @Override
                public void onConnected(String deviceName) {
                    log("BluetoothManager leGattManager.OnConnection ");
                    onBleConnect(new BleConnection(deviceName));
                }
            });
            bluetoothManager.initBlueTooth();
        }
        //bluetoothManager.initBlueTooth();
        bluetoothManager.scanDevice(bleScanCallback);
        //bluetoothManager.scanDevice();
    }

    private BluetoothManager.BleScanCallback bleScanCallback = new BluetoothManager.BleScanCallback() {
        @Override
        public void onDevice(ScanResult result) {
            if (listener != null)
                listener.onBluetoothDeviceFound(result);
        }
    };

    private void log(String s) {
        Logger.e("CommunicationManager: " + s);
    }

    private void logi(String s) {
        Logger.e("CommunicationManager: " + s);
    }

    public void stopScanning() {
        log("Stopping scanning....");
        stopUDPDiscoveryServer();
        stopBleDiscoveryServer();
    }

    public void stopDiscoveryServers() {
        stopUDPDiscoveryServer();
        stopBleDiscoveryServer();
    }

    public void onDestroy() {
        commRunning = false;
        bluetoothManager = null;
        listener = null;
        udpServer = null;
    }


    public void stopUDPDiscoveryServer() {
        if (udpServer != null) {
            udpServer.stop();
        }
        // udpServer = null;
    }

    public void stopBleDiscoveryServer() {
        if (bluetoothManager != null) {
            bluetoothManager.stopScanDevice();
        }
        // bluetoothManager = null;
    }

    private void broadcastConsumer(byte[] message, InetAddress ip) {
        log("broadcastConsumer encrypted " + Arrays.toString(message));
        debugCommands("broadcastConsumer", message);
        Encryption.mbp_decrypt(message, message.length);
        log("broadcastConsumer decrypted " + Arrays.toString(message));
        debugCommands("broadcastConsumer", message);
        //Encryption.mbp_encrypt(message, message.length);
        logi("broadcastConsumer parse start ----------------- ");
        if (message.length >= MIN_COMMAND_LENGTH + 3) {
            logi("MIN_COMMAND_LENGTH passed");
            for (int i = 0; i < message.length; i++) {
                if (message[i] == 77 && message[i + 1] == 73 && message[i + 2] == 66 && message[i + 3] == 79) {
                    if (message.length > (i + message[i + 6] + 8)) {
                        byte[] command = new byte[message[i + 6] + 2];
                        for (int j = 0; j < message[i + 6] + 2; j++) {
                            command[j] = message[i + 5 + j];
                        }

                        logi("broadcastConsumer checkBoosterDevice " + ip);
                        checkBoosterDevice(command, ip);
                    }
                } else if (message[i] == 77 && message[i + 1] == 66 && message[i + 2] == 82 && message[i + 3] == 88 && message[i + 4] == 76) {
                    if (message.length > (i + message[i + 6] + 8)) {
                        byte[] command = new byte[message[i + 6] + 2];
                        for (int j = 0; j < message[i + 6] + 2; j++) {
                            command[j] = message[i + 5 + j];
                        }

                        logi("broadcastConsumer checkRxlDevice " + ip);
                        checkRxlDevice(command, ip);
                    }
                }
            }
        }
        logi("broadcastConsumer parse ended ----------------- ");
    }

    private void debugCommands(String tag, byte[] msg) {
        if (msg == null) {

            return;
        }
        int count = msg.length - 1;
        if (count == -1)
            return;

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append((char) msg[i]);
            if (i == count) {
                b.append(']').toString();
                break;
            }
            b.append(", ");
        }

        logi("debugCommands " + tag + " - " + new String(msg));
        logi("debugCommands2 " + tag + " : " + b);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            Arrays.stream(msg);
//            StreamSupport.stream(Spliterators.spliterator(msg, Character.toLowerCase('a'), Character.toLowerCase('a'), Spliterator.ORDERED | Spliterator.IMMUTABLE))
//
//            StreamSupport.intStream(new Streams.RangeIntSpliterator(startInclusive, endInclusive, true), false);
//        }
//            char[] chars = IntStream.rangeClosed('a', 'z')
//                   .mapToObj(c -> Character.toString((char) c))
//                   .collect(Collectors.joining())
//                   .toCharArray();
//        }
    }

    private void bleHrDiscoverConsumer(String uid, String serial) {
        add(new Device("", uid, serial, HR_MONITOR));
        SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setStatusConnected(DEVICE_WARNING);
        // if (listener != null)
        //     listener.onDeviceDiscoveredEvent("");
        //EventBus.getDefault().postSticky(new onDeviceDiscoveredEvent(""));
    }

    private void bleScaleDiscoverConsumer(String uid, String serial) {
        add(new Device("", uid, serial, SCALE));
        SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setStatusConnected(DEVICE_WARNING);
        //  if (listener != null)
        //     listener.onDeviceDiscoveredEvent("");
        //EventBus.getDefault().postSticky(new onDeviceDiscoveredEvent(""));
    }

    private void bleBoosterDiscoverConsumer(String uid, String serial) {
        add(new Device("", uid, serial.replace("MIBO-", ""), BLE_STIMULATOR));
        SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setStatusConnected(DEVICE_WARNING);
        // if (listener != null)
        //   listener.onDeviceDiscoveredEvent("");

        //EventBus.getDefault().postSticky(new onDeviceDiscoveredEvent(""));
    }

    private void bleHrConsumer(int hr, String uid) {
        for (Device d : mDiscoveredDevices) {
            if (d.getUid().equals(uid)) {
                if (d.getStatusConnected() != DEVICE_WAITING && d.getStatusConnected() != DEVICE_CONNECTED) {
//                    if (d.getStatusConnected() == DEVICE_DISCONNECTED) {
//                        EventBus.getDefault().postSticky(new ChangeColorEvent(d, d.getUid()));
//                    }
                    d.setStatusConnected(DEVICE_CONNECTED);
                    SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setStatusConnected(DEVICE_CONNECTED);

                    if (listener != null)
                        listener.onConnectionStatus(uid);

                    //EventBus.getDefault().postSticky(new onConnectionStatus(uid));

                }
            }
        }
        if (SessionManager.getInstance().getSession().getCurrentSessionStatus() == 1 ||
                SessionManager.getInstance().getSession().getCurrentSessionStatus() == 2) {
            SessionManager.getInstance().getSession().getUserByHrUid(uid).setHr(hr);
        }
        if (listener != null)
            listener.HrEvent(hr, uid);
        // EventBus.getDefault().postSticky(new HrEvent(hr, uid));
    }


    private synchronized void checkBoosterDevice(byte[] command, InetAddress ip) {
        log("checkNewDevice --- " + true);
        for (Device d : mDiscoveredDevices) {
            if (d.getIp().equals(ip)) {
                log("checkNewDevice --- found1 " + ip);
                return;
            }
        }
        add(new Device("", DataParser.getUID(command), ip, WIFI_STIMULATOR));
    }

    private synchronized void checkRxlDevice(byte[] command, InetAddress ip) {
        log("checkNewDevice --- " + true);
        for (Device d : mDiscoveredDevices) {
            if (d.getIp().equals(ip)) {
                log("checkNewDevice --- found1 " + ip);
                return;
            }
        }
        add(new Device("", DataParser.getUID(command), ip, RXL_WIFI));
    }

    private void checkNewDevice(byte[] command, InetAddress ip) {
        boolean newDevice = true;
        log("checkNewDevice --- " + true);
        for (Device d : mDiscoveredDevices) {
            if (d.getIp().equals(ip)) {
                log("checkNewDevice --- found1 " + ip);
                newDevice = false;
                //return;
            }
            if (newDevice && Device.convetrUiidToString(DataParser.getUID(command)).equals(d.getUid())) {
                log("checkNewDevice --- found1  convetrUiidToString UID matched");
                newDevice = false;
                d.setIp(ip);
                d.setStatusConnected(DEVICE_WARNING);
                SessionManager.getInstance().getSession().getRegisteredDevicebyUid(d.getUid()).setStatusConnected(DEVICE_WARNING);
                //if (listener != null)
                //    listener.onDeviceDiscoveredEvent("");
                //EventBus.getDefault().postSticky(new onDeviceDiscoveredEvent(""));
                // connectDevice(d);
            }
        }
        if (newDevice) {
            log("checkNewDevice new device " + newDevice);
            addDeviceToDiscoveredDevices(new Device("", DataParser.getUID(command), ip, WIFI_STIMULATOR));
            //if (listener != null)
            //  listener.onDeviceDiscoveredEvent("");
            //EventBus.getDefault().postSticky(new onDeviceDiscoveredEvent(""));
        } else {
            log("checkNewDevice --- " + newDevice);
        }


    }

    private synchronized void add(Device device) {
        log("mDiscoveredDevices Add Device............. --- " + device);
        if (mDiscoveredDevices == null)
            mDiscoveredDevices = new ArrayList<>();
        for (Device d : mDiscoveredDevices) {
            if (d.getUid().equals(device.getUid())) {
                if (d.getType() == device.getType()) {
                    return;
                }
            }
        }
        mDiscoveredDevices.add(device);
        if (listener != null)
            listener.onDeviceDiscoveredEvent(device);
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDeviceToDiscoveredDevices(Device device) {
        log("addDeviceToDiscoveredDevices --- " + device);
        boolean newDevice = true;
        for (Device d : mDiscoveredDevices) {
            if (d.getUid().equals(device.getUid())) {
                newDevice = false;
            }
        }
        if (newDevice) {
            add(device);
        }
    }

    public ArrayList<Device> getDiscoveredDevices() {
        return mDiscoveredDevices;
    }

    public void resetDiscoveredDevices() {
        mDiscoveredDevices.clear();
        if (bluetoothManager != null) bluetoothManager.clearDevicesboosterBle();
    }
    public boolean isDiscoveredbyUid(String uid) {
        for (Device d : mDiscoveredDevices) {
            if (d.getUid().equals(uid)) {
                return true;
            }
        }

        return false;
    }


    public void connectDevice(Device device) {
        if (device == null)
            return;
        if (device.getType() == WIFI_STIMULATOR) {
            for (Device d : mDiscoveredDevices) {
                if (d.getUid().equals(device.getUid()) && (d.getType() == WIFI_STIMULATOR)) {
                    connectTCPDevice(d.getIp().getHostAddress(), TCP_PORT, d.getUid());
                    SessionManager.getInstance().getSession().addConnectedDevice(device);
                    device.setStatusConnected(DEVICE_CONNECTING);
                }
            }
        } else if (device.getType() == HR_MONITOR) {
            //stopDiscoveryServers();
            if (bluetoothManager != null)
                bluetoothManager.connectHrGattDevice(device.getUid());
            SessionManager.getInstance().getSession().addConnectedDevice(device);
            device.setStatusConnected(DEVICE_CONNECTING);
        } else if (device.getType() == BLE_STIMULATOR) {
            //stopDiscoveryServers();
            if (bluetoothManager != null)
                bluetoothManager.connectMIBOBoosterGattDevice(device.getUid());
            SessionManager.getInstance().getSession().addConnectedDevice(device);
            device.setStatusConnected(DEVICE_CONNECTING);
        } else if (device.getType() == SCALE) {
            SessionManager.getInstance().getSession().addScale(bluetoothManager.devicesScaleBle.get(0));
        } else if (device.getType() == RXL_WIFI) {
            log("device connect " + device.getIp());
            connectTCPDevice(device.getIp().getHostAddress(), TCP_PORT, device.getUid());
            SessionManager.getInstance().getSession().addConnectedDevice(device);
            device.setStatusConnected(DEVICE_CONNECTING);
        }

    }


    private void connectTCPDevice(String ServerIP, String ServerPort, String Uid) {
        TCPConnect(ServerIP, ServerPort, Uid);

    }

    public void deviceDisconnect(Device device) {
        if (device == null)
            return;
        if (device.getType() == WIFI_STIMULATOR) {
            if (device.getIp() != null) {
                TCPDisconnect(device.getUid());
            }
        } else if (device.getType() == HR_MONITOR) {
            if (bluetoothManager != null)
                bluetoothManager.disconnectHrGattDevice(device.getUid());
        } else if (device.getType() == BLE_STIMULATOR) {
            if (bluetoothManager != null)
                bluetoothManager.disconnectMIBOBoosterGattDevice(device.getUid());
        }
    }

    public void TCPDisconnect(String Uid) {
//        for(TCPClient t : tcpClients) {
//            if(t.getUid().equals(Uid)){
//                t.stopClient();
//                this.tcpClients.remove(t);
//            }
//        }
        if (tcpClients != null) {
            int aux = -1;
            for (TCPClient t : tcpClients) {
                if (t.getUid().equals(Uid)) {
                    aux = tcpClients.indexOf(t);
                    t.stopClient();
                }
            }
            if (aux != -1) {
                tcpClients.remove(aux);
            }
        }
    }

    private void TCPConnect(String ip, String port, String Uid) {
        //create a TCPClient object
        Logger.e("CommunicationManager TCPConnect ip " + ip + " , port " + port);
        boolean newDevice = true;
        for (TCPClient t : tcpClients) {
            if (t.getServerIp().equals(ip)) {
                newDevice = false;
            }
        }
        if (newDevice) {
            tcpClients.add(new TCPClient(ip, Integer.parseInt(port), Uid, new TCPClient.OnMessageReceived() {
                @Override
                public void messageReceived(byte[] message, String uid) {
                    receiveCommands(message, uid, false);
                }
            }));
            for (TCPClient t : tcpClients) {
                if (t.getServerIp().equals(ip)) {
                    Log.e("tcpcon", "run");
                    t.run();
                }
            }
        }

    }

    private void receiveCommands(byte[] message, String uid, boolean bluetooth) {//lento?
        Encryption.mbp_decrypt(message, message.length);
        log("receiveCommands msg " + Arrays.toString(message) + " : UID " + uid);
        log("receiveCommands char " + Arrays.toString(new String(message).toCharArray()) + " : UID " + uid);
        if (message.length >= MIN_COMMAND_LENGTH) {
            try {
                for (int i = 0; i < message.length; i++) {
                    if (message.length > i + 4) {
                        if (message[i] == 77 && message[i + 1] == 73 &&////cooutofbound
                                message[i + 2] == 66 && message[i + 3] == 79) {
                            if (message.length > (i + message[i + 6] + 8)) {
                                byte[] command = new byte[message[i + 6] + 2];
                                for (int j = 0; j < message[i + 6] + 2; j++) {
                                    command[j] = message[i + 5 + j];
                                }
                                parseCommands2(command, uid);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log("receiveCommands error " + e.getMessage());
            }
        }
    }


    private void parseCommands2(byte[] command, String uid) {
        log("parseCommands msg " + Arrays.toString(command) + " : UID " + uid);
        if (listener != null)
            listener.onCommandReceived(DataParser.getCommand(command), command, uid);
    }

    private void parseCommands(byte[] command, String uid) {
        //Log.e("commManager", "consummer msg uid: " + uid);
        log("parseCommands msg " + Arrays.toString(command) + " : UID " + uid);

        switch (DataParser.getCommand(command)) {
            case COMMAND_PING_RESPONSE:
                log("parseCommands COMMAND_PING_RESPONSE");
                break;
            case COMMAND_DEVICE_STATUS_RESPONSE:
                log("parseCommands COMMAND_DEVICE_STATUS_RESPONSE");
                for (Device d : mDiscoveredDevices) {
                    if (d.getUid().equals(uid)) {
                        d.setBatteryLevel(DataParser.getStatusBattery(command));
                        d.setSignalLevel(DataParser.getStatusSignal(command));
                        SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setBatteryLevel(DataParser.getStatusBattery(command));
                        SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setSignalLevel(DataParser.getStatusSignal(command));
                        if (listener != null)
                            listener.DeviceStatusEvent(d.getUid());
                        //EventBus.getDefault().postSticky(new DeviceStatusEvent(d.getUid()));
                        if (d.getStatusConnected() != DEVICE_WAITING && d.getStatusConnected() != DEVICE_CONNECTED) {
                            if (d.getStatusConnected() == DEVICE_DISCONNECTED) {
                                if (listener != null)
                                    listener.ChangeColorEvent(d, d.getUid());
                                //EventBus.getDefault().postSticky(new ChangeColorEvent(d, d.getUid()));
                            }
                            d.setStatusConnected(DEVICE_CONNECTED);
                            SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setStatusConnected(DEVICE_CONNECTED);

                            if (listener != null)
                                listener.onConnectionStatus(uid);
                            //EventBus.getDefault().postSticky(new onConnectionStatus(uid));
                        } else {
                            d.setStatusConnected(DEVICE_CONNECTED);
                            SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setStatusConnected(DEVICE_CONNECTED);
                        }
                        if (SessionManager.getInstance().getSession().getCurrentSessionStatus() == 1 ||
                                SessionManager.getInstance().getSession().getCurrentSessionStatus() == 2) {
                            if (SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setNewDeviceChannelAlarms(
                                    DataParser.getChannelAlarms(command))) {
                                AlarmManager.getInstance().getAlarms().AddDeviceChannelAlarm(
                                        SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).getDeviceChannelAlarms(),
                                        d.getUid());

                                if (listener != null)
                                    listener.onAlarmEvent();
                                //EventBus.getDefault().postSticky(new onAlarmEvent());
                            }
                        }
                        SessionManager.getInstance().getSession().checkDeviceStatus(DataParser.getStatusFlags(command), uid);
                        // Log.e("commManager", "signal: " + DataParser.getStatusSignal(command)+" bat:"+DataParser.getStatusBattery(command));
                    }

                }
                //  Log.e("commManager", "Receiver PING");
                break;
            case COMMAND_FIRMWARE_REVISION_RESPONSE:
                log("parseCommands COMMAND_FIRMWARE_REVISION_RESPONSE");
                break;
            case COMMAND_SET_DEVICE_COLOR_RESPONSE:
                log("parseCommands COMMAND_SET_DEVICE_COLOR_RESPONSE");
                break;
            case COMMAND_SET_COMMON_STIMULATION_PARAMETERS_RESPONSE:
                log("parseCommands COMMAND_SET_COMMON_STIMULATION_PARAMETERS_RESPONSE");
                //Log.e("commManager", "Receiver PROGRAM");
                break;
            case COMMAND_SET_MAIN_LEVEL_RESPONSE:
                log("parseCommands COMMAND_SET_MAIN_LEVEL_RESPONSE");
                if (listener != null)
                    listener.GetMainLevelEvent(DataParser.getMainLevel(command), uid);
                //EventBus.getDefault().postSticky(new GetMainLevelEvent(DataParser.getMainLevel(command), uid));
                SessionManager.getInstance().getSession().getUserByBoosterUid(uid).setMainLevel(DataParser.getMainLevel(command));
                //EventBus.getDefault().postSticky(new SendMainLevelEvent(1,uid));
                break;
            case COMMAND_SET_CHANNELS_LEVELS_RESPONSE:
                log("parseCommands COMMAND_SET_CHANNELS_LEVELS_RESPONSE");
                if (listener != null)
                    listener.GetLevelsEvent(uid);

                //EventBus.getDefault().postSticky(new GetLevelsEvent(uid));
                //  Log.e("commManager", "Receiver LEVELS");
                break;
            case COMMAND_START_CURRENT_CYCLE_RESPONSE:
                log("parseCommands COMMAND_START_CURRENT_CYCLE_RESPONSE");
                SessionManager.getInstance().getUserSession().getDevice().setIsStarted(true);
                if (listener != null)
                    listener.GetLevelsEvent(uid);
                //EventBus.getDefault().postSticky(new DevicePlayPauseEvent(uid));
                //Log.e("commManager", "Receiver START");
                break;
            case COMMAND_PAUSE_CURRENT_CYCLE_RESPONSE:
                log("parseCommands COMMAND_PAUSE_CURRENT_CYCLE_RESPONSE");
                SessionManager.getInstance().getUserSession().getDevice().setIsStarted(false);
                //SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setIsStarted(false);
                if (listener != null)
                    listener.GetLevelsEvent(uid);
                //EventBus.getDefault().postSticky(new DevicePlayPauseEvent(uid));
                //Log.e("commManager", "Receiver PAUSE");
                break;
            case COMMAND_RESET_CURRENT_CYCLE_RESPONSE:
                log("parseCommands COMMAND_RESET_CURRENT_CYCLE_RESPONSE");
                //Log.e("commManager", "Receiver RESET");
                break;
            case ASYNC_PROGRAM_STATUS:
                log("parseCommands ASYNC_PROGRAM_STATUS");
                if (listener != null)
                    listener.ProgramStatusEvent(DataParser.getProgramStatusTime(command), DataParser.getProgramStatusAction(command), DataParser.getProgramStatusPause(command),
                            DataParser.getProgramStatusCurrentBlock(command), DataParser.getProgramStatusCurrentProgram(command), uid);
                SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setDeviceSessionTimer(DataParser.getProgramStatusTime(command));
                //EventBus.getDefault().postSticky(new ProgramStatusEvent(DataParser.getProgramStatusTime(command),
                //  DataParser.getProgramStatusAction(command), DataParser.getProgramStatusPause(command),
                //    DataParser.getProgramStatusCurrentBlock(command), DataParser.getProgramStatusCurrentProgram(command), uid));
                SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setDeviceSessionTimer(DataParser.getProgramStatusTime(command));

                //  Log.e("commManager", "Receiver ASYNC PROGRAM STATUS");
                break;
            case COMMAND_ASYNC_SET_MAIN_LEVEL:
                log("parseCommands COMMAND_ASYNC_SET_MAIN_LEVEL");
                if (listener != null)
                    listener.GetMainLevelEvent(DataParser.getMainLevelAsync(command), uid);
                //EventBus.getDefault().postSticky(new GetMainLevelEvent(DataParser.getMainLevelAsync(command), uid));
                SessionManager.getInstance().getSession().getUserByBoosterUid(uid).setMainLevel(DataParser.getMainLevelAsync(command));
                //Log.e("commManager", "Receiver ASYNC MAINLEVEL");
                break;
            case COMMAND_ASYNC_PAUSE:
                log("parseCommands COMMAND_ASYNC_PAUSE");
                SessionManager.getInstance().getUserSession().getDevice().setIsStarted(false);
                if (listener != null)
                    listener.DevicePlayPauseEvent(uid);

                //  EventBus.getDefault().postSticky(new DevicePlayPauseEvent(uid));
                //Log.e("commManager", "Receiver ASYNC Pause");
                break;
            case COMMAND_ASYNC_START:
                log("parseCommands COMMAND_ASYNC_START");
                SessionManager.getInstance().getUserSession().getDevice().setIsStarted(true);
                if (listener != null)
                    listener.DevicePlayPauseEvent(uid);
                //EventBus.getDefault().postSticky(new DevicePlayPauseEvent(uid));
                //Log.e("commManager", "Receiver ASYNC Pause");
                break;
            default:
                log("parseCommands DEFAULTS");
                break;
        }
    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSendProgramEvent(SendProgramEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendProgram(1, 0, event.getProgram()));
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                DataParser.sendProgram(1, 0, event.getProgram()));

        //tcpClients.get(0).sendMessage(DataParser.sendProgram( event.getProgram()));
        Log.e("CommManager", "Program EVENT");

    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSendProgramChangesHotEvent(SendProgramChangesHotEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendProgramOnHot(1, 0, event.getProgram()));
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                DataParser.sendProgramOnHot(1, 0, event.getProgram()));

        //tcpClients.get(0).sendMessage(DataParser.sendProgram( event.getProgram()));
        Log.e("CommManager", "Program EVENT");

    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSendCircuitEvent(SendCircuitEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                sendCircuitTCP(event.getCircuit(), t);
            }
        }
        sendCircuitGATT(event.getCircuit(), event.getUid());

        //tcpClients.get(0).sendMessage(DataParser.sendProgram( event.getProgram()));
        Log.e("CommManager", "Program EVENT");
    }

    private void sendCircuitTCP(Circuit circuit, TCPClient TCPSocket) {
        int index = 0;
        for (Program p : circuit.getPrograms()) {
            TCPSocket.sendMessage(DataParser.sendProgram(circuit.getPrograms().length, index, p));
            index++;
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendCircuitGATT(Circuit circuit, String Uid) {
        int index = 0;
        for (Program p : circuit.getPrograms()) {
            if (bluetoothManager != null)
                bluetoothManager.sendToMIBOBoosterGattDevice(Uid,
                    DataParser.sendProgram(circuit.getPrograms().length, index, p));
            index++;
        }
    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onChangeColorEvent(ChangeColorEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                    DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
        // tcpClients.get(0).sendMessage(DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
        Log.e("CommManager", "Color EVENT");

    }


    public void onChangeColorEventRxl(ChangeColorEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                    DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
        // tcpClients.get(0).sendMessage(DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
        Log.e("CommManager", "Color EVENT");

    }

    // @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDeviceSearchEvent(DeviceSearchEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendSearchCommand());
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                    DataParser.sendSearchCommand());
        // tcpClients.get(0).sendMessage(DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
        Log.e("CommManager", "Search EVENT");

    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMainLevelEvent(SendMainLevelEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendMain(event.getLevel()), "onMainLevelEvent");
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                    DataParser.sendMain(event.getLevel()));
        // tcpClients.get(0).sendMessage(DataParser.sendMain( event.getLevel()));
        Log.e("CommManager", "MainLevel EVENT");

    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onChannelsLevelEvent(SendChannelsLevelEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        log("onChannelsLevelEvent "+ Arrays.toString(event.getLevels()));
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendLevels(event.getLevels()), "onChannelsLevelEvent");
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                    DataParser.sendLevels(event.getLevels()));
        //tcpClients.get(0).sendMessage(DataParser.sendLevels( event.getLevels()));
        Log.e("CommManager", "Channels EVENT");

    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDevicePlayEvent(SendDevicePlayEvent event) {
        log("SendDevicePlayEvent isWifi " + isWifi);
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            log(t.getUid() + " : " + event.getUid());
            if (t.getUid().equals(event.getUid())) {
                log("onDevicePlayEvent UID Matched");
                t.sendMessage(DataParser.sendStart());
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(), DataParser.sendStart());

//        if (isWifi) {
//            for (TCPClient t : tcpClients) {
//                if (t.getUid().equals(event.getUid())) {
//                    t.sendMessage(DataParser.sendStart());
//                }
//            }
//        } else {
//            bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(), DataParser.sendStart());
//        }
        //tcpClients.get(0).sendMessage(DataParser.sendStart());
        Log.e("CommManager", "Play EVENT");

    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDeviceRestartEvent(SendDeviceStartEvent event) {
        onDeviceStartEvent(event);
    }
    public void onDeviceStartEvent(SendDeviceStartEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendReStart());
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                DataParser.sendReStart());
        //tcpClients.get(0).sendMessage(DataParser.sendStart());
        log("RESTART EVENT");

    }


    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDeviceStopEvent(SendDeviceStopEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendStop());
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                DataParser.sendStop());
        //tcpClients.get(0).sendMessage(DataParser.sendStop());
        Log.e("CommManager", "Stop EVENT");

    }

//    //@Subscribe
//    public void onNoSubscriberEvent(NoSubscriberEvent event) {
//        //EventBus.getDefault().removeStickyEvent(event);
//    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onBleConnect(BleConnection event) {
        log("onBleConnect discoered " + event.getname());
        if (event.getname() != null) {
            if (event.getname().contains("HW")) {
                event.getname().replace("HW", "");
            }
            if (event.getname().contains("MIBO-")) {
                event.getname().replace("MIBO-", "");
            }
        }

        for (Device d : mDiscoveredDevices) {
            if (d.getUid().equals(event.getname())) {

                d.setStatusConnected(DEVICE_CONNECTED);
                SessionManager.getInstance().getSession().getRegisteredDevicebyUid(event.getname()).setStatusConnected(DEVICE_CONNECTED);
                if (listener != null)
                    listener.onConnectionStatus(event.getname());
                //EventBus.getDefault().postSticky(new onConnectionStatus(event.getname()));


            }
        }
    }

    public @Nullable
    ArrayList<TCPClient> getTcpClients() {
        return tcpClients;
    }

    private CommunicationListener listener;

    public CommunicationListener getListener() {
        return listener;
    }

    public CommunicationManager setListener(CommunicationListener listener) {
        this.listener = listener;
        return this;
    }

}
