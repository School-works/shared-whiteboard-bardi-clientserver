package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 3000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             Scanner scanner = new Scanner(System.in)) {

            String welcomeMessage = in.readLine();
            System.out.println(welcomeMessage);

            System.out.print("Inserire login ");
            String login = scanner.nextLine();
            out.println(login);

            String serverResponse = in.readLine();
            if (serverResponse.equals("OK")) {
                System.out.println("Login eseguito con successo!");
            }

            String command;
            while (true) {
                System.out.print("Inserisci un comando (ADD <message>, LIST, DEL <id>, QUIT): ");
                command = scanner.nextLine();
                out.println(command);

                if (command.startsWith("ADD")) {
                    String response = in.readLine();
                    System.out.println(response);
                    
                } else if (command.equals("LIST")) {
                    String messages = in.readLine();
                    System.out.println("Lavagna:\n" + messages);

                } else if (command.startsWith("DEL")) {
                    String response = in.readLine();
                    System.out.println(response);

                } else if (command.equals("QUIT")) {
                    String response = in.readLine();
                    System.out.println(response);
                    break;

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
