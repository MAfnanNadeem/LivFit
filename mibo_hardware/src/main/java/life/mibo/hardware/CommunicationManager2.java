package life.mibo.hardware;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

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
import life.mibo.hardware.network.TCPClientNio;
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
import static life.mibo.hardware.models.DeviceTypes.SCALE;
import static life.mibo.hardware.models.DeviceTypes.WIFI_STIMULATOR;

/**
 * Created by Sumeet Gehi on 17/12/2019.
 * <p>
 * Trying to reimplement Fernando's API and Logic with Java Nio based thread driven socket channels and new Android APIs
 */

public class CommunicationManager2 {

    private static CommunicationManager2 manager;
    //public ArrayList<TCPClient> tcpClients = new ArrayList<>();
    public UDPServer udpServer;
    public BluetoothManager bluetoothManager;
    //public ScaleManager scaleManager;
    public ArrayList<Device> mDiscoveredDevices = new ArrayList<>();
    private Activity activity;
    boolean commRunning = true;
    private Thread pingThread;

    private CommunicationManager2() {
        //EventBus.getDefault().register(this);
        if (manager != null) {
            throw new RuntimeException("getInstance() to get the instance of this class");
        }
        pingThread = new Thread(new PingThread());
        pingThread.start();
    }

    private CommunicationManager2(CommunicationListener listener) {
        this();
        setListener(listener);
    }

    public static CommunicationManager2 getInstance() {
        if (manager == null) {
            manager = new CommunicationManager2();
        }
        return manager;
    }

    public static CommunicationManager2 getInstance(CommunicationListener listener) {
        if (manager == null) {
            manager = new CommunicationManager2(listener);
        }
        if (manager.listener == null)
            manager.listener = listener;
        return manager;
    }

    public void setContext(Activity activity) {
        this.activity = activity;

    }

    public Activity getContext() {
        return activity;

    }

    public void onDestroy() {
        commRunning = false;
    }

