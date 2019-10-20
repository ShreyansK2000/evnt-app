package com.example.evnt;

import android.os.AsyncTask;

import com.google.android.gms.common.util.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// TODO setup ALL api requests;
public class ServerRequestModule implements Serializable {
    private static final String url = "https://api.evnt.me";
    private String resultString;
//    private static String events = "events/";

    public String getRequest(String request) {

        return "";
    }
}
