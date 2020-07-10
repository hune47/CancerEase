package com.graduation.CancerEaseProj.Models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class DoctorNotification {
    private int status;
    private String recordNo, email, message, title;
    @ServerTimestamp
    private Timestamp timestamp;

    public DoctorNotification() {
    }

    public DoctorNotification(String recordNo, String email, int status, String message, String title) {
        this.recordNo = recordNo;
        this.email = email;
        this.status = status;
        this.message = message;
        this.title = title;
    }

    public String getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(String recordNo) {
        this.recordNo = recordNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
