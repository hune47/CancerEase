package com.graduation.CancerEaseProj.Models;

import java.io.Serializable;

public class ChatGroup implements Serializable {
    private int id, enrolled;
    private String name, image;

    public ChatGroup() {
    }

    public ChatGroup(String name, String image, int enrolled) {
        this.enrolled = enrolled;
        this.name = name;
        this.image = image;
    }

    public ChatGroup(int id, String name, String image, int enrolled) {
        this.id = id;
        this.enrolled = enrolled;
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(int enrolled) {
        this.enrolled = enrolled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
