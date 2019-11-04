package com.example.evnt;

import android.content.Context;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;

public class IdentProvider {

    private SharedPreferences pref;

    public IdentProvider(Context ctx) {
        pref = PreferenceManager.getDefaultSharedPreferences(ctx);
    }
    // TODO consolidate this into a shared storage manager, probably

    public String getValue(String value) {
        return pref.getString(value, null);
    }

    public void setValue(String key, String value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
