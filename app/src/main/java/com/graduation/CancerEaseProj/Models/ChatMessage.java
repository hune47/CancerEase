package com.graduation.CancerEaseProj.Models;

public class ChatMessage {
    private String sender_name, sender_id, message, type, date, time;

    public ChatMessage() {
    }

    public ChatMessage(String sender_name, String sender_id, String message, String type, String date, String time) {
        this.sender_name = sender_name;
        this.sender_id = sender_id;
        this.message = message;
        this.type = type;
        this.date = date;
        this.time = time;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