    class PingThread implements Runnable {
        @Override
        public void run() {
            while (commRunning) {
                try {
                    Thread.sleep(4000);
                    HashMap<String, TCPClientNio.Client> map = TCPClientNio.get().getMap();
                    for (HashMap.Entry<String, TCPClientNio.Client> client : map.entrySet()) {
                        client.getValue().sendMessage(DataParser.sendGetStatus(false));
                        pingSentDevice(client.getKey());
                        //client.getValue().sendMessage(message);
                    }
                    // TCPClientNio.get().sendMessage(DataParser.sendGetStatus());
//                    for (TCPClient t : tcpClients) {
//                        t.sendMessage(DataParser.sendGetStatus());
//                        pingSentDevice(t.getUid());
//                        // Log.e("commManag", "send ping");
//                    }
                    if (bluetoothManager != null) {
                        for (BluetoothDevice d : bluetoothManager.getConnectedBleDevices()) {
                            if (d.getName() != null) {
                                if (d.getName().contains("MIBO-")) {
                                    bluetoothManager.sendPingToBoosterGattDevice(DataParser.sendGetStatus(false), d);
                                    pingSentDevice(d.getName().replace("MIBO-", ""));
                                }
                                if ((d.getName().contains("HW") || d.getName().contains("Geonaute"))) {
                                    bluetoothManager.sendPingToBoosterGattDevice(DataParser.sendGetStatus(false), d);
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
    public synchronized void startScanning(boolean isBluetooth) {
        if (isBluetooth) {
            if (bluetoothManager == null) {
                bluetoothManager = new BluetoothManager(activity, new BluetoothManager.OnBleDeviceDiscovered() {
                    @Override
                    public void bleHrDeviceDiscovered(String uid, String serial) {
                        log("bleHrDeviceDiscovered " + uid + " IP " + serial);
                        bleHrDiscoverConsumer(uid, serial);
                    }

                    @Override
                    public void bleBoosterDeviceDiscovered(String uid, String serial) {
                        log("bleBoosterDeviceDiscovered " + uid + " IP " + serial);
                        if (!SessionManager.getInstance().getSession().isBoosterMode())// true wifi mode, false ble mode
                            bleBoosterDiscoverConsumer(uid, serial);
                    }

                    @Override
                    public void bleScaleDeviceDiscovered(String uid, String serial) {
                        log("bleScaleDeviceDiscovered " + uid + " IP " + serial);
                        bleScaleDiscoverConsumer(uid, serial);
                    }
                }, new BluetoothManager.OnBleCharChanged() {
                    @Override
                    public void bleHrChanged(int hr, String serial) {
                        log("bleHrChanged " + hr + " IP " + serial);
                        bleHrConsumer(hr, serial);
                    }

                    @Override
                    public void bleBoosterChanged(byte[] data, String serial) {
                        log("bleBoosterChanged " + data + " IP " + serial);
                        // bleBoosterConsumer(data, serial);
                        messageConsumer(data, serial);
                    }
                }
                );
                bluetoothManager.initBlueTooth();
            }
            bluetoothManager.reset();
            bluetoothManager.scanDevice();
        } else {
            if (udpServer == null) {
                udpServer = new UDPServer(new UDPServer.OnBroadcastReceived() {
                    @Override
                    public void broadcastReceived(byte[] msg, InetAddress ip) {
                        if (listener != null)
                            listener.udpDeviceReceiver(msg, ip);
                        log("udpDeviceReceiver " + msg + " IP " + ip);

                        SessionManager manager = SessionManager.getInstance();
                        if (manager.getSession() != null) {
                            if (manager.getSession().isBoosterMode()) {// true wifi mode
                                broadcastConsumer(msg, ip);
                            } else {
                                log("Session Manager No Boosted Mode " + manager);
                            }
                        } else {
                            log("Session Manager getSession is NULL " + manager);
                        }
                    }
                });
            }
            udpServer.start(activity);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public synchronized void startScanning(final Activity context) {
        if (udpServer == null) {
            udpServer = new UDPServer(new UDPServer.OnBroadcastReceived() {
                @Override
                public void broadcastReceived(byte[] msg, InetAddress ip) {
                    if (listener != null)
                        listener.udpDeviceReceiver(msg, ip);
                    log("udpDeviceReceiver " + msg + " IP " + ip);
                    // TODO remove later
                    broadcastConsumer(msg, ip);
                    SessionManager manager = SessionManager.getInstance();
                    if (manager.getSession() != null) {
                        if (manager.getSession().isBoosterMode()) {// true wifi mode
                            broadcastConsumer(msg, ip);
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

        //TODO: Change to not overwrite the current one if its initialized and only start discovery
        if (bluetoothManager == null) {
            bluetoothManager = new BluetoothManager(activity, new BluetoothManager.OnBleDeviceDiscovered() {
                @Override
                public void bleHrDeviceDiscovered(String uid, String serial) {
                    log("bleHrDeviceDiscovered " + uid + " IP " + serial);
                    bleHrDiscoverConsumer(uid, serial);

                }

                @Override
                public void bleBoosterDeviceDiscovered(String uid, String serial) {
                    log("bleBoosterDeviceDiscovered " + uid + " IP " + serial);
                    if (!SessionManager.getInstance().getSession().isBoosterMode())// true wifi mode, false ble mode
                        bleBoosterDiscoverConsumer(uid, serial);

                }

                @Override
                public void bleScaleDeviceDiscovered(String uid, String serial) {
                    log("bleScaleDeviceDiscovered " + uid + " IP " + serial);
                    bleScaleDiscoverConsumer(uid, serial);

                }
            }, new BluetoothManager.OnBleCharChanged() {
                @Override
                public void bleHrChanged(int hr, String serial) {
                    log("bleHrChanged " + hr + " IP " + serial);
                    bleHrConsumer(hr, serial);

                }

                @Override
                public void bleBoosterChanged(byte[] data, String serial) {
                    log("bleBoosterChanged " + data + " IP " + serial);
                    // bleBoosterConsumer(data, serial);
                    messageConsumer(data, serial);
                    //Log.e("commManag","Char booster changed "+data);
                }
            }
            );
            bluetoothManager.initBlueTooth();
        }
        //bluetoothManager.initBlueTooth();
        bluetoothManager.scanDevice(bleScanCallback);
        //bluetoothManager.scanDevice();

    }

    BluetoothManager.BleScanCallback bleScanCallback = new BluetoothManager.BleScanCallback() {
        @Override
        public void onDevice(ScanResult result) {
            if (listener != null)
                listener.onBluetoothDeviceFound(result);
        }
    };

    public void stopScanning() {
        log("Stopping scanning....");
        stopUDPDiscoveryServer();
        stopBleDiscoveryServer();
    }

    public void stopUDPDiscoveryServer() {
        if (udpServer != null) {
            udpServer.stop();
        }
        udpServer = null;
    }

    public void stopBleDiscoveryServer() {
        if (bluetoothManager != null) {
            bluetoothManager.stopScanDevice();
        }

    }

    private void broadcastConsumer(byte[] message, InetAddress ip) {
        Logger.e("broadcastConsumer Manager is NULL " + manager);

        Encryption.mbp_decrypt(message, message.length);
        //Encryption.mbp_encrypt(message, message.length);
        if (message.length >= MIN_COMMAND_LENGTH + 3) {
            for (int i = 0; i < message.length; i++) {
                if (message[i] == 77 && message[i + 1] == 73 &&
                        message[i + 2] == 66 && message[i + 3] == 79) {
                    if (message.length > (i + message[i + 6] + 8)) {
                        byte[] command = new byte[message[i + 6] + 2];
                        for (int j = 0; j < message[i + 6] + 2; j++) {
                            command[j] = message[i + 5 + j];
                        }

                        log("broadcastConsumer checkNewDevice " + ip);
                        checkNewDevice2(command, ip);
                    }
                }
            }
        }
    }

    private void bleHrDiscoverConsumer(String uid, String serial) {
        add(new Device("", uid, serial, HR_MONITOR));
        SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setStatusConnected(DEVICE_WARNING);
        if (listener != null)
            listener.onDeviceDiscoveredEvent("");
        //EventBus.getDefault().postSticky(new onDeviceDiscoveredEvent(""));
    }

    private void bleScaleDiscoverConsumer(String uid, String serial) {
        add(new Device("", uid, serial, SCALE));
        SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setStatusConnected(DEVICE_WARNING);
        if (listener != null)
            listener.onDeviceDiscoveredEvent("");
        //EventBus.getDefault().postSticky(new onDeviceDiscoveredEvent(""));
    }

    private void bleBoosterDiscoverConsumer(String uid, String serial) {
        add(new Device("", uid, serial.replace("MIBO-", ""), BLE_STIMULATOR));
        SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setStatusConnected(DEVICE_WARNING);
        if (listener != null)
            listener.onDeviceDiscoveredEvent("");

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


    private synchronized void checkNewDevice2(byte[] command, InetAddress ip) {
        log("checkNewDevice --- " + true);
        for (Device d : mDiscoveredDevices) {
            if (d.getIp().equals(ip)) {
                log("checkNewDevice --- found1 " + ip);
                return;
            }
        }
        add(new Device("", DataParser.getUID(command), ip, WIFI_STIMULATOR));
    }


    private void checkNewDevice(byte[] command, InetAddress ip) {
        boolean newDevice = true;

        for (int i = 0; i < mDiscoveredDevices.size(); i++) {//Device d : mDiscoveredDevices
            if (mDiscoveredDevices.get(i).getIp().equals(ip)) {
                newDevice = false;
            }
            if (newDevice && Device.convetrUiidToString(DataParser.getUID(command)).equals(mDiscoveredDevices.get(i).getUid())) {
                newDevice = false;
                mDiscoveredDevices.get(i).setIp(ip);
                mDiscoveredDevices.get(i).setStatusConnected(DEVICE_WARNING);
                SessionManager.getInstance().getSession().getRegisteredDevicebyUid(mDiscoveredDevices.get(i).getUid()).setStatusConnected(DEVICE_WARNING);
                if (listener != null)
                    listener.onDeviceDiscoveredEvent("");
                //EventBus.getDefault().postSticky(new onDeviceDiscoveredEvent(""));
                //    connectDevice(d);
            }
        }
        if (newDevice) {
            log("new device " + newDevice);
            addDeviceToDiscoveredDevices(new Device("", DataParser.getUID(command), ip, WIFI_STIMULATOR));
            if (listener != null)
                listener.onDeviceDiscoveredEvent("");
            //EventBus.getDefault().postSticky(new onDeviceDiscoveredEvent(""));
        }


    }

    public void add(Device device) {
        log("mDiscoveredDevices Add Device............. --- " + device);
        if (mDiscoveredDevices == null)
            mDiscoveredDevices = new ArrayList<>();
        mDiscoveredDevices.add(device);
        if (listener != null)
            listener.onDeviceDiscoveredEvent(device);
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Device> getDiscoveredDevices() {
        return mDiscoveredDevices;
    }

    public void resetDiscoveredDevices() {
        mDiscoveredDevices.clear();
        bluetoothManager.clearDevicesboosterBle();
    }

    public void addDeviceToDiscoveredDevices(Device device) {
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

    public boolean isDiscoveredbyUid(String uid) {
        for (Device d : mDiscoveredDevices) {
            if (d.getUid().equals(uid)) {
                return true;
            }
        }

        return false;
    }


    public void connectDevice(Device device) {
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
            bluetoothManager.connectHrGattDevice(device.getUid());
            SessionManager.getInstance().getSession().addConnectedDevice(device);
            device.setStatusConnected(DEVICE_CONNECTING);
        } else if (device.getType() == BLE_STIMULATOR) {
            //stopDiscoveryServers();
            bluetoothManager.connectMIBOBoosterGattDevice(device.getUid());
            SessionManager.getInstance().getSession().addConnectedDevice(device);
            device.setStatusConnected(DEVICE_CONNECTING);
        } else if (device.getType() == SCALE) {
            SessionManager.getInstance().getSession().addScale(bluetoothManager.devicesScaleBle.get(0));
        }

    }


    private void connectTCPDevice(String ServerIP, String ServerPort, String Uid) {
        TCPConnectNio(ServerIP, ServerPort, Uid);

    }

    public void deviceDisconnect(Device device) {
        if (device.getType() == WIFI_STIMULATOR) {
            if (device.getIp() != null) {
                TCPDisconnect(device.getUid());
            }
        } else if (device.getType() == HR_MONITOR) {
            bluetoothManager.disconnectHrGattDevice(device.getUid());
        } else if (device.getType() == BLE_STIMULATOR) {
            bluetoothManager.disconnectMIBOBoosterGattDevice(device.getUid());
        }
    }

    public void TCPDisconnect() {
        TCPClientNio.get().stopClient();
    }

    public void TCPDisconnect(String Uid) {
//        for(TCPClient t : tcpClients) {
//            if(t.getUid().equals(Uid)){
//                t.stopClient();
//                this.tcpClients.remove(t);
//            }
//        }
//        if (tcpClients != null) {
//            int aux = -1;
//            for (TCPClient t : tcpClients) {
//                if (t.getUid().equals(Uid)) {
//                    aux = tcpClients.indexOf(t);
//                    t.stopClient();
//                }
//            }
//            if (aux != -1) {
//                tcpClients.remove(aux);
//            }
//        }

        TCPClientNio.get().stopClient(Uid);
    }

    private void TCPConnect(String ip, String port, String Uid) {
        //create a TCPClient object
//        Logger.e("CommunicationManager TCPConnect ip " + ip + " , port " + port);
//        boolean newDevice = true;
//        for (TCPClient t : tcpClients) {
//            if (t.getServerIp().equals(ip)) {
//                newDevice = false;
//            }
//        }
//        if (newDevice) {
//            tcpClients.add(new TCPClient(ip, Integer.parseInt(port), Uid, new TCPClient.OnMessageReceived() {
//                @Override
//                public void messageReceived(byte[] message, String uid) {
//                    messageConsumer(message, uid);
//                }
//            }));
//
//            for (TCPClient t : tcpClients) {
//                if (t.getServerIp().equals(ip)) {
//                    log("run");
//                    t.run();
//                }
//            }
//        }

    }

    private void messageConsumer(byte[] message, String uid) {//lento?
        Encryption.mbp_decrypt(message, message.length);
        if (message.length >= MIN_COMMAND_LENGTH) {
            // log("consummer lenght "+message.length);
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
                                receiveCommandEventDispatcher(command, uid);
                            }
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }


    private void receiveCommandEventDispatcher(byte[] command, String uid) {
        //log("consummer msg uid: " + uid);
        switch (DataParser.getCommand(command)) {
            case COMMAND_PING_RESPONSE:
                break;
            case COMMAND_DEVICE_STATUS_RESPONSE:
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
                        // log("signal: " + DataParser.getStatusSignal(command)+" bat:"+DataParser.getStatusBattery(command));
                    }

                }
                //  log("Receiver PING");
                break;
            case COMMAND_FIRMWARE_REVISION_RESPONSE:
                log("Receiver FIRMWARE");
                break;
            case COMMAND_SET_DEVICE_COLOR_RESPONSE:
                log("Receiver COLOR");
                break;
            case COMMAND_SET_COMMON_STIMULATION_PARAMETERS_RESPONSE:
                log("Receiver PROGRAM");
                break;
            case COMMAND_SET_MAIN_LEVEL_RESPONSE:
                if (listener != null)
                    listener.GetMainLevelEvent(DataParser.getMainLevel(command), uid);
                //EventBus.getDefault().postSticky(new GetMainLevelEvent(DataParser.getMainLevel(command), uid));
                SessionManager.getInstance().getSession().getUserByBoosterUid(uid).setMainLevel(DataParser.getMainLevel(command));
                log("Receiver MAINLEVEL" + uid);
                //EventBus.getDefault().postSticky(new SendMainLevelEvent(1,uid));
                break;
            case COMMAND_SET_CHANNELS_LEVELS_RESPONSE:
                if (listener != null)
                    listener.GetLevelsEvent(uid);

                //EventBus.getDefault().postSticky(new GetLevelsEvent(uid));
                //  log("Receiver LEVELS");
                break;
            case COMMAND_START_CURRENT_CYCLE_RESPONSE:
                SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setIsStarted(true);
                if (listener != null)
                    listener.GetLevelsEvent(uid);
                //EventBus.getDefault().postSticky(new DevicePlayPauseEvent(uid));
                log("Receiver START");
                break;
            case COMMAND_PAUSE_CURRENT_CYCLE_RESPONSE:
                SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setIsStarted(false);
                if (listener != null)
                    listener.GetLevelsEvent(uid);
                //EventBus.getDefault().postSticky(new DevicePlayPauseEvent(uid));
                log("Receiver PAUSE");
                break;
            case COMMAND_RESET_CURRENT_CYCLE_RESPONSE:
                log("Receiver RESET");
                break;
            case ASYNC_PROGRAM_STATUS:
                if (listener != null)
                    listener.ProgramStatusEvent(DataParser.getProgramStatusTime(command), DataParser.getProgramStatusAction(command), DataParser.getProgramStatusPause(command),
                            DataParser.getProgramStatusCurrentBlock(command), DataParser.getProgramStatusCurrentProgram(command), uid);
                SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setDeviceSessionTimer(DataParser.getProgramStatusTime(command));
                //EventBus.getDefault().postSticky(new ProgramStatusEvent(DataParser.getProgramStatusTime(command),
                //  DataParser.getProgramStatusAction(command), DataParser.getProgramStatusPause(command),
                //    DataParser.getProgramStatusCurrentBlock(command), DataParser.getProgramStatusCurrentProgram(command), uid));
                SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setDeviceSessionTimer(DataParser.getProgramStatusTime(command));

                //  log("Receiver ASYNC PROGRAM STATUS");
                break;
            case COMMAND_ASYNC_SET_MAIN_LEVEL:
                if (listener != null)
                    listener.GetMainLevelEvent(DataParser.getMainLevelAsync(command), uid);
                //EventBus.getDefault().postSticky(new GetMainLevelEvent(DataParser.getMainLevelAsync(command), uid));
                SessionManager.getInstance().getSession().getUserByBoosterUid(uid).setMainLevel(DataParser.getMainLevelAsync(command));
                log("Receiver ASYNC MAINLEVEL");
                break;
            case COMMAND_ASYNC_PAUSE:
                SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setIsStarted(false);
                if (listener != null)
                    listener.DevicePlayPauseEvent(uid);

                //  EventBus.getDefault().postSticky(new DevicePlayPauseEvent(uid));
                log("Receiver ASYNC Pause");
                break;
            case COMMAND_ASYNC_START:
                SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setIsStarted(true);
                if (listener != null)
                    listener.DevicePlayPauseEvent(uid);
                //EventBus.getDefault().postSticky(new DevicePlayPauseEvent(uid));
                log("Receiver ASYNC Pause");
                break;
            default:
                break;
        }
    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSendProgramEvent(SendProgramEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        try {
            TCPClientNio.get().sendMessage(event.getUid(), DataParser.sendProgram(1, 0, event.getProgram()));
        } catch (Exception e) {
            e.printStackTrace();
            log("onSendProgramEvent error ");
        }
//        for (TCPClient t : tcpClients) {
//            if (t.getUid().equals(event.getUid())) {
//                t.sendMessage(DataParser.sendProgram(1, 0, event.getProgram()));
//            }
//        }
        bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                DataParser.sendProgram(1, 0, event.getProgram()));

        //tcpClients.get(0).sendMessage(DataParser.sendProgram( event.getProgram()));
        log("Program EVENT");

    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSendProgramChangesHotEvent(SendProgramChangesHotEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        try {
            TCPClientNio.get().sendMessage(event.getUid(), DataParser.sendProgramOnHot(1, 0, event.getProgram()));
        } catch (Exception e) {
            e.printStackTrace();
            log("onSendProgramChangesHotEvent error ");
        }
//        for (TCPClient t : tcpClients) {
//            if (t.getUid().equals(event.getUid())) {
//                t.sendMessage(DataParser.sendProgramOnHot(1, 0, event.getProgram()));
//            }
//        }
        bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                DataParser.sendProgramOnHot(1, 0, event.getProgram()));

        //tcpClients.get(0).sendMessage(DataParser.sendProgram( event.getProgram()));
        log("Program EVENT");

    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSendCircuitEvent(SendCircuitEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        try {
            sendCircuitTCP(event.getCircuit(), TCPClientNio.get().get(event.getUid()));
            //TCPClientNio.get().sendMessage(event.getUid(), DataParser.sendProgramOnHot(1, 0, event.getProgram()));
        } catch (Exception e) {
            e.printStackTrace();
            log("onSendCircuitEvent error ");
        }
//        for (TCPClient t : tcpClients) {
//            if (t.getUid().equals(event.getUid())) {
//                sendCircuitTCP(event.getCircuit(), t);
//            }
//        }
        sendCircuitGATT(event.getCircuit(), event.getUid());

        //tcpClients.get(0).sendMessage(DataParser.sendProgram( event.getProgram()));
        log("Program EVENT");
    }

    private void sendCircuitTCP(Circuit circuit, TCPClientNio.Client client) {
        int index = 0;
        for (Program p : circuit.getPrograms()) {
            if (client != null)
                client.sendMessage(DataParser.sendProgram(circuit.getPrograms().length, index, p));
            index++;
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
            bluetoothManager.sendToMIBOBoosterGattDevice(Uid,
                    DataParser.sendProgram(circuit.getPrograms().length, index, p));
            index++;
        }
    }

    private void send(String uid, byte[] msg) {
        try {
            TCPClientNio.get().sendMessage(uid, msg);
        } catch (Exception e) {
            e.printStackTrace();
            log("send message error " + uid + " :: " + e.getMessage());
        }
    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onChangeColorEvent(ChangeColorEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);

        send(event.getUid(), DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
//        for (TCPClient t : tcpClients) {
//            if (t.getUid().equals(event.getUid())) {
//                t.sendMessage(DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
//            }
//        }
        bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
        // tcpClients.get(0).sendMessage(DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
        log("Color EVENT");

    }

    // @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDeviceSearchEvent(DeviceSearchEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        send(event.getUid(), DataParser.sendSearchCommand());
//        for (TCPClient t : tcpClients) {
//            if (t.getUid().equals(event.getUid())) {
//                t.sendMessage(DataParser.sendSearchCommand());
//            }
//        }
        bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                DataParser.sendSearchCommand());
        // tcpClients.get(0).sendMessage(DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
        log("Search EVENT");

    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMainLevelEvent(SendMainLevelEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        send(event.getUid(), DataParser.sendMain(event.getLevel()));
//        for (TCPClient t : tcpClients) {
//            if (t.getUid().equals(event.getUid())) {
//                t.sendMessage(DataParser.sendMain(event.getLevel()));
//            }
//        }
        bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                DataParser.sendMain(event.getLevel()));
        // tcpClients.get(0).sendMessage(DataParser.sendMain( event.getLevel()));
        log("MainLevel EVENT");

    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onChannelsLevelEvent(SendChannelsLevelEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        send(event.getUid(), DataParser.sendLevels(event.getLevels()));
//        for (TCPClient t : tcpClients) {
//            if (t.getUid().equals(event.getUid())) {
//                t.sendMessage(DataParser.sendLevels(event.getLevels()));
//            }
//        }
        bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                DataParser.sendLevels(event.getLevels()));
        //tcpClients.get(0).sendMessage(DataParser.sendLevels( event.getLevels()));
        log("Channels EVENT");

    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDevicePlayEvent(SendDevicePlayEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        send(event.getUid(), DataParser.sendStart());
//        for (TCPClient t : tcpClients) {
//            if (t.getUid().equals(event.getUid())) {
//                t.sendMessage(DataParser.sendStart());
//            }
//        }
        bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                DataParser.sendStart());
        //tcpClients.get(0).sendMessage(DataParser.sendStart());
        log("Play EVENT");

    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDeviceStartEvent(SendDeviceStartEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        send(event.getUid(), DataParser.sendReStart());
//        for (TCPClient t : tcpClients) {
//            if (t.getUid().equals(event.getUid())) {
//                t.sendMessage(DataParser.sendReStart());
//            }
//        }
        bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                DataParser.sendReStart());
        //tcpClients.get(0).sendMessage(DataParser.sendStart());
        log("Play EVENT");

    }


    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDeviceStopEvent(SendDeviceStopEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        send(event.getUid(), DataParser.sendStop());
//        for (TCPClient t : tcpClients) {
//            if (t.getUid().equals(event.getUid())) {
//                t.sendMessage(DataParser.sendStop());
//            }
//        }
        bluetoothManager.sendToMIBOBoosterGattDevice(event.getUid(),
                DataParser.sendStop());
        //tcpClients.get(0).sendMessage(DataParser.sendStop());
        log("Stop EVENT");

    }

//    //@Subscribe
//    public void onNoSubscriberEvent(NoSubscriberEvent event) {
//        //EventBus.getDefault().removeStickyEvent(event);
//    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    private void onBleConnect(BleConnection event) {
        String name = "";
        if (event.getname().contains("HW")) {
            event.getname().replace("HW", "");
        }
        if (event.getname().contains("MIBO-")) {
            event.getname().replace("MIBO-", "");
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

    private void log(String s) {
        Logger.e("CommunicationManager: " + s);
    }

    //private TCPClientNioTest tcpClientNio = null;
    //public ArrayList<TCPClient> mTCPClients = new ArrayList<>();

    private void TCPConnectNio(String ip, String port, String id) {
        TCPClientNio.get().add(ip, Integer.parseInt(port), id, new TCPClientNio.OnMessageReceived() {
            @Override
            public void messageReceived(byte[] message, String uid) {
                log("TCP Client Test Message Received " + message);
                messageConsumer(message, uid);
            }
        });
    }

    private CommunicationListener listener;

    public CommunicationListener getListener() {
        return listener;
    }

    public CommunicationManager2 setListener(CommunicationListener listener) {
        this.listener = listener;
        return this;
    }

}
