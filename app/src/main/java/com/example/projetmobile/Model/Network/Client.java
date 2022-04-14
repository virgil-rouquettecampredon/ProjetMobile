package com.example.projetmobile.Model.Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {
    //connection to server with socket using TCP protocol
    private Socket socket;
    private String ip;
    private int port;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(ip, port);
    }

    public void disconnect() throws IOException {
        socket.close();
    }

    public void send(String message) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
        out.write(message);
        out.flush();
    }

    public String receive() throws IOException {
        InputStreamReader in = new InputStreamReader(socket.getInputStream());
        BufferedReader br = new BufferedReader(in);
        return br.readLine();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }


}
