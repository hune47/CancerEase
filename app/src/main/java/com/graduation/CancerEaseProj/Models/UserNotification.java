package com.graduation.CancerEaseProj.Models;

public class UserNotification {
    private int id, domain_id, level;
    private String  notification, url, video;

    public UserNotification() {
    }

    public UserNotification(int domain_id, int level, String notification, String url, String video) {
        this.domain_id = domain_id;
        this.level = level;
        this.notification = notification;
        this.url = url;
        this.video = video;
    }

    public UserNotification(int id, int domain_id, int level, String notification, String url, String video) {
        this.id = id;
        this.domain_id = domain_id;
        this.level = level;
        this.notification = notification;
        this.url = url;
        this.video = video;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDomain_id() {
        return domain_id;
    }

    public void setDomain_id(int domain_id) {
        this.domain_id = domain_id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
