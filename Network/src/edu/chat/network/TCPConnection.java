package edu.chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket;
    private final Thread thread;
    private final TCPConnectionListener tcpListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection (TCPConnectionListener tcpListener, String ip, int port) throws IOException {
        this(tcpListener, new Socket(ip, port));
    }

    public TCPConnection(TCPConnectionListener tcpListener, Socket socket) throws IOException {
        this.tcpListener = tcpListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    tcpListener.onConnectionReady(TCPConnection.this);

                    while(!thread.isInterrupted()) {
                        tcpListener.onReceiveString(TCPConnection.this, in.readLine());
                    }
                } catch (IOException e) {
                    tcpListener.onException(TCPConnection.this, e);
                } finally {
                    tcpListener.onDisconnect(TCPConnection.this);
                }
            }
        });

        thread.start();
    }

    public synchronized void sendString (String value) {
        try {
            out.write(value +"\r\n");
            out.flush();
        } catch (IOException e) {
            tcpListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            tcpListener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
