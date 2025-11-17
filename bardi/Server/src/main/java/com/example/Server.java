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

    public static void delMessage(int messageId, List<Message> whiteboard) {
        whiteboard.removeIf(message -> message.getId() == messageId);
    }

    public static String listMessages(List<Message> whiteboard) {
        StringBuilder totalWhiteboard = new StringBuilder();

        for (Message message : whiteboard) {
            totalWhiteboard.append("[").append(message.getId()).append("] ")
                           .append(message.getAuthor()).append(": ")
                           .append(message.getText()).append("\n");
        }
        return totalWhiteboard.toString();
    }

    static class ClientHandler extends Thread {
        private Socket clientSocket;
        private static List<String> loggedInUsers = Collections.synchronizedList(new ArrayList<>());
        private static List<Message> whiteboard = Collections.synchronizedList(new ArrayList<>());
        private static int lastMessageId = 0;

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
                
                if (login == null || loggedInUsers.contains(login)) {
                    out.println("ERR LOGINREQUIRED");
                    clientSocket.close();
                    return;
                }

                loggedInUsers.add(login);
                out.println("OK");

                String response;
                while ((response = in.readLine()) != null) {
                    String[] command = response.split(" ", 2);
                    Message message = null;

                    if (command.length > 1) {
                        message = new Message(lastMessageId, login, command[1]);
                    }

                    switch (command[0]) {
                        case "ADD":
                            if (command.length < 2 || command[1].isEmpty()) {
                                out.println("ERR SYNTAX");
                            } else {
                                addMessage(message, whiteboard);
                                out.println("OK ADDED " + lastMessageId);
                                lastMessageId++;
                            }
                            break;

                        case "LIST":
                            String totalMessages = listMessages(whiteboard);
                            if (totalMessages.isEmpty()) {
                                out.println("BOARD: END");
                            } else {
                                out.println("BOARD:");
                                out.print(totalMessages);
                                out.println("END");
                            }
                            break;

                        case "DEL":
                            if (command.length < 2) {
                                out.println("ERR SYNTAX");
                            } else {
                                try {
                                    int messageId = Integer.parseInt(command[1]);
                                    boolean messageFound = false;

                                    for (Message m : whiteboard) {
                                        if (m.getId() == messageId && m.getAuthor().equals(login)) {
                                            delMessage(messageId, whiteboard);
                                            out.println("OK DELETED");
                                            messageFound = true;
                                            break;
                                        }
                                    }
                                    if (!messageFound) {
                                        out.println("ERR PERMISSION");
                                    }
                                } catch (NumberFormatException e) {
                                    out.println("ERR SYNTAX");
                                }
                            }
                            break;

                        case "QUIT":
                            out.println("BYE");
                            loggedInUsers.remove(login);
                            clientSocket.close();
                            return;

                        default:
                            out.println("ERR UNKNOWNCMD");
                            break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
