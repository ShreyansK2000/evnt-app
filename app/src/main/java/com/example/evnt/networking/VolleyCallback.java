package com.example.evnt.networking;

import org.json.JSONArray;

public interface VolleyCallback {
    void onEventsListSuccessResponse(JSONArray data);
    void onErrorResponse(String result);
}
