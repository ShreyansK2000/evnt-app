package com.example.evnt.networking;

import org.json.JSONObject;

public interface VolleyBestEventCallback {
    void onReceivedBestEvent(JSONObject response);
    void onErrorResponse(String error);
}
