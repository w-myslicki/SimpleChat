package edu.chat.client;

import edu.chat.network.TCPConnection;
import edu.chat.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP ="..."; //IPv4-адрес
    private static final int PORT = 8000;

    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldName = new JTextField();
    private final JTextField fieldMessage = new JTextField();

    private  TCPConnection connection;

    private  ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        add(fieldName, BorderLayout.NORTH);
        add(fieldMessage, BorderLayout.SOUTH);
        fieldMessage.addActionListener(this);

        setVisible(true);

        try {
            connection = new TCPConnection(this,IP,PORT);
        } catch (IOException e) {
            printMessage("Connection exception: "  + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = fieldMessage.getText();

        if(message.equals("")) return;

        fieldMessage.setText(null);
        connection.sendString(fieldName.getText() + ":" + message);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection close!");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception exception) {
        printMessage("Connection exception: "  + exception);
    }

    private synchronized void printMessage(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(message + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
