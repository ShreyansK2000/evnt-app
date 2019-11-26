package com.example.evnt;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * EvntCardInfo class is a model to store all the relevant information about
 * an event. This is an immutable class, i.e. events cannot be modified directly
 * on the front end - REST API calls should be used to modify the event on the
 * backend and retrieve the event information again to construct a new EvntCardInfo
 * object.
 *
 * Some fields are not currently in use, they were either planned features or
 * added to express posibility of features we could add if we further develop this
 * application
 *
 */
public class EvntCardInfo implements Serializable {

    private String location;
    private String evnt_name;
    private String host_name;
    private String host_id;
    private String start_time;
    private String end_time;
    private String dateString;
    private String description;
    private String inORout;
    private int image;
    private String id;
    private List<String> tag_list;

    private EvntCardInfo(EvntCardInfo.Builder builder) {

        /* All the information from the builder is assumed to be safe */
        location = builder.location;
        evnt_name = builder.evnt_name;
        host_id = builder.host_id;
        host_name = builder.host_name;
        start_time = builder.start_time;
        end_time = builder.end_time;
        description = builder.description;
        inORout = builder.inORout;
        id = builder.id;
        tag_list = builder.tags;

        /*
         * Assign image resource files based on certain commong tags
         * The common tags are currently decided arbitrarily. A better
         * analysis in the future can help improve this
         */
        Map<String, Integer> defaultImages = new HashMap<String, Integer>() {{
            put("sports", R.drawable.sports);
            put("party", R.drawable.party);
            put("games", R.drawable.games);
        }};
        image = builder.tags.isEmpty() || !defaultImages.containsKey(builder.tags.get(0)) ?
                R.drawable.random : defaultImages.get(builder.tags.get(0));

        /*
         * Here we perform the operation to parse the date timestamps into
         * more human readable information using various time related util classes
         */
        int start_date = 0;
        int end_date = 0;
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                           "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        TimeZone tz = TimeZone.getDefault();
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

    /*
     * Getter methods for instance fields for event information
     */

    public String getLocation() {
        return location;
    }

    public String getEvntName() {
        return evnt_name;
    }

    public String getHostId() {
        return host_id;
    }

    public String getHostName() {
        return host_name;
    }

    public String getStartTime() {
        return start_time;
    }

    public String getEndTime() {
        return end_time;
    }

    public String getInOrOut() {
        return inORout;
    }

    public String getDateString() {
        return dateString;
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

    public List<String> getTagList() {
        return tag_list;
    }

    /**
     * We use an internal builder class to create
     * Event info card objects since there were various parameters to be used
     * and the safest way to assign them correctly is to use a builder model.
     *
     * We also want our Event info card objects to be immutable and the builder
     * allows us to enforce that requirement.
     */
    public static class Builder {

        private String location;
        private String evnt_name;
        private String host_name;
        private String host_id;
        private String start_time;
        private String end_time;
        private String description;
        private String inORout;
        private int image;          // currently unused due to how we are assigning images (see above)
        private String id;
        private List<String> tags;

        /**
         * Builder constructor which initializes all
         * the field instances to default values in case the information
         * is not available or used for a given event. In most situations,
         * these values should not be used.
         */
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
            tags = new ArrayList<String>();
        }

        public Builder withLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder withName(String name) {
            this.evnt_name = name;
            return this;
        }

        public Builder withHostId(String host_id) {
            this.host_id = host_id;
            return this;
        }

        public Builder withHostName(String host_name) {
            this.host_name = host_name;
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

        public Builder withTagList(String[] tags) {
            this.tags = new ArrayList<String>(Arrays.asList(tags));
            return this;
        }

        public EvntCardInfo build() {
            return new EvntCardInfo(this);
        }
    }
}
