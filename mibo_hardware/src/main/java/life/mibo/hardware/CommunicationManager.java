package life.mibo.hardware;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

import life.mibo.hardware.bluetooth.BleGattManager;
import life.mibo.hardware.bluetooth.OnBleCharChanged;
import life.mibo.hardware.bluetooth.OnBleDeviceDiscovered;
import life.mibo.hardware.constants.Config;
import life.mibo.hardware.core.DataParser;
import life.mibo.hardware.core.Logger;
import life.mibo.hardware.core.Utils;
import life.mibo.hardware.encryption.Encryption;
import life.mibo.hardware.events.BleConnection;
import life.mibo.hardware.events.ChangeColorEvent;
import life.mibo.hardware.events.DelayColorEvent;
import life.mibo.hardware.events.DevicePauseResumeEvent;
import life.mibo.hardware.events.DeviceSearchEvent;
import life.mibo.hardware.events.PodEvent;
import life.mibo.hardware.events.ProximityEvent;
import life.mibo.hardware.events.RxlBlinkEvent;
import life.mibo.hardware.events.RxlStatusEvent;
import life.mibo.hardware.events.SendChannelsLevelEvent;
import life.mibo.hardware.events.SendCircuitEvent;
import life.mibo.hardware.events.SendDevicePlayEvent;
import life.mibo.hardware.events.SendDeviceStartEvent;
import life.mibo.hardware.events.SendDeviceStopEvent;
import life.mibo.hardware.events.SendMainLevelEvent;
import life.mibo.hardware.events.SendProgramChangesHotEvent;
import life.mibo.hardware.events.SendProgramEvent;
import life.mibo.hardware.fastble.data.BleDevice;
import life.mibo.hardware.models.Device;
import life.mibo.hardware.models.DeviceColors;
import life.mibo.hardware.models.ScaleData;
import life.mibo.hardware.models.program.Circuit;
import life.mibo.hardware.models.program.Program;
import life.mibo.hardware.network.CommunicationListener;
import life.mibo.hardware.network.TCPClient;
import life.mibo.hardware.network.UDPServer;
import life.mibo.hardware.rxl.RXLManager;

import static java.lang.Thread.sleep;
import static life.mibo.hardware.constants.Config.MIN_COMMAND_LENGTH;
import static life.mibo.hardware.constants.Config.RXL_TAP_EVENT;
import static life.mibo.hardware.constants.Config.TCP_PORT;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_CONNECTED;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_CONNECTING;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_DISCONNECTED;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_DISCOVERED;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_FAILED;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_NEUTRAL;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_WARNING;
import static life.mibo.hardware.models.DeviceTypes.BLE_STIMULATOR;
import static life.mibo.hardware.models.DeviceTypes.HR_MONITOR;
import static life.mibo.hardware.models.DeviceTypes.RXL_BLE;
import static life.mibo.hardware.models.DeviceTypes.RXL_WIFI;
import static life.mibo.hardware.models.DeviceTypes.RXT_WIFI;
import static life.mibo.hardware.models.DeviceTypes.SCALE;
import static life.mibo.hardware.models.DeviceTypes.SCALE_OLD;
import static life.mibo.hardware.models.DeviceTypes.WIFI_STIMULATOR;

//import static life.mibo.hardware.BluetoothManager2.INDICATE;

//import life.mibo.hardware.fastble.callback.BleScanAndConnectCallback;
//import life.mibo.hardware.fastble.callback.BleScanCallback;
//import life.mibo.hardware.fastble.data.BleDevice;
//import life.mibo.hardware.fastble.exception.BleException;

/**
 * Created by Fer on 18/03/2019.
 * updated by Sumeet Kumar on 17/12/2019.
 *
 */

public class CommunicationManager {

    private static CommunicationManager manager;
    private ArrayList<TCPClient> tcpClients = new ArrayList<>();
    private UDPServer udpServer;
    private BluetoothManager2 bluetoothManager;
    //public ScaleManager scaleManager;
    private HashMap<String, Device> mDiscoveredDevices = new HashMap<String, Device>();
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
                log("PingThread commRunning ");
                try {
                    Thread.sleep(4000);
                    for (TCPClient t : tcpClients) {
                        t.sendMessage(DataParser.sendGetStatus(t.getType()), "PingThread");
                        //pingSentDevice(t.getUid());
                        // log("PingThread tcpClients sendMessage");
                        // Log.e("commManag", "send ping");
                    }
                    if (bluetoothManager != null) {
                        ArrayList<BluetoothDevice> list = bluetoothManager.getConnectedBleDevices();
                        log("PingThread bluetoothManager size " + list.size());
                        for (BluetoothDevice d : list) {
                            log("PingThread bluetoothManager sendMessage " + d);
                            if (d.getName() != null) {
                                if (d.getName().contains("MBRXL")) {
                                    bluetoothManager.sendPingToBoosterGattDevice(DataParser.sendGetStatus(DataParser.RXL), d);
                                    log("PingThread bluetoothManager to MBRXL");
                                } else if (d.getName().contains("MIBO-")) {
                                    bluetoothManager.sendPingToBoosterGattDevice(DataParser.sendGetStatus(DataParser.BOOSTER), d);
                                    log("PingThread bluetoothManager to MIBO-");
                                } else if ((d.getName().contains("HW") || d.getName().contains("Geonaute"))) {
                                    //bluetoothManager.sendPingToBoosterGattDevice(DataParser.sendGetStatus(DataParser.BOOSTER), d);
                                    //pingSentDevice(d.toString());
                                    log("PingThread bluetoothManager to Geonaute-");
                                }
                            }
                        }
                    } else {
                        log("PingThread bluetoothManager is NULL ");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log("PingThread Exception " + e.getMessage());
                }
            }
        }
    }

