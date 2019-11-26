package com.example.evnt;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * IdentProvider contains an instance of a SharedPreferences object.
 * This is used to cache user information which can more easily be accessed
 * using the context of the application or activity, rather than constantly
 * passing the values around as parameters.
 */
public class IdentProvider {

    private SharedPreferences pref;

    /**
     * Create a new IdentProvider object
     * will be identical to any other instance of the object
     * in terms of the values it contains and returns
     *
     * @param ctx The context used to create the object and fetch cached
     *            shared preferences.
     */
    public IdentProvider(Context ctx) {
        pref = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    /**
     * Uses a key to fetch a value from the shared preferences
     * @param key String key used to fetch the value
     * @return String containing value corresponding to the requested key
     */
    public String getValue(String key) {
        return pref.getString(key, null);
    }

    /**
     * Adds a new key-value pair to the shared preferences
     * @param key key for new pair
     * @param value value for new pair
     */
    public void setValue(String key, String value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
