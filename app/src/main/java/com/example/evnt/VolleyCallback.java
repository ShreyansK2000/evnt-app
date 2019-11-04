package com.example.evnt;

import org.json.JSONArray;

public interface VolleyCallback {

    void onEventsListSuccessResponse(JSONArray data);
    void onErrorResponse(String result);
}
