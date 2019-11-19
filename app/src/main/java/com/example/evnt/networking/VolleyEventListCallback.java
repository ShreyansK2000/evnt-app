package com.example.evnt.networking;

import org.json.JSONArray;

public interface VolleyEventListCallback {
    void onEventsListSuccessResponse(JSONArray data);
    void onErrorResponse(String result);
}
