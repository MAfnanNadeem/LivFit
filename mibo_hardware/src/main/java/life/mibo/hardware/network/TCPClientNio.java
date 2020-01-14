package life.mibo.hardware.network;


import android.util.Log;

import com.samigehi.socket.SocketServer;
import com.samigehi.socket.Util;
import com.samigehi.socket.callback.AsyncSocket;
import com.samigehi.socket.callback.CompletedCallback;
import com.samigehi.socket.callback.ConnectCallback;
import com.samigehi.socket.callback.DataCallback;
import com.samigehi.socket.callback.DataEmitter;
import com.samigehi.socket.callback.WritableCallback;
import com.samigehi.socket.core.ByteBufferReader;

import java.util.Arrays;
import java.util.HashMap;

import life.mibo.hardware.core.Logger;
import life.mibo.hardware.encryption.Encryption;

public class TCPClientNio {

    private static TCPClientNio instance = null;
    // private HashMap<String, DataSink> hashMap;
    private HashMap<String, Client> hashMap;

    private TCPClientNio() {
        if (instance != null)
            Log.e("TCPClientNioTest", "instance is not null-------");
        hashMap = new HashMap<>();
        log("TCPClientNioTest started -- ");
    }

    public static TCPClientNio get() {
        if (instance == null)
            instance = new TCPClientNio();
        return instance;
    }

    public void add(String server, int port, String uid, OnMessageReceived listener) {
        log("TCPClientNio started -- " + uid);
        if (get(uid) == null) {
            Client client = new Client(server, port, uid, listener);
            put(uid, client);
        }
    }


    public void sendMessage(String uid, final byte[] message) throws Exception {
        log("TCPClientNio sendMessage1 -- " + Arrays.toString(message));
        Client client = get(uid);
        if (client == null)
            throw new Exception("Sending Message Client is Null");
        client.sendMessage(message);
    }

    public HashMap<String, Client> getMap() {
        return hashMap;
    }

    public void sendMessage(final byte[] message) throws Exception {
        log("TCPClientNio sendMessage3 -- " + Arrays.toString(message));
        for (HashMap.Entry<String, Client> client : hashMap.entrySet()) {
            client.getValue().sendMessage(message);
        }
    }

    public void stopClient(String id) {
        Client c = get(id);
        if (c != null)
            c.stop();
    }

    public void stopClient() {
        for (Client c : hashMap.values()) {
            c.stop();
        }
    }

    public void run(String uid) throws Exception {
        Client client = get(uid);
        if (client == null)
            throw new Exception("Starting Client is Null");
        client.start();
    }

    public interface OnMessageReceived {
        void messageReceived(byte[] message, String uid);
    }


    public static void log(String msg) {
        Logger.e("TCPClient-NIO", "Async: " + msg);
    }

    public void put(String uid, Client sink) {
        if (hashMap == null)
            hashMap = new HashMap<>();
        hashMap.put(uid, sink);
    }

    public Client get(String uid) {
        if (hashMap != null)
            return hashMap.get(uid);
        return null;
    }

    public class Client {

        private AsyncSocket asyncSocket;
        public String server = ""; // your computer IP
        public int serverPort = 0;
        private OnMessageReceived listener = null;
        private String uid;

        public Client(String server, int port, String Uid, OnMessageReceived listener) {
            this.listener = listener;
            this.server = server;
            serverPort = port;
            uid = Uid;
            log("TCPClientNio started -- ");
        }

        public void sendMessage(final byte[] message) {
            log("TCPClientNio sendMessage2 -- " + message);
            if (asyncSocket == null) {
                start();
                log("TCPClientNio asyncSocket -- NULL");
                return;
            }
            Encryption.mbp_encrypt(message, message.length);
            try {
                SocketServer.getDefault().post(new Runnable() {
                    @Override
                    public void run() {
                        Util.writeAll(asyncSocket, message, new CompletedCallback() {
                            @Override
                            public void onCompleted(Exception ex) {
                                log("TCPClient Nio write completed");
                                if (ex != null) {
                                    Logger.e("sendMessage ERORRR ---------- ", ex);
                                    start();
                                }
                            }
                        });
                    }
                });
            } catch (Exception e) {
                Logger.e("sendMessage ERORRR22222 ---------- ", e);
                start();
            }
        }

        public void stop() {
            if (asyncSocket != null && asyncSocket.getServer() != null)
                asyncSocket.getServer().stop();
            else
                log("AsyncServer socket is null while stopping ");
        }

        public void start() {
            // isRunning = true;
            log("SocketServer connecting.... "+server + " : "+serverPort);
            SocketServer.getDefault().connectSocket(server, serverPort, new ConnectCallback() {
                @Override
                public void onConnectCompleted(Exception ex, AsyncSocket socket) {
                    log("onConnectCompleted....");
                    if (ex != null) {
                        ex.printStackTrace();
                        Logger.e("TCPClient-New onConnectCompleted error ", ex);
                    }
                    if (socket != null) {
                        asyncSocket = socket;
                        socket.setDataCallback(new DataCallback() {
                            @Override
                            public void onDataAvailable(DataEmitter emitter, ByteBufferReader bb) {
                                log("AsyncSocket onDataAvailable....");
                                if (listener != null) {
                                    //if (bb.getAllByteArray().length >= MIN_COMMAND_LENGTH)
                                    listener.messageReceived(bb.getAllByteArray(), uid);
                                }
                            }
                        });

                        socket.setWriteableCallback(new WritableCallback() {
                            @Override
                            public void onWriteable() {
                                log("AsyncSocket onWriteable....");
                            }
                        });

                        socket.setClosedCallback(new CompletedCallback() {
                            @Override
                            public void onCompleted(Exception ex2) {
                                log("AsyncSocketc losed onCompleted ....");    // when connection closed
                            }
                        });

                        socket.setEndCallback(new CompletedCallback() {
                            @Override
                            public void onCompleted(Exception ex) {
                                log("AsyncSocket END onCompleted....");
                            }
                        });
                    }
                }
            });
        }
    }
}