//    private void pingSentDevice(String uid) {
//        for (Device d : SessionManager.getInstance().getUserSession().getDevices()) {//for(Device d : mDiscoveredDevices) {
//            if (d.getUid().equals(uid)) {
//                switch (d.getStatusConnected()) {
//                    case DEVICE_NEUTRAL:
//                        d.setStatusConnected(DEVICE_WAITING);
//                        SessionManager.getInstance().getUserSession().setDeviceStatus(uid, DEVICE_WAITING);
//                        break;
//                    case DEVICE_CONNECTED:
//                        d.setStatusConnected(DEVICE_WAITING);
//                        SessionManager.getInstance().getUserSession().setDeviceStatus(uid, DEVICE_WAITING);
//                        break;
//                    case DEVICE_WAITING:
//                        d.setStatusConnected(DEVICE_WARNING);
//                        SessionManager.getInstance().getUserSession().setDeviceStatus(uid, DEVICE_WARNING);
//                        if (listener != null)
//                            listener.onConnectionStatus(d.getUid());
//                        //EventBus.getDefault().postSticky(new onConnectionStatus(d.getUid()));
//                        break;
//                    case DEVICE_WARNING:
//                        d.setStatusConnected(DEVICE_DISCONNECTED);
//                        SessionManager.getInstance().getUserSession().setDeviceStatus(uid, DEVICE_DISCONNECTED);
//                        if (listener != null)
//                            listener.onConnectionStatus(d.getUid());
//                        //EventBus.getDefault().postSticky(new onConnectionStatus(d.getUid()));
//
//                        break;
//                    case DEVICE_DISCONNECTED:
//                        AlarmManager.getInstance().getAlarms().AddDeviceAlarmByType(DEVICE_ALARM_DISCONNECTED, d.getUid());
//                        if (listener != null)
//                            listener.onAlarmEvent();
//
//                        //EventBus.getDefault().postSticky(new onAlarmEvent());
//                        break;
//                    case DEVICE_CONNECTING:
//
//                        break;
//                    default:
//                        d.setStatusConnected(DEVICE_WAITING);
//                        SessionManager.getInstance().getUserSession().setDeviceStatus(uid, DEVICE_WAITING);
//                }
//            }
//        }
//    }

    public void removeNotConnectedDevices() {
        log("reScanNonConnectedDevices");
        try {
            Iterator<Device> i = mDiscoveredDevices.values().iterator();
            while (i.hasNext()) {
                Device d = i.next();
                if (d.getStatusConnected() == DEVICE_DISCOVERED || d.getStatusConnected() == DEVICE_NEUTRAL || d.getStatusConnected() == DEVICE_DISCONNECTED) {
                    SessionManager.getInstance().getSession().removeRegisteredDevice(d);
                    i.remove();
                }
            }
        } catch (Exception e) {
            // MiboEvent.log(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public synchronized void reScanning(final Activity context, boolean isWifi) {
        mDiscoveredDevices.clear();
        stopScanning();
        //udpServer = null;
        //bluetoothManager = null;
        startScanning(context, isWifi);

    }

    private UDPServer.OnBroadcastReceived wifiListener = new UDPServer.OnBroadcastReceived() {
        @Override
        public void broadcastReceived(byte[] msg, InetAddress ip) {
            if (listener != null)
                listener.udpDeviceReceiver(msg, ip);
            log("udpDeviceReceiver " + new String(msg) + " IP " + ip);
            // TODO remove later broadcastConsumer
            //broadcastConsumer(msg, ip);
            SessionManager manager = SessionManager.getInstance();
            if (manager.getUserSession() != null) {
                if (manager.getUserSession().isBoosterMode()) {// true wifi mode
                    broadcastConsumer(msg, ip);
                    log("Session Manager Boosted Mode");
                } else {
                    log("Session Manager No Boosted Mode " + manager);
                }
            } else {
                log("Session Manager getSession is NULL " + manager);
            }
        }
    };

    private OnBleDeviceDiscovered bleListener = new OnBleDeviceDiscovered() {
        @Override
        public void bleHrDeviceDiscovered(String uid, String name) {
            log("BluetoothManager bleHrDeviceDiscovered " + uid + " IP " + name);
            bleHrDiscoverConsumer(uid, name);

        }

        @Override
        public void bleBoosterDeviceDiscovered(String uid, String serial) {
            log("BluetoothManager bleBoosterDeviceDiscovered " + uid + " IP " + serial);
            //if (!SessionManager.getInstance().getUserSession().isBoosterMode())// true wifi mode, false ble mode
            bleBoosterDiscoverConsumer(uid, serial);

        }

        @Override
        public void bleRXLDiscovered(String uid, String serial, String name) {
            log("bleRXLDiscovered " + uid);
            bleRxlDiscovered(uid, serial, name);
        }

        @Override
        public void bleScaleDeviceDiscovered(String uid, String serial) {
            log("BluetoothManager bleScaleDeviceDiscovered " + uid + " IP " + serial);
            bleScaleDiscoverConsumer(uid, serial);

        }

        @Override
        public void onConnect(String name, int status) {
            if (listener != null)
                listener.onConnect(name, status);
            SessionManager.getInstance().getUserSession().setDeviceStatusByName(name, DEVICE_CONNECTED);
        }

        @Override
        public void onDisconnect(boolean isActive, String name, int code) {
            if (listener != null)
                listener.onDisconnect(false, name, code, "");

            SessionManager.getInstance().getUserSession().setDeviceStatusByName(name, DEVICE_DISCONNECTED);
        }

        @Override
        public void onConnectFailed(String name, String error) {
            if (listener != null)
                listener.onDisconnect(true, name, -1, error);

            SessionManager.getInstance().getUserSession().setDeviceStatusByName(name, DEVICE_FAILED);
        }

    };

    private OnBleCharChanged bleCharChanged = new OnBleCharChanged() {
        @Override
        public void bleHrChanged(int hr, String serial) {
            log("BluetoothManager bleHrChanged " + hr + " IP " + serial);
            //bleHrConsumer(hr, serial);

        }

        @Override
        public void bleScale(float weight, ScaleData data, int code, Object other) {
            if (listener != null)
                listener.onScale(weight, data, code, other);
        }

        @Override
        public void bleBoosterChanged(byte[] data, String uid, int property) {
            log("BluetoothManager bleBoosterChanged " + Arrays.toString(data) + " - IP " + uid);
            // bleBoosterConsumer(data, serial);
            if (uid.startsWith("HW")) {
                bleHrConsumer(data, uid, property);
            } else {
                if (property == Config.INDICATE) {
                    if (listener != null)
                        listener.onCommandReceived(Config.INDICATE, data, uid, DataParser.BOOSTER);
                    return;
                }
                receiveCommands(data, Utils.getUid(uid), true);
            }
            //Log.e("commManag","Char booster changed "+data);
        }
    };

    public void startHrMonitor(String uid) {
        if (bluetoothManager != null)
            bluetoothManager.startReading(uid);
    }

    public void stopHrMonitor(String uid) {
        if (bluetoothManager != null)
            bluetoothManager.stopReading(uid);
    }

    private static double extractHeartRate(BluetoothGattCharacteristic characteristic) {

        int flag = characteristic.getProperties();
        int format = -1;
        // Heart rate bit number format
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
        }
        final int heartRate = characteristic.getIntValue(format, 1);
        return heartRate;
    }

    private static double extractHeartRate(byte[] characteristic) {
//        int flag = characteristic.getProperties();
//        int format = -1;
//        // Heart rate bit number format
//        if ((flag & 0x01) != 0) {
//            format = BluetoothGattCharacteristic.FORMAT_UINT16;
//        } else {
//            format = BluetoothGattCharacteristic.FORMAT_UINT8;
//        }
//        final int heartRate = characteristic.getIntValue(format, 1);

        return characteristic[1];
    }


    private BleGattManager.OnConnection bleConnection = new BleGattManager.OnConnection() {
        @Override
        public void onConnected(String deviceName) {
            log("BluetoothManager leGattManager.OnConnection ");
            onBleConnect(new BleConnection(deviceName));
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public synchronized void startScanning(final Activity context, boolean isWifi) {
        this.isWifi = isWifi;
        log("Starting scanning...." + isWifi);
        log("Starting scanning...." + this.isWifi);
        rxlCount = 0;
        if (this.isWifi)
            scanWifi();
        else
            scanBluetooth(context);
    }

    public void scanWifi() {
        log("WIFI Scanning.....");
        if (udpServer == null) {
            mDiscoveredDevices.clear();
            udpServer = new UDPServer();
        }
        udpServer.addListener(wifiListener);
        udpServer.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scanBluetooth(Context context) {
        log("BLUETOOTH Scanning.....");

        //TODO: Change to not overwrite the current one if its initialized and only start discovery
        if (bluetoothManager == null) {
            bluetoothManager = new BluetoothManager2(context);
            bluetoothManager.initBlueTooth();
        }
        //bluetoothManager.initBlueTooth();
        //bluetoothManager.reset();
        //bluetoothManager.reset();
        bluetoothManager.setListener(bleListener);
        bluetoothManager.setOnBleCharChanged(bleCharChanged);
        bluetoothManager.setBleGatListener(bleConnection);
        bluetoothManager.scanDevice();
        //bluetoothManager.scanDevice();
    }


    public static void log(String s) {
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
        try {
            if (bluetoothManager != null)
                bluetoothManager.destroy();
            pingThread = null;
            tcpClients.clear();
            mDiscoveredDevices.clear();
            commRunning = false;
            bluetoothManager = null;
            listener = null;
            udpServer = null;
            //pingThread.stop();
            pingThread = null;
        } catch (Exception e) {

        }

    }

    public void release() {
        onDestroy();
        manager = null;

    }


    public void stopUDPDiscoveryServer() {
        if (udpServer != null) {
            udpServer.stop();
        }
        // udpServer = null;
    }

    public void stopBleDiscoveryServer() {
        if (bluetoothManager != null) {
            try {
                bluetoothManager.stopScanDevice();
            }
            catch (Exception e){

            }
        }
        // bluetoothManager = null;
    }

    private boolean isBooster(byte[] message) {
        return message.length > 3 && message[0] == 77 && message[1] == 73 && message[2] == 66 && message[3] == 79;
    }

    private boolean isRxl(byte[] message) {
        return message.length > 4 && message[0] == 77 && message[1] == 66 && message[2] == 82 && message[3] == 88 && message[4] == 76;
    }

    private synchronized void broadcastConsumer(byte[] message, InetAddress ip) {
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

                        logi("broadcastConsumer checkBoosterDevice " + ip + " : " + Arrays.toString(command));
                        checkBoosterDevice(command, ip);
                        return;
                    }
                } else if (message[i] == 'M' && message[i + 1] == 'B' && message[i + 2] == 'R' && message[i + 3] == 'X') {
                    if (message[i + 4] == 'L') {
                        if (message.length > (i + message[i + 7] + 8)) {
                            byte[] command = new byte[message[i + 7] + 2];
                            for (int j = 0; j < message[i + 7] + 2; j++) {
                                command[j] = message[i + 6 + j];
                            }

                            logi("broadcastConsumer checkRxlDevice " + ip + " : " + Arrays.toString(command));
                            checkRxlDevice(ip, command);
                            return;
                        }
                    }
                    if (message[i + 4] == 'T') {
                        if (message.length > (i + message[i + 7] + 8)) {
                            byte[] command = new byte[message[i + 7] + 2];
                            for (int j = 0; j < message[i + 7] + 2; j++) {
                                command[j] = message[i + 6 + j];
                            }

                            logi("broadcastConsumer checkRxlDevice " + ip + " : " + Arrays.toString(command));
                            checkRxtDevice(ip, command);
                            return;
                        }
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

    private void bleHrDiscoverConsumer(String uid, String name) {
        add(new Device(name, uid, name, HR_MONITOR));
        SessionManager.getInstance().getUserSession().setDeviceStatus(uid, DEVICE_WARNING);
        // if (listener != null)
        //     listener.onDeviceDiscoveredEvent("");
        //EventBus.getDefault().postSticky(new onDeviceDiscoveredEvent(""));
    }

    private void bleScaleDiscoverConsumer(String uid, String serial) {
        if (serial != null && serial.contains("WS806"))
            add(new Device(serial, uid, serial, SCALE_OLD));
        else add(new Device(serial, uid, serial, SCALE));
        SessionManager.getInstance().getUserSession().setDeviceStatus(uid, DEVICE_WARNING);
        //  if (listener != null)
        //     listener.onDeviceDiscoveredEvent("");
        //EventBus.getDefault().postSticky(new onDeviceDiscoveredEvent(""));
    }

    private void bleBoosterDiscoverConsumer(String uid, String serial) {
        add(new Device("", uid, serial.replace("MIBO-", ""), BLE_STIMULATOR));
        SessionManager.getInstance().getUserSession().setDeviceStatus(uid, DEVICE_WARNING);
        // if (listener != null)
        //   listener.onDeviceDiscoveredEvent("");

        //EventBus.getDefault().postSticky(new onDeviceDiscoveredEvent(""));
    }

    private void bleRxlDiscovered(String uid, String serial, String name) {
        //log("bleRxlDiscovered serial: " + serial);
        //log("bleRxlDiscovered uid: " + Utils.getUid(serial));
        add(new Device(name, uid, Utils.getUid(serial), RXL_BLE));
        SessionManager.getInstance().getUserSession().setDeviceStatus(uid, DEVICE_WARNING);
        // if (listener != null)
        //   listener.onDeviceDiscoveredEvent("");

        //EventBus.getDefault().postSticky(new onDeviceDiscoveredEvent(""));
    }

    private void bleHrConsumer(byte[] hr, String uid, int property) {
        if (listener != null)
            listener.HrEvent(hr, uid, property);
        log("bleHrConsumer " + hr);
//        for (Device d : mDiscoveredDevices.values()) {
//            if (d.getUid().equals(uid)) {
//                if (d.getStatusConnected() != DEVICE_WAITING && d.getStatusConnected() != DEVICE_CONNECTED) {
////                    if (d.getStatusConnected() == DEVICE_DISCONNECTED) {
////                        EventBus.getDefault().postSticky(new ChangeColorEvent(d, d.getUid()));
////                    }
//                    d.setStatusConnected(DEVICE_CONNECTED);
//                    SessionManager.getInstance().getUserSession().setDeviceStatus(uid, DEVICE_CONNECTED);
//
//                    if (listener != null)
//                        listener.onConnectionStatus(uid);
//
//                    //EventBus.getDefault().postSticky(new onConnectionStatus(uid));
//
//                }
//            }
//        }
//        if (hr.length > 1) {
//            log("bleHrConsumer HR >> " + hr[1]);
//            // HR
//        } else if (hr.length == 1) {
//            log("bleHrConsumer Battery >> " + hr[0]);
//            // Battery
//            Device device = SessionManager.getInstance().getUserSession().getDevice(uid);
//            if (device != null) {
//                device.setBatteryLevel(hr[0]);
//                device.setStatusConnected(DEVICE_CONNECTED);
//            }
//            log("bleHrConsumer device " + device);
//        }

//        if (SessionManager.getInstance().getUserSession().getCurrentSessionStatus() == 1 ||
//                SessionManager.getInstance().getUserSession().getCurrentSessionStatus() == 2) {
//            SessionManager.getInstance().getUserSession().getUserByHrUid(uid).setHr(hr);
//        }
//        if (listener != null)
//            listener.HrEvent(hr, uid);
        // EventBus.getDefault().postSticky(new HrEvent(hr, uid));
    }


    private synchronized void checkBoosterDevice(byte[] command, InetAddress ip) {
        log("checkNewDevice --- " + true);
        for (Device d : mDiscoveredDevices.values()) {
            if (d.getIp().equals(ip)) {
                log("checkBoosterDevice --- found1 " + ip);
                return;
            }
        }
        add(new Device("", DataParser.getUID(command), ip, WIFI_STIMULATOR));
    }

    private int rxlCount = 0;

    private synchronized void checkRxlDevice(InetAddress ip, byte[] command) {
        log("checkRxlDevice --- " + ip);
        // log("checkRxlDevice uid: " + DataParser.getUIDRxl(ip.getAddress(), command));
        for (Device d : mDiscoveredDevices.values()) {
            if (d.getIp().equals(ip)) {
                log("checkRxlDevice --- found in mDiscoveredDevices " + ip);
                return;
            }
        }
        add(new Device("RXL " + ++rxlCount, DataParser.getUIDRxl(ip.getAddress(), command), ip, RXL_WIFI));
    }

    private synchronized void checkRxtDevice(InetAddress ip, byte[] cmd) {
        log("checkRxlDevice --- " + ip);
        // log("checkRxlDevice mac --- " + new String(DataParser.getUIDRxl(ip.getAddress(),cmd)));
        //log("checkRxlDevice mac --- " + new String(DataParser.getUIDRxl(ip.getAddress(),cmd)));
        //debugCommands("checkRxlDevice mac", DataParser.getUIDRxl(ip.getAddress(),cmd));
        for (Device d : mDiscoveredDevices.values()) {
            if (d.getIp().equals(ip)) {
                log("checkRxlDevice --- found in mDiscoveredDevices " + ip);
                return;
            }
        }
        add(new Device("RXT", DataParser.getUIDRxl(ip.getAddress(), cmd), ip, RXT_WIFI));
    }

    private void checkNewDevice(byte[] command, InetAddress ip) {
        boolean newDevice = true;
        log("checkNewDevice --- " + true);
        for (Device d : mDiscoveredDevices.values()) {
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
                SessionManager.getInstance().getUserSession().setDeviceStatus(d.getUid(), DEVICE_WARNING);
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
        log("mDiscoveredDevices add Device............. --- " + device);
        if (mDiscoveredDevices == null)
            mDiscoveredDevices = new HashMap<>();
        if (bluetoothManager != null) {
            // if (bluetoothManager.contains(device.getUid()))
            //    return;
        }
        if (mDiscoveredDevices.containsKey(device.getUid()))
            return;
        mDiscoveredDevices.put(device.getUid(), device);
        log("mDiscoveredDevices Device added............. --- " + device);
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
        for (Device d : mDiscoveredDevices.values()) {
            if (d.getUid().equals(device.getUid())) {
                newDevice = false;
            }
        }
        if (newDevice) {
            add(device);
        }
    }

    // todo not implemented yet
    public ArrayList<Device> getConnectedDevices() {
        ArrayList<Device> list = new ArrayList<>();
        if (tcpClients.size() > 0) {
            for (TCPClient client : tcpClients) {

            }
        }

        if (bluetoothManager != null) {
            ArrayList<BluetoothDevice> ble = bluetoothManager.getConnectedBleDevices();
            for (BluetoothDevice b : ble) {

            }
        }

        return list;
    }


    public synchronized void reconnectBleBooster(@Nullable String uid) {
        if(uid == null)
            return;
        if (pingThread == null) {
            commRunning = true;
            pingThread = new Thread(new PingThread());
            pingThread.start();
        }
        if (!commRunning) {
            commRunning = true;
            pingThread.start();
        }

        if (bluetoothManager != null) {
            if (uid.startsWith("MIBO-")) {
                uid = uid.substring("MIBO-".length());
            }
            bluetoothManager.connectMIBOBoosterGattDevice(uid);
        }
    }

    public synchronized void connectDevice(Device device) {
        //  log("PingThread connectDevice commRunning " + commRunning + " :: " + pingThread);
        if (pingThread == null) {
            //  log("PingThread connectDevice PingThread WAS NULL NOW INIT..... ");
            commRunning = true;
            pingThread = new Thread(new PingThread());
            pingThread.start();
        }
        if (!commRunning) {
            // log("PingThread connectDevice commRunning WAS FALSE NOW INIT..... ");
            commRunning = true;
            pingThread.start();
        }
        //  log("PingThread connectDevice commRunning " + commRunning + " :: " + pingThread);
        log("connectDevice " + device);
        if (device == null)
            return;
        switch (device.getType()) {
            case WIFI_STIMULATOR: {
                String disc = "Not Found!";
                for (Device d : mDiscoveredDevices.values()) {
                    if (d.getUid().equals(device.getUid()) && (d.getType() == WIFI_STIMULATOR)) {
                        connectTCPDevice(d.getIp().getHostAddress(), TCP_PORT, d.getUid(), DataParser.BOOSTER);
                        SessionManager.getInstance().getUserSession().addDevice(device);
                        device.setStatusConnected(DEVICE_CONNECTING);
                        disc = "Found!...";
                    }
                }
                log("connectDevice " + disc);
            }
            break;

            case BLE_STIMULATOR: {
                log("connectDevice BLE_STIMULATOR " + bluetoothManager);
                if (bluetoothManager != null) {
                    String id = device.getUid();
                    if (id.startsWith("MIBO-")) {
                        id = device.getName().substring("MIBO-".length());
                    }
                    bluetoothManager.connectMIBOBoosterGattDevice(id);
                }
                //if (bluetoothManager != null)
                //  bluetoothManager.connectMIBOBoosterGattDevice(device.getUid());
                SessionManager.getInstance().getUserSession().addDevice(device);
                device.setStatusConnected(DEVICE_CONNECTING);
            }
            break;

            case RXL_WIFI: {
                log("device connect " + device.getIp());
                //DataParser.isRxl = true;
                connectTCPDevice(device.getIp().getHostAddress(), TCP_PORT, device.getUid(), DataParser.RXL);
                SessionManager.getInstance().getUserSession().addDevice(device);
                device.setStatusConnected(DEVICE_CONNECTING);
            }
            break;

            case RXL_BLE: {
                log("connectDevice RXL_BLE " + bluetoothManager);
                if (bluetoothManager != null) {
                    log("connectDevice RXL_BLE UID " + device.getUid());
                    bluetoothManager.connectRXLGattDevice(Utils.getUid(device.getUid()));
                }
                //if (bluetoothManager != null)
                //  bluetoothManager.connectMIBOBoosterGattDevice(device.getUid());
                SessionManager.getInstance().getUserSession().addDevice(device);
                device.setStatusConnected(DEVICE_CONNECTING);
            }
            break;

            case RXT_WIFI: {
                log("RXT_WIFI connect " + device.getIp());
                connectTCPDevice(device.getIp().getHostAddress(), TCP_PORT, device.getUid(), DataParser.RXT);
                SessionManager.getInstance().getUserSession().addDevice(device);
                device.setStatusConnected(DEVICE_CONNECTING);
            }
            break;
            case HR_MONITOR: {
                //stopDiscoveryServers();
                log("HR_MONITOR connect " + device.getIp());
                if (bluetoothManager != null)
                    bluetoothManager.connectHrGattDevice(device.getUid());
                SessionManager.getInstance().getUserSession().addDevice(device);
                device.setStatusConnected(DEVICE_CONNECTING);
            }
            break;

            case SCALE: {
                //SessionManager.getInstance().getUserSession().addScale(bluetoothManager.devicesScaleBle.get(0));
                if (bluetoothManager != null)
                    bluetoothManager.connectScale(device.getUid());
                // SessionManager.getInstance().getUserSession().addDevice(bluetoothManager.getScaleDevice());
                SessionManager.getInstance().getUserSession().addDevice(device);
            }
            case SCALE_OLD: {
                //SessionManager.getInstance().getUserSession().addScale(bluetoothManager.devicesScaleBle.get(0));
                if (bluetoothManager != null)
                    bluetoothManager.connectScaleLegacy(device.getUid());
                // SessionManager.getInstance().getUserSession().addDevice(bluetoothManager.getScaleDevice());
                SessionManager.getInstance().getUserSession().addDevice(device);
            }
            break;

        }

    }


    public void disconnectDevice(Device device) {
        log("disconnectDevice " + device);
        if (device == null)
            return;

        int pos = -1;

        if (device.getType() == WIFI_STIMULATOR) {
            if (device.getIp() != null) {
                pos = disconnectTCPDevice(device.getUid(), false);
            }
        } else if (device.getType() == RXL_WIFI) {
            if (device.getIp() != null) {
                pos = disconnectTCPDevice(device.getUid(), true);
            }

        } else if (device.getType() == RXT_WIFI) {
            if (device.getIp() != null) {
                pos = disconnectTCPDevice(device.getUid(), true);
            }

        } else if (device.getType() == HR_MONITOR) {
            if (bluetoothManager != null)
                bluetoothManager.disconnectHrGattDevice(device.getUid());
        } else if (device.getType() == BLE_STIMULATOR) {
            log("disconnectDevice BLE_STIMULATOR");
            if (bluetoothManager != null) {
                String id = device.getUid();
                if (id.contains(":")) {
                    id = device.getName().substring("MIBO-".length());
                }
                pos = bluetoothManager.disconnectMIBOBoosterGattDevice(id);
            }
        } else if (device.getType() == RXL_BLE) {
            log("disconnectDevice BLE_STIMULATOR");
            if (bluetoothManager != null) {
                String id = device.getUid();
                if (id.contains(":")) {
                    id = device.getName().substring("MIBO-".length());
                }
                pos = bluetoothManager.disconnectMIBOBoosterGattDevice(id);
            }
        } else if (device.getType() == SCALE) {
            log("disconnectDevice BLE_STIMULATOR");
            if (bluetoothManager != null) {
                String id = device.getUid();
                if (id.contains(":")) {
                    id = device.getName().substring("MIBO-".length());
                }
                pos = bluetoothManager.disconnectMIBOBoosterGattDevice(id);
            }
        } else if (device.getType() == SCALE_OLD) {
            log("disconnectDevice BLE_STIMULATOR");
            if (bluetoothManager != null) {
                bluetoothManager.disconnectScaleLegacy(device.getUid());
            }
        }

        if (pos != -1) {
            SessionManager.getInstance().getUserSession().removeDevice(device);
        }
        if (pos != -1 && listener != null)
            listener.onDeviceDisconnect(device.getUid());

    }

    private int disconnectTCPDevice(String uid, boolean rxl) {
//        for(TCPClient t : tcpClients) {
//            if(t.getUid().equals(Uid)){
//                t.stopClient();
//                this.tcpClients.remove(t);
//            }
//        }
        int aux = -1;
        if (tcpClients != null) {

            for (TCPClient t : tcpClients) {
                if (t.getUid().equals(uid)) {
                    aux = tcpClients.indexOf(t);
                    t.stopClient();
                    break;
                }
            }
            if (aux != -1) {
                tcpClients.remove(aux);
            }
            log("disconnectTCPDevice " + uid + " : " + aux);
        }
        return aux;
    }

    private void connectTCPDevice(String ip, String port, String Uid, int rxl) {
        log("connectTCPDevice " + ip);
        //create a TCPClient object
        // log("TCPConnect ip " + ip + " , port " + port);
        boolean newDevice = true;
        for (TCPClient t : tcpClients) {
            if (t.getServerIp().equals(ip)) {
                newDevice = false;
                break;
            }
        }
        if (newDevice) {
            tcpClients.add(new TCPClient(ip, Integer.parseInt(port), Uid, rxl, new TCPClient.OnMessageReceived() {
                @Override
                public void messageReceived(byte[] message, String uid) {
                    receiveCommands(message, uid, false);
                }
            }));
            for (TCPClient t : tcpClients) {
                if (t.getServerIp().equals(ip)) {
                    log("connectTCPDevice TCPClient run");
                    t.run();
                    break;
                }
            }
        }
        log("TCPConnect ip " + ip + " , port " + port + " newDevice " + newDevice);


    }

    private void receiveCommands(byte[] message, String uid, boolean bluetooth) {//lento?
        Encryption.mbp_decrypt(message, message.length);
        log("receiveCommands char " + Utils.getChars(message) + " : UID " + uid);
        log("receiveCommands byte " + Utils.getBytes(message) + " : UID " + uid);
        if (message.length >= MIN_COMMAND_LENGTH) {
            try {
                for (int i = 0; i < message.length; i++) {
                    // log("receiveCommands i "+i);
                    if (message.length > i + 4) {
                        if (message[i] == 77 && message[i + 1] == 73 && message[i + 2] == 66 && message[i + 3] == 79) {
                            if (message.length > (i + message[i + 6] + 8)) {
                                byte[] command = new byte[message[i + 6] + 2];
                                for (int j = 0; j < message[i + 6] + 2; j++) {
                                    command[j] = message[i + 5 + j];
                                }
                                parseCommandsBooster(command, uid);
                                //log("receiveCommands i "+i);
                                return;
                            }
                        } else if (message[i] == 77 && message[i + 1] == 66 && message[i + 2] == 82 && message[i + 3] == 88) {
                            if (message[i + 4] == 76) {
                                if (message.length > (i + message[i + 6] + 8)) {
                                    byte[] command = new byte[message[i + 7] + 2];
                                    for (int j = 0; j < message[i + 7] + 2; j++) {
                                        command[j] = message[i + 6 + j];
                                        //log("parseCommandsRxl parsing  " + j + " : " + (message[i + 6 + j] & 0xFF));
                                    }

                                    parseCommandsRxl(command, uid);
                                    //log("receiveCommands2 i " + i);
                                    return;
                                }
                            }
                            if (message[i + 4] == 'T') {
                                if (message.length > (i + message[i + 6] + 8)) {
                                    byte[] command = new byte[message[i + 7] + 2];
                                    for (int j = 0; j < message[i + 7] + 2; j++) {
                                        command[j] = message[i + 6 + j];
                                        //log("parseCommandsRxl parsing  " + j + " : " + (message[i + 6 + j] & 0xFF));
                                    }

                                    parseCommandsRxt(command, uid);
                                    //log("receiveCommands2 i " + i);
                                    return;
                                }
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


    private void parseCommandsBooster(byte[] command, String uid) {
        log("parseCommands2 msg " + Arrays.toString(command) + " : UID " + uid);
        if (listener != null)
            listener.onCommandReceived(DataParser.getCommand(command), command, uid, DataParser.BOOSTER);
    }

    private void parseCommandsRxl(byte[] command, String uid) {
        log("parseCommandsRxl msg " + Utils.getBytes(command) + " : UID " + uid);
        if (DataParser.getCommand(command) == RXL_TAP_EVENT) {
            // RXLHelper.Companion.getInstance().post(new RxlStatusEvent(command, uid));
            RXLManager.Companion.getInstance().postDirect(new RxlStatusEvent(command, uid));
            return;
        }

        if (listener != null)
            listener.onCommandReceived(DataParser.getCommand(command), command, uid, DataParser.RXL);
    }

    private void parseCommandsRxt(byte[] command, String uid) {
        log("parseCommandsRXT msg " + Utils.getChars(command) + " : UID " + uid);
        log("parseCommandsRXT msg " + Utils.getBytes(command) + " : UID " + uid);
        if (listener != null)
            listener.onCommandReceived(DataParser.getCommand(command), command, uid, DataParser.RXT);
    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSendProgramEvent(SendProgramEvent event) {
        log("onSendProgramEvent " + event);
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendProgram(1, 0, event.getProgram()));
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendMessage(event.getUid(),
                    DataParser.sendProgram(1, 0, event.getProgram()), "SendProgramEvent");

        //tcpClients.get(0).sendMessage(DataParser.sendProgram( event.getProgram()));
        log("Program EVENT");

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
            bluetoothManager.sendMessage(event.getUid(),
                    DataParser.sendProgramOnHot(1, 0, event.getProgram()), "SendProgramChangesHotEvent");

        //tcpClients.get(0).sendMessage(DataParser.sendProgram( event.getProgram()));
        log("Program EVENT");

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
        log("Program EVENT");
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
                bluetoothManager.sendMessage(Uid,
                        DataParser.sendProgram(circuit.getPrograms().length, index, p), "sendCircuitGATT");
            index++;
        }
    }

    // POD
    public void onPodEvent(@NotNull PodEvent event) {
        log("PodEvent "+event);
        if (event.isAll()) {
            for (TCPClient t : tcpClients) {
                if (event.getPod().getType() == RXL_WIFI || event.getPod().getType() == RXL_BLE)
                    t.sendMessage(DataParser.sendRxlColor(event.getPod().getColorPalet(), event.getTime(), t.getType()), "onPodEvent");
            }
        } else {
            for (TCPClient t : tcpClients) {
                if (t.getUid().equals(event.getUid())) {
                    t.sendMessage(DataParser.sendRxlColor(event.getPod().getColorPalet(), event.getTime(), t.getType()), "onPodEvent");
                }
            }
        }

    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onChangeColorEvent(ChangeColorEvent event) {
        log("onChangeColorEvent "+event);
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                if (event.getDevice().getType() == RXL_WIFI) {
                    t.sendMessage(DataParser.sendRxlColor(event.getDevice().getColorPalet(), event.getTime(), event.getData(), t.getType()), "onChangeColorEvent");
                } else if (event.getDevice().getType() == RXT_WIFI) {
                    t.sendMessage(DataParser.sendRxtColor(Integer.parseInt(event.getDevice().getData().toString()), event.getDevice().getColorPalet(), event.getTime(), t.getType()), "onRXTColorChange");
                } else {
                    t.sendMessage(DataParser.sendColor(DeviceColors.getColor(event.getDevice().getColorPalet()), t.getType()), "onChangeColorEvent");
                }
            }
        }
        if (bluetoothManager != null) {
            if (event.getDevice().getType() == RXL_BLE) {
                //t.sendMessage(DataParser.sendRxlColor(event.getDevice().getColorPalet(), event.getTime(), t.getType()), "onChangeColorEvent");
                bluetoothManager.sendMessage(event.getUid(), DataParser.sendRxlColor(event.getDevice().getColorPalet(), event.getTime(), event.getData(), DataParser.RXL), "ChangeColorEvent", DataParser.RXL);
            } else {
                bluetoothManager.sendMessage(event.getUid(), DataParser.sendColor(DeviceColors.getColor(event.getDevice().getColorPalet()), event.getDevice().type()), "ChangeColorEvent");
            }


        }
        // tcpClients.get(0).sendMessage(DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
        log("onChangeColorEvent color changed..................... " + event.getUid());

    }

    public synchronized void onDelayColorEvent(DelayColorEvent event) {
        log("onDelayColorEvent " + event);
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendRxlDelayColor(event.getDevice().getColorPalet(), event.getTime(), event.getData(), event.getDelay()), "onDelayColorEvent");
            }
        }
        if (bluetoothManager != null) {
            bluetoothManager.sendMessage(event.getUid(), DataParser.sendRxlColor(event.getDevice().getColorPalet(), event.getTime(), event.getData(), DataParser.RXL), "DelayColorEvent", DataParser.RXL);
        }
        // tcpClients.get(0).sendMessage(DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
        log("onChangeColorEvent color changed..................... " + event.getUid());

    }

    public synchronized void onBlinkEvent(RxlBlinkEvent event) {
        log("onBlinkEvent: " + event.getUid());
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                log("onBlinkEvent UID MATCHED");
                t.sendMessage(DataParser.sendRxlBlink(event.getColor(), event.getCycles(), event.getTimeOn(), event.getTimeOff()), "onBlinkEvent");
            }
        }
        if (bluetoothManager != null) {
            bluetoothManager.sendMessage(event.getUid(), DataParser.sendRxlBlink(event.getColor(), event.getCycles(), event.getTimeOn(), event.getTimeOff()), "onBlinkEvent", DataParser.RXL);
        }
        // tcpClients.get(0).sendMessage(DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
        log("onBlinkEvent blink color changed..................... " + event.getUid());

    }

    public void onProximityEvent(ProximityEvent event) {
        log("ProximityEvent " + event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendProximitySensor(event.getType()), "ProximityEvent");
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendMessage(event.getUid(), DataParser.sendProximitySensor(event.getType()), "ProximityEvent", DataParser.RXL);
        log("ProximityEvent");
    }


    public void onChangeColorEventRxl(ChangeColorEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet()), event.getDevice().type()));
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendMessage(event.getUid(),
                    DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet()), event.getDevice().type()), "ChangeColorEvent");
        // tcpClients.get(0).sendMessage(DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
        log("Color EVENT");

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
            bluetoothManager.sendMessage(event.getUid(),
                    DataParser.sendSearchCommand(), "DeviceSearchEvent");
        // tcpClients.get(0).sendMessage(DataParser.sendColor(DeviceColors.getColorPaleteToByte(event.getDevice().getColorPalet())));
        log("Search EVENT");

    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMainLevelEvent(SendMainLevelEvent event) {
        log("onMainLevelEvent " + event.getLevel());
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendMain(event.getLevel()), "onMainLevelEvent");
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendMessage(event.getUid(),
                    DataParser.sendMain(event.getLevel()), "SendMainLevelEvent");
        // tcpClients.get(0).sendMessage(DataParser.sendMain( event.getLevel()));
        log("MainLevel EVENT");

    }

    public @Nullable
    BleDevice getBle(String uid) {
        if (bluetoothManager != null)
            return bluetoothManager.getBle(uid);
        return null;
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
            bluetoothManager.sendMessage(event.getUid(),
                    DataParser.sendLevels(event.getLevels()), "SendChannelsLevelEvent");
        //tcpClients.get(0).sendMessage(DataParser.sendLevels( event.getLevels()));

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
            bluetoothManager.sendMessage(event.getUid(), DataParser.sendStart(), "SendDevicePlayEvent");

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
    }

    //@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDeviceRestartEvent(SendDeviceStartEvent event) {
        onDeviceStartEvent(event);
    }

    public void onDeviceResumePauseEvent(DevicePauseResumeEvent event) {
        log("onDeviceResumePauseEvent DevicePauseResumeEvent ");
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                log("onDeviceStartEvent ");
                t.sendMessage(DataParser.sendPause(event.getData()));
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendMessage(event.getUid(),
                    DataParser.sendPause(event.getData()), "onDeviceResumePauseEvent");
        //tcpClients.get(0).sendMessage(DataParser.sendStart());
        log("RESTART EVENT");

    }

    public void onFirmwareEvent(String uid) {
        log("onFirmwareEvent");
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(uid)) {
                t.sendMessage(DataParser.sendGetFirm());
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendMessage(uid, DataParser.sendGetFirm(), "onFirmwareEvent");
        //tcpClients.get(0).sendMessage(DataParser.sendStart());
    }

    public void onDeviceStartEvent(SendDeviceStartEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                log("onDeviceStartEvent ");
                t.sendMessage(DataParser.sendReStart());
            }
        }
        if (bluetoothManager != null)
            bluetoothManager.sendMessage(event.getUid(),
                    DataParser.sendReStart(), "onDeviceStartEvent");
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
            bluetoothManager.sendMessage(event.getUid(),
                    DataParser.sendStop(), "SendDeviceStopEvent");
        //tcpClients.get(0).sendMessage(DataParser.sendStop());
        log("onDeviceStopEvent Stop EVENT");

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
            } else if (event.getname().contains("MIBO-RXL")) {
                event.getname().replace("MIBO-RXL-", "");
            } else if (event.getname().contains("MIBO-")) {
                event.getname().replace("MIBO-", "");
            } else if (event.getname().contains("MBRXL")) {
                event.getname().replace("MBRXL-", "");
            }
        }

        for (Device d : mDiscoveredDevices.values()) {
            if (d.getUid().equals(event.getname())) {

                d.setStatusConnected(DEVICE_CONNECTED);
                SessionManager.getInstance().getUserSession().setDeviceStatus(event.getname(), DEVICE_CONNECTED);
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

    public void broadcastMessage(int status, String uid, String msg) {
        if (listener != null)
            listener.onDisconnect(true, uid, status, msg);
    }

    public void sendMessage(TCPClient client, byte[] msg) {
        client.sendMessage(msg);
    }

    public void sendBleMessage(String uid, byte[] msg) {
        if (bluetoothManager != null)
            bluetoothManager.sendMessage(uid, msg, "sendBleMessage");
    }


    // @Subscribe(threadMode = ThreadMode.ASYNC)
    private final Object lock = new Object();
    //Sync object for non-fair locks
    private final ReentrantLock locks = new ReentrantLock();

    public void onChangeRxtColorEvent(ChangeColorEvent event) {
        log("onChangeRxtColorEvent call");
        //EventBus.getDefault().removeStickyEvent(event);
        synchronized (lock) {
            log("onChangeRxtColorEvent execute");
            for (TCPClient t : tcpClients) {
                if (t.getUid().equals(event.getUid())) {
                    t.sendMessage(DataParser.sendRxtColor(event.getTileIdInt(), event.getColor(), event.getTime(), event.getData(), event.getLightType()));
                    return;
                }
            }
        }


    }

    // @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRxtIdConfigurations(ChangeColorEvent event) {
        log("onRxtIdConfigurations");
        // EventBus.getDefault().removeStickyEvent(event);
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendRxtIdConfig());
                return;
            }
        }
    }

    public void onRxtBlinkAll(ChangeColorEvent event) {
        log("onRxtBlinkAll");
        for (TCPClient t : tcpClients) {
            if (t.getUid().equals(event.getUid())) {
                t.sendMessage(DataParser.sendRxtBlinkColor(event.getColor(), event.getTileIdInt(), event.getTime(), event.getData()));
                return;
            }
        }
    }


}
