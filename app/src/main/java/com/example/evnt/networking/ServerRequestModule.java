package com.example.evnt.networking;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.evnt.IdentProvider;
import com.example.evnt.R;
import com.example.evnt.adapters.EvntListAdapterCallback;
import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


// TODO setup ALL api requests;
public class ServerRequestModule implements Serializable {
    private static ServerRequestModule mInstance;
    private RequestQueue mRequestQueue;
    private Context context;
    private IdentProvider ident;
    private String token;

    private ServerRequestModule(Context context, IdentProvider ident) {
        this.context = context.getApplicationContext();
        this.ident = ident;
        this.token = ident.getValue(context.getString(R.string.access_token));
        mRequestQueue = getRequestQueue();
    }

    public static synchronized ServerRequestModule getInstance(Context context, IdentProvider ident) {
        // If instance is not available, create it. If available, reuse and return the object.
        if (mInstance == null) {
            mInstance = new ServerRequestModule(context, ident);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key. It should not be activity context,
            // or else RequestQueue won't last for the lifetime of your app
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    private void addToRequestQueue(Request req) {
        getRequestQueue().add(req);
    }

    public void getEventsRequest(String requestURL, final VolleyEventListCallback callback) {
        String url = requestURL + ident.getValue(context.getString(R.string.user_id));
        StringRequest stringBodyRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject res = null;
                        try {
                            res = new JSONObject(response);
                            JSONArray data = res.getJSONArray("data");
                            callback.onEventsListSuccessResponse(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Fail
                        error.printStackTrace();
                        callback.onErrorResponse(error.toString());
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("accessToken", token);
                params.put("userId", ident.getValue(context.getString(R.string.user_id)));
                return params;
            }
        };
        addToRequestQueue(stringBodyRequest);
    }

    public void markUserAttendance(String requestURL, final VolleyAttendanceCallback callback) {
        String url = requestURL + ident.getValue(context.getString(R.string.user_id));
        StringRequest stringBodyRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onAttendanceSuccessResponse();
                        // Also a hack, jesus this is all bad
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // Fail
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("accessToken", token);
                params.put("userId", ident.getValue(context.getString(R.string.user_id)));
                return params;
            }
        };
        addToRequestQueue(stringBodyRequest);
    }

    public void deleteEventsRequest(String requestURL, final EvntListAdapterCallback callback) {
        String url = requestURL;
        StringRequest stringBodyRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.deleteEvent();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("accessToken", token);
                params.put("userId", ident.getValue(context.getString(R.string.user_id)));
                return params;
            }
        };
        addToRequestQueue(stringBodyRequest);
    }

    public void getBestEvent(String requestURL, final VolleyBestEventCallback callback) {
        String url = requestURL + ident.getValue(context.getString(R.string.user_id));
        StringRequest stringBodyRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject res = null;
                        try {
                            res = new JSONObject(response);
                            callback.onReceivedBestEvent(res);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("accessToken", token);
                params.put("userId", ident.getValue(context.getString(R.string.user_id)));
                params.put("userLocation", "");
                return params;
            }
        };
        addToRequestQueue(stringBodyRequest);
    }
}

