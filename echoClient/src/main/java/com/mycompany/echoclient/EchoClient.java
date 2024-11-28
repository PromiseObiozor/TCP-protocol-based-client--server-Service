/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.echoclient;

import java.net.*;
import java.io.*;

/**
 *
 * @author promiseobiozor
 */
public class EchoClient {

    private static InetAddress host;
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("Host ID not found!");
            System.exit(1);
        }
        run();
    }

    private static void run() {
        Socket link = null;
        try {
            link = new Socket(host, PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);
            BufferedReader userEntry = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Enter message in format: action; date; time; description");
            System.out.println("Example: add, 2 November 2024, 6 pm, Fireworks Dublin City Centre");
            System.out.println("Enter 'STOP' to exit\n");

            while (true) {
                String message;
                String response;

                System.out.print("Enter message: ");
                message = userEntry.readLine();
                out.println(message);
                response = in.readLine();

                if (response != null && response.equals("TERMINATE")) {
                    System.out.println("Connection terminated by server");
                } else {
                    System.out.println("\nServer response: " + response);
                }

                if (message.trim().equalsIgnoreCase("STOP")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("I/O Exception " + e);
        } finally {
            try {
                System.out.println("\n* Closing connection... *");
                if (link != null) {
                    link.close();
                }
            } catch (IOException e) {
                System.out.println("Unable to disconnect/close!");
                System.exit(1);
            }
        }
    }
}
