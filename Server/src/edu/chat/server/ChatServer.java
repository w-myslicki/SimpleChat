package edu.chat.server;

import edu.chat.network.TCPConnection;
import edu.chat.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer implements TCPConnectionListener {

    public static void main(String[] args) {
        new ChatServer();
    }

    private final List<TCPConnection> connectionList = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server is running!");

        try (ServerSocket serverSocket = new ServerSocket(8000)) {

            while(true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connectionList.add(tcpConnection);
        sendToAllConnection("Client connected: " +tcpConnection.toString());
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnection(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connectionList.remove(tcpConnection);
        sendToAllConnection("Client disconnected: " +tcpConnection.toString());
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception exception) {
        System.out.println("TCPConnection exception: " + exception);
    }

    private void sendToAllConnection(String value) {
        System.out.println("Message: " + value);

        for (int i = 0; i < connectionList.size(); i++) {
            connectionList.get(i).sendString(value);
        }
    }
}
