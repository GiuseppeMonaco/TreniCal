package it.trenical.client;

import it.trenical.client.auth.AuthManager;
import it.trenical.client.auth.GrpcAuthManager;
import it.trenical.client.gui.MainFrame;

import javax.swing.*;

public class Client {
    public static void main(String[] args) {
        AuthManager auth = new GrpcAuthManager();

        JFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
