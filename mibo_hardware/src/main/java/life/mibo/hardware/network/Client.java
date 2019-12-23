package life.mibo.hardware.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class Client {

    private SocketChannel client = null;
    private InetSocketAddress socketAddress = null;
    private MyThread thread = null;

    public Client() {
    }

    public void makeConnection(String server, int port, String uid) {
        int result = 0;
        try {
            client = SocketChannel.open();
            socketAddress = new InetSocketAddress(server, port);
            client.connect(socketAddress);
            client.configureBlocking(false);
            receiveMessage();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while ((result = sendMessage()) != -1) {
        }

        try {
            client.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int sendMessage() {
        System.out.println("Inside SendMessage");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String msg = null;
        ByteBuffer bytebuf = ByteBuffer.allocate(1024);
        int nBytes = 0;
        try {
            msg = in.readLine();
            System.out.println("msg is " + msg);
            bytebuf = ByteBuffer.wrap(msg.getBytes());
            nBytes = client.write(bytebuf);
            System.out.println("nBytes is " + nBytes);
            if (msg.equals("quit") || msg.equals("shutdown")) {
                System.out.println("time to stop the client");
                interruptThread();
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                client.close();
                return -1;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Wrote " + nBytes + " bytes to the server");
        return nBytes;
    }

    public void receiveMessage() {
        thread = new MyThread("Receive THread", client);
        thread.start();

    }

    public void interruptThread() {
        thread.val = false;
    }

    public static void start(String server, int port, String uid) {
        Client cl = new Client();
        cl.makeConnection(server, port, uid);
    }

    public class MyThread extends Thread {

        public SocketChannel sc = null;
        public boolean val = true;

        public MyThread(String str, SocketChannel client) {
            super(str);
            sc = client;
        }

        public void run() {

            System.out.println("Inside receivemsg");
            int nBytes = 0;
            ByteBuffer buf = ByteBuffer.allocate(2048);
            try {
                while (val) {
                    while ((nBytes = nBytes = client.read(buf)) > 0) {
                        buf.flip();
                        Charset charset = Charset.forName("us-ascii");
                        CharsetDecoder decoder = charset.newDecoder();
                        CharBuffer charBuffer = decoder.decode(buf);
                        String result = charBuffer.toString();
                        System.out.println(result);
                        buf.flip();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}