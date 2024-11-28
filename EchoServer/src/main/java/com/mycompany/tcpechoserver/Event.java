/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.tcpechoserver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author promiseobiozor
 */
public class Event {
    private LocalDateTime dateTime;
    private String description;
    
    public Event(LocalDateTime dateTime, String description) {
        this.dateTime = dateTime;
        this.description = description;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy; h:mm a");
        return dateTime.format(formatter) + ", " + description;
    }
}

