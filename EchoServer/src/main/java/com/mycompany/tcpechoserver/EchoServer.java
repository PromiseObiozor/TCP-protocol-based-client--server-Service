/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.tcpechoserver;

import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

/**
 *
 * @author promiseobiozor
 */
public class EchoServer {

    private static ServerSocket servSock;
    private static final int PORT = 12345;
    private static int iClientConnections = 0;
    private static List<Event> events = Collections.synchronizedList(new ArrayList<>());

    public static void main(String args[]) {
        System.out.println("Opening port...\n");
        try {
            servSock = new ServerSocket(PORT);
            System.out.println("Server ready....\n");
        } catch (IOException e) {
            System.out.println("Unable to attach port!");
            System.exit(1);
        }
        do {
            run();
        } while (true);
    }

    private static void run() {
        Socket link = null;
        try {
            link = servSock.accept();
            iClientConnections++;
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Client Message Received " + iClientConnections + ": " + message);

                if (message.equalsIgnoreCase("STOP")) {
                    out.println("TERMINATE");
                    break;
                }

                try {
                    String response = processMessage(message);
                    out.println(response);
                } catch (IncorrectActionException e) {
                    out.println("Error: " + e.getMessage());
                }
            }
            }catch (IOException e) {
            System.out.println("I/O Exception " + e);
        }finally {
            try {
                System.out.println("* Closing connection... *\n");
                if (link != null) {
                    link.close();
                }
            } catch (IOException e) {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
        }
     }
    
    

    private static String processMessage(String message) throws IncorrectActionException {
        String[] parts = message.split(",");
        if (parts.length != 4) {
            throw new IncorrectActionException("Invalid message format");
        }

        String action = parts[0].trim().toLowerCase();
        String dateStr = parts[1].trim();
        String timeStr = parts[2].trim().toUpperCase();
        String description = parts[3].trim();

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);   // Line 90 - 91 source: https://stackoverflow.com/questions/78698589/whats-the-point-of-locale-in-datetimeformatter/78700230#78700230
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h a", Locale.ENGLISH);

            LocalDate date = LocalDate.parse(dateStr, dateFormatter);
            LocalTime time = LocalTime.parse(timeStr, timeFormatter);
            LocalDateTime dateTime = LocalDateTime.of(date, time);

            synchronized (events) {
                switch (action) {
                    case "add":
                        events.add(new Event(dateTime, description));
                        break;
                    case "remove":
                        events.removeIf(e
                                -> e.getDateTime().equals(dateTime)
                                && e.getDescription().equals(description));
                        break;
                    default:
                        throw new IncorrectActionException("Invalid action: " + action);
                }

                List<Event> dateEvents = new ArrayList<>();
                for (Event event : events) {
                    if (event.getDateTime().toLocalDate().equals(date)) {
                        dateEvents.add(event);
                    }
                }

                dateEvents.sort(Comparator.comparing(Event::getDateTime));

                if (dateEvents.isEmpty()) {
                    return "No events scheduled for " + dateStr;
                }

                StringBuilder response = new StringBuilder(dateStr + ", ");
                for (Event event : dateEvents) {
                    response.append(event.getDateTime().format(DateTimeFormatter.ofPattern("h:mm a")))
                            .append(", ")
                            .append(event.getDescription())
                            .append(", ");
                }
                return response.toString().trim();
            }
        } catch (DateTimeParseException e) {
            throw new IncorrectActionException("Invalid date/time format");
        }
    }
}
