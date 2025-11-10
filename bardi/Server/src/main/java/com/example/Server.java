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

    public static String listMessages(List<Message> whiteboard) { // metto gli elementi dentro la whiteboard in una
                                                                  // stringa così da poter essere ritornata più facilmente e più comprensibilmente
        String totalWhiteboard = "";

        for (int i = 0; i < whiteboard.size(); i++) {
            totalWhiteboard = "[" + whiteboard.get(i).getId() + "] " + whiteboard.get(i).getAuthor() + ": "
                    + whiteboard.get(i).getText() + totalWhiteboard;
        }
        return totalWhiteboard;
    }

    public static boolean stringElementAlreadyExistsIn(ArrayList<String> list) { // semplice controllo su stringhe in una lista
        String temp = "";
        for (int i = 0; i < list.size(); i++) {
            if (temp == list.get(i)) {
                return true;
            } else {
                temp = list.get(i);
            }
        }
        return false;
    }

    public static boolean integerElementAlreadyExistsIn(ArrayList<Integer> list) { // uguale ma adattato per interi
        Integer temp = -1;
        for (int i = 0; i < list.size(); i++) {
            if (temp == list.get(i)) {
                return true;
            } else {
                temp = list.get(i);
            }
        }
        return false;
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
                ArrayList users = new ArrayList<String>();
                String login = in.readLine();
                if (login == null || stringElementAlreadyExistsIn(users)) { // se il login esiste oppure se qualcuno ha
                                                                            // già fatto login con stesso nome ERR LOGINREQUIRED
                    out.println("ERR LOGINREQUIRED");
                } else {
                    out.println("OK");

                }
                List<Message> whiteboard = Collections.synchronizedList(new ArrayList<>());
                ArrayList ids = new ArrayList<Integer>();
                Integer id = 0;

                while (login != null) {
                    ids.add(id);

                    String response = in.readLine();

                    String[] command = response.split(" ", 2);

                    Message message = new Message(id, login, command[1]);

                    switch (command[0]) {
                        case "ADD":
                            if (command[1].isEmpty()) { // se la length == 0 vuol dire che non c'è nulla, quindi ERR SYNTAX
                                out.println("ERR SYNTAX");
                            } else {
                                addMessage(message, whiteboard);
                                out.println("OK ADDED" + id);
                                id++;
                            }

                            break;
                        case "LIST":
                            String totalMes = listMessages(whiteboard);
                            if (totalMes.isEmpty()) { // se la length == 0 vuol dire che non c'è nulla, quindi la
                                                      // whiteboard è vuote e quindi BOARD: END
                                out.println("BOARD: END");
                            } else {
                                out.println(listMessages(whiteboard));

                            }
                            break;
                        case "DEL":
                            if (command[1].isEmpty()) { // se la length == 0 vuol dire che non c'è nulla, quindi ERR SYNTAX
                                out.println("ERR SYNTAX");
                            } else if (integerElementAlreadyExistsIn(ids)) {
                                out.println("ERR NOTFOUND");
                            } else {

                                delMessage(message, whiteboard);
                                out.println("OK DELETED");
                            }
                            break;
                        case "QUIT":
                            out.println("BYE");
                            clientSocket.close();
                            break;
                        default:
                            out.println("ERR UNKNOWNCMD"); // se nessuno dei comandi listati è inserito:
                            break;
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}