package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(3000);
            System.out.println("Server funzionante, aspettando client");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void addMessage(Message message, List<Message> whiteboard) {
        whiteboard.add(message);
    }

    public static void delMessage(Message message, List<Message> whiteboard) {
        whiteboard.remove(message);
    }

    public static String listMessages(List<Message> whiteboard) {
        String totalWhiteboard = "";

        for (int i = 0; i < whiteboard.size(); i++) {
            totalWhiteboard = "[" + whiteboard.get(i).getId() + "] " + whiteboard.get(i).getAuthor() + ": "
                    + whiteboard.get(i).getText() + totalWhiteboard;
        }
        return totalWhiteboard;
    }

    static class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("WELCOME");
                String login = in.readLine();
                if (login != null) {
                    out.println("OK");
                } else {

                }
                List<Message> whiteboard = Collections.synchronizedList(new ArrayList<>());
                int id = 0;
                while (login != null) {

                    String response = in.readLine();

                    String[] command = response.split(" ", 2);

                    Message message = new Message(id, login, command[1]);

                    switch (command[0]) {
                        case "ADD":
                            addMessage(message, whiteboard);
                            out.println("OK ADDED" + id);
                            id++;
                            break;
                        case "LIST":
                            out.println(listMessages(whiteboard));
                            break;
                        case "DEL":
                            delMessage(message, whiteboard);
                            out.println("OK DELETED");
                            break;
                        case "QUIT":
                            out.println("BYE");
                            clientSocket.close();
                            break;
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}