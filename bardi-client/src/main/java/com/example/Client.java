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

            // Login
            System.out.print("Inserire login: ");
            String login = scanner.nextLine();
            out.println(login);

            String serverResponse = in.readLine();
            if ("OK".equals(serverResponse)) {
                System.out.println("Login eseguito con successo!");
            } else {
                System.out.println("Errore nel login: " + serverResponse);
                return; // uscita se login fallito
            }

            String command;
            while (true) {
                System.out.print("Inserisci un comando (ADD <message>, LIST, DEL <id>, QUIT): ");
                command = scanner.nextLine();
                out.println(command);

                if (command.startsWith("ADD")) {
                    // aspetta la risposta del server dopo aver mandato un messaggio
                    String response = in.readLine();
                    if (response.startsWith("OK ADDED")) {
                        System.out.println("Messaggio aggiunto con ID: " + response.split(" ")[2]);
                    } else {
                        System.out.println("Errore nell'aggiunta del messaggio: " + response);
                    }

                } else if (command.equals("LIST")) {
                    // aspetta la risposta del server dopo aver richiesto la lista
                    String response = in.readLine();
                    if (response.startsWith("BOARD:")) {
                        System.out.println("Lavagna:");
                        String message;
                        while (!(message = in.readLine()).equals("END")) {
                            System.out.println(message);
                        }
                    } else {
                        System.out.println("Errore nel recupero della lista: " + response);
                    }

                } else if (command.startsWith("DEL")) {
                    // aspetta la risposta del server dopo aver cancellato un messaggio
                    String response = in.readLine();
                    if (response.equals("OK DELETED")) {
                        System.out.println("Messaggio cancellato con successo.");
                    } else {
                        System.out.println("Errore nella cancellazione del messaggio: " + response);
                    }

                } else if (command.equals("QUIT")) {
                    String response = in.readLine();
                    System.out.println(response);
                    break;

                } else {
                    System.out.println("Comando non valido. Riprova.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
