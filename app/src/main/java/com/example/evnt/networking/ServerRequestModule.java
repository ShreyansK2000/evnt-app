package com.example.evnt.networking;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.evnt.IdentProvider;
import com.example.evnt.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


// TODO setup ALL api requests;
public class ServerRequestModule implements Serializable {
//    private static final String apiURL = "https://api.evnt.me";
    private static ServerRequestModule mInstance;
    private static Context context;
    private RequestQueue mRequestQueue;
    private static IdentProvider ident;
//    private String resultString;

    private ServerRequestModule(Context context, IdentProvider ident) {
        this.context = context;
        this.ident = ident;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized ServerRequestModule getInstance(Context context, IdentProvider ident) {
        // If instance is not available, create it. If available, reuse and return the object.
        if (mInstance == null) {
            mInstance = new ServerRequestModule(context, ident);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key. It should not be activity context,
            // or else RequestQueue won't last for the lifetime of your app
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public  void addToRequestQueue(Request req) {
        getRequestQueue().add(req);
    }

    public void getEventsRequest(String requestURL, final VolleyCallback callback) {
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
        );
        addToRequestQueue(stringBodyRequest);
    }
}
