package com.example.evnt;

import java.io.Serializable;

/**
 * Stores the information about a given event, images are
 * currently stored as drawable IDs
 *
 * TODO figure out how to store images associated with events using server calls (cache?)
 * TODO figure out how to get location and such to show map snippet
 */
public class EvntCardInfo implements Serializable {


    private String location;
    private String evnt_name;
    private String host_name;
    private String time;
    private String description;
    private String inORout;
    private int image;

    // We shouldn't have access to this directly
    private EvntCardInfo(EvntCardInfo.Builder builder) {
        location = builder.location;
        evnt_name = builder.evnt_name;
        host_name = builder.host_name;
        time = builder.time;
        description = builder.description;
        inORout = builder.inORout;
        image = builder.image;
    }

//    public EvntCardInfo () {
//        this.location = "";
//        this.evnt_name = "";
//        this.host_name = "";
//        this.time = "";
//        this.description = "";
//    }
//
//    public EvntCardInfo(String location, String evnt_name, String host_name, String time, String description, int image) {
//        this.location = location;
//        this.evnt_name = evnt_name;
//        this.host_name = host_name;
//        this.time = time;
//        this.description = description;
//        this.image = image;
//    }

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

    public static class Builder {
        // I really wish we had Lombok for this
        private String location;
        private String evnt_name;
        private String host_name;
        private String time;
        private String description;
        private String inORout;
        private int image;

        public Builder() {
            location = "";
            evnt_name = "";
            host_name = "Anonymous";
            time = "";
            description = "";
            inORout = "";
            image = R.drawable.chika;
        }

        public Builder withLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder withName(String name) {
            this.evnt_name = name;
            return this;
        }

        public Builder withHost(String host) {
            this.host_name = host;
            return this;
        }

        public Builder withTime(String time) {
            this.time = time;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withInOROut(String in) {
            this.inORout = in;
            return this;
        }

        public Builder withImage(int image) {
            this.image = image;
            return this;
        }

        public EvntCardInfo build() {
            return new EvntCardInfo(this);
        }
    }
}
