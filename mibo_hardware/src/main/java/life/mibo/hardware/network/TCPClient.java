package life.mibo.hardware.network;

import android.os.Process;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import life.mibo.hardware.core.DataParser;
import life.mibo.hardware.core.Logger;
import life.mibo.hardware.encryption.Encryption;

import static life.mibo.hardware.constants.Config.MIN_COMMAND_LENGTH;


/**
 * Created by Fer on 18/03/2019.
 * <p>
 * Updated By
 * Sumeet (samigehi)
 * 03/12/2019
 */

public class TCPClient {

    public String serverIp;
    public int serverPort;
    private byte[] serverMessage;
    private OnMessageReceived listener;
    private boolean isRunning = false;
    private boolean isStopped = false;
    private DataOutputStream mBufferOut;
    private DataInputStream mBufferIn;
    private String uid;
    private Thread thread;
    private Socket mSocket;
    private int type = DataParser.BOOSTER;
    private Object object;

    public boolean isRxl() {
        return type == DataParser.RXL;
    }

    public int getType() {
        return type;
    }

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages
     * received from server
     */
    public TCPClient(String ip, int port, String Uid, int rxl, OnMessageReceived listener) {
        this.listener = listener;
        this.serverIp = ip;
        serverPort = port;
        uid = Uid;
        this.type = rxl;
    }

    public String getServerIp() {
        return serverIp;
    }

    public String getUid() {
        return uid;
    }

    public void sendMessage(byte[] message, String tag) {
        log(tag+" char: " + Arrays.toString(new String(message).toCharArray()));
        sendMessage(message);
        log( tag+" byte: " + Arrays.toString(message));
    }

    public void sendMessage(byte[] message) {
        log(" sendMessage char: " + Arrays.toString(new String(message).toCharArray()));

        Encryption.mbp_encrypt(message, message.length);

        // Encryption.mbp_decrypt(message, message.length);
        if (mBufferOut != null) {
            try {
                //Log.e("TCP Client", "send 1");
                mBufferOut.write(message);
                mBufferOut.flush();
                log("sendMessage sent");
                //Log.e("TCP Client", "send 2");
            } catch (Exception e) {
                Logger.e("TCPClient sendMessage IOException", e);
                stopClient();
                run();
                e.printStackTrace();
            }
        } else {
            log("TCPClient mBufferOut is dead or connection not started");
        }
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {
        Logger.i("TCPClient stopClient " + uid);
        isStopped = true;
        isRunning = false;
        if (mBufferOut != null) {
            try {
                mBufferOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            mSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //listener = null;
        mBufferIn = null;
        mBufferOut = null;
        serverMessage = null;
        //thread.interrupt();
        //stop();
    }

    public void startClient() {
        run();
    }

    public void run() {
        Logger.i("TCPClient running IP " + serverIp + ", UID " + uid);

        isStopped = false;
        isRunning = true;

        thread = new Thread("TCPThread") {
            @Override
            public void run() {

                Process.setThreadPriority(Process.THREAD_PRIORITY_MORE_FAVORABLE);
                try {
                    // here you must put your computer's IP address.
                    InetAddress serverAddr = InetAddress.getByName(serverIp);

                    log("C: Connecting... " + serverIp);

                    mSocket = new Socket(serverAddr, serverPort);
                    try {
                        mSocket.setKeepAlive(true);
                        //mSocket.setSoTimeout(500);
                        mSocket.setTcpNoDelay(true);
                        mSocket.setKeepAlive(true);
                        mSocket.setSoTimeout(0);
                        // sends the message to the server
                        mBufferOut = new DataOutputStream(mSocket.getOutputStream());
                        // receives the message which the server sends back
                        mBufferIn = new DataInputStream((
                                mSocket.getInputStream()));

                        // server
                        while (isRunning) {

                            int bytesNum = mBufferIn.available();
                           // Logger.e("TCPClient MessageReceived bytesNum " +bytesNum);
                            if (bytesNum >= MIN_COMMAND_LENGTH) {
                                // Log.e("runtcp", "num "+bytesNum);
                                serverMessage = new byte[bytesNum];
                                int r = mBufferIn.read(serverMessage);
                                log("TCPClient MessageReceived " + new String(serverMessage) + " : " + r);
                                if (serverMessage != null && listener != null) {
                                    listener.messageReceived(serverMessage, uid);
                                }
                            } else {
                                //   Logger.e("TCPClient Message Bytes  " + bytesNum);
                            }
                            Thread.sleep(20);
                        }

                    } catch (Exception e) {

                        log("  Exception 1" + e.getMessage());

                    } finally {
                        mSocket.close();
                    }

                } catch (Exception e) {

                    log("Exception 2 " + e);

                }
            }
        };
        thread.start();


    }

    public interface OnMessageReceived {
        void messageReceived(byte[] message, String uid);
    }


    // ExecutorService executor = Executors.newFixedThreadPool(8);
    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static void add(TCPClient client) {
        if (client != null)
            executor.submit(client.thread);
    }

    public static void onDestroy() {
        try {
            executor.shutdown();
        } catch (Exception e) {
            Logger.e("TCPClient could not shutdown executor ", e);
        }
    }

    public boolean isAvailable() {
        boolean available = false;
        try {
            if (mSocket != null)
                available = !mSocket.isClosed();

        } catch (Exception e) {

        }

        return available;
    }

    public void setDevice(Object object) {
        this.object = object;
    }

    public Object getDevice() {
        return object;
    }

    public boolean isStopped() {
        return isStopped;
    }

    void log(String msg) {
        Logger.e("TCPClient", msg);
    }
}

