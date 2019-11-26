package com.example.evnt;

/**
 * Callback used by the pick event fragment in order to perform
 * an action when the app is able to retrieve the user's location
 * using google play services
 */
public interface GetLocationCallback {
    /**
     * Pass the acquired location as a string to the callback action
     * @param location String to add to event and request header for complex logic
     */
    void gotLocationString(String location);
}
