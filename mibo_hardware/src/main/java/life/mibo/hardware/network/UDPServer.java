package life.mibo.hardware.network;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import life.mibo.hardware.constants.CommunicationsConfig;

/**
 * Created by Fer on 19/03/2019.
 */

public class UDPServer {

    private Context context;
    private final int PortSend = CommunicationsConfig.UDP_PORT_SEND ;
    private final int PortReceive = CommunicationsConfig.UDP_PORT_RECEIVE;

    private DatagramSocket SendSocket;

    private Thread UDPThread;
    private boolean UDPRunning = true;


    private OnBroadcastReceived mBroadcastListener = null;

    public UDPServer(OnBroadcastReceived listener) {
        mBroadcastListener = listener;
    }


    @SuppressLint("NewApi")
    public void runUdpServer(final Activity context) {
        this.context = context;

        UDPRunning = true;

        UDPThread = new Thread("UDPThread") {
            @Override
            public void run() {

                Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
                final byte[] lMsg = new byte[64];
                try {
                    DatagramSocket ReceiveSocket = new DatagramSocket(null);
                    ReceiveSocket.setReuseAddress(true);
                    ReceiveSocket.bind(new InetSocketAddress(PortReceive));
                    while (UDPRunning) {
                        DatagramPacket msgPacket = new DatagramPacket(lMsg, lMsg.length);
                        ReceiveSocket.receive(msgPacket);
//                        String response = new String(lMsg, 0, lMsg.length);
//                        Log.e("udpR","ReciveUDP");
                        if (lMsg != null && mBroadcastListener != null) {
                            mBroadcastListener.broadcastReceived(lMsg ,msgPacket.getAddress());
                        }
                        Thread.sleep(20);

                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception e) {
                Log.e("udp", "Error", e);
                     }
            }
        };
        UDPThread.start();

    }

    public void stopUdpServer() {
        if(UDPThread != null)
            UDPRunning = false;
        mBroadcastListener = null;
    }

    //uid 6 byte, type 1 byte, command 1 byte, lenght 1 byte, data n bytes(lenght)
    public void sendMessage(InetAddress clientIp, byte[] uid, byte[] type, byte[] command, byte[] lenght, byte[] data){

       // send(outputStream.toByteArray( ),clientIp);
    }

    public boolean send(byte[] message, InetAddress clientIp) throws IllegalArgumentException {
        if(message == null )
            throw new IllegalArgumentException();
        // Create the send socket
        if(SendSocket == null) {
            try {
                SendSocket = new DatagramSocket();
            } catch (SocketException e) {
                Log.d("UDPSEND", "There was a problem creating the sending socket. Aborting.");
                e.printStackTrace();
                return false;
            }
        }

        // Build the packet
        DatagramPacket packet;
        byte[] data = message;
        packet = new DatagramPacket(data, data.length, clientIp, PortSend);

        try {
            SendSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public interface OnBroadcastReceived {
        void broadcastReceived(byte[] msg, InetAddress ip);
    }


}
