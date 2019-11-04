package com.example.evnt;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
    private String start_time;
    private String end_time;
    private String dateString;
    private String description;
    private String inORout;
    private int image;
    private static String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private String id;

    // We shouldn't have access to this directly
    private EvntCardInfo(EvntCardInfo.Builder builder) {
        location = builder.location;
        evnt_name = builder.evnt_name;
        host_name = builder.host_name;
        start_time = builder.start_time;
        end_time = builder.end_time;
        description = builder.description;
        inORout = builder.inORout;
        image = builder.image;
        id = builder.id;

        int start_date = 0;
        int end_date = 0;

        // Operations for date conversion from stored UTC format
        TimeZone tz = TimeZone.getDefault();
        System.out.println(start_time);
        DateFormat currentTZFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        currentTZFormat.setTimeZone(tz);
        Locale currentLocale = Locale.getDefault();

        Calendar cal = Calendar.getInstance();

        try {
            Date date = currentTZFormat.parse(start_time);
            cal.setTime(date);
            start_date = cal.get(Calendar.DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String start_AM_PM = (cal.get(Calendar.AM_PM) == Calendar.AM) ? " AM" : " PM";
        dateString = "On " + months[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.DATE) + " from "
                + cal.get(Calendar.HOUR) + ":"
                + String.format(currentLocale,"%02d", cal.get(Calendar.MINUTE)) + start_AM_PM;

        try {
            Date date = currentTZFormat.parse(end_time);
            cal.setTime(date);
//            cal.setTimeZone(tz);
            end_date = cal.get(Calendar.DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String end_AM_PM = (cal.get(Calendar.AM_PM) == Calendar.AM) ? " AM" : " PM";
        if (end_date == start_date || end_date == start_date + 1) {
            dateString = dateString + " to " + cal.get(Calendar.HOUR) + ":"
                         + String.format(currentLocale, "%02d", cal.get(Calendar.MINUTE))  + end_AM_PM;
        } else {
            dateString = dateString + " till " + months[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.DATE)
                         + " at " + cal.get(Calendar.HOUR) + ":"
                         + String.format(currentLocale, "%02d", cal.get(Calendar.MINUTE)) + end_AM_PM;
        }
    }

    public String getLocation() {
        return location;
    }

    public String getEvntName() {
        return evnt_name;
    }

    public String getHostName() {
        return host_name;
    }

    public String getStartTime() { return start_time; }

    public String getEndTime() { return end_time; }

    public String getDateString() { return dateString; }

    public String getInOrOut() {
        return inORout;
    }

    public String getDescription() {
        return description;
    }

    public int getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public static class Builder {
        // I really wish we had Lombok for this
        private String location;
        private String evnt_name;
        private String host_name;
        private String start_time;
        private String end_time;
        private String description;
        private String inORout;
        private int image;
        private String id;

        public Builder() {
            location = "";
            evnt_name = "";
            host_name = "Anonymous";
            start_time = "";
            end_time = "";
            description = "";
            inORout = "";
            image = R.drawable.chika;
            id = "";
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

        public Builder withStartTime(String time) {
            this.start_time = time;
            return this;
        }

        public Builder withEndTime(String time) {
            this.end_time = time;
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

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public EvntCardInfo build() {
            return new EvntCardInfo(this);
        }
    }
}
