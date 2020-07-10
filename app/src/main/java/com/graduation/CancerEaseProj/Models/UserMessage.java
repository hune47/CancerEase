package com.graduation.CancerEaseProj.Models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class UserMessage {
    private int id, seen, type;
    private  String subject, message, sender, receiver;
    @ServerTimestamp
    private Timestamp timestamp;

    public UserMessage() {
    }

    public UserMessage(int id, int seen, int type, String subject, String message, String sender, String receiver) {
        this.id = id;
        this.seen = seen;
        this.type = type;
        this.subject = subject;
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    public UserMessage(String subject, String message, String sender, String receiver, int seen, int type) {
        this.seen = seen;
        this.type = type;
        this.subject = subject;
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
