package com.example.evnt;

import java.io.Serializable;

public class EvntCardInfo implements Serializable {


    private String location;
    private String evnt_name;
    private String host_name;
    private String time;
    private String description;
    private String inORout;
    private int image;

    public EvntCardInfo () {
        this.location = "";
        this.evnt_name = "";
        this.host_name = "";
        this.time = "";
        this.description = "";
    }

    public EvntCardInfo(String location, String evnt_name, String host_name, String time, String description, int image) {
        this.location = location;
        this.evnt_name = evnt_name;
        this.host_name = host_name;
        this.time = time;
        this.description = description;
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEvnt_name() {
        return evnt_name;
    }

    public void setEvnt_name(String evnt_name) {
        this.evnt_name = evnt_name;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInORout() {
        return inORout;
    }

    public void setInORout(String inORout) {
        this.inORout = inORout;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
