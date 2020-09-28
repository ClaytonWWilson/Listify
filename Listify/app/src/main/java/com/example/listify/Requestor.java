package com.example.listify;


import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Requestor {

    private final String DEV_BASEURL = "https://datoh7woc9.execute-api.us-east-2.amazonaws.com/Development";

    AuthManager authManager;
    RequestQueue queue;
    String apiKey;

    Requestor(Context context, AuthManager authManager, String apiKey) {
        queue = Volley.newRequestQueue(context);
        this.authManager = authManager;
        this.apiKey = apiKey;
    }

    public <T> void getObject(String id, Class<T> classType, Receiver<T> receiver) {
        String getURL = DEV_BASEURL + "/" + classType.getSimpleName() + "?id=" + id;
    }

    public void postObject(Object toPost, Response.ErrorListener failureHandler) throws JSONException {
        String postURL = DEV_BASEURL + "/" + toPost.getClass().getSimpleName();
        queue.add(buildRequest(postURL, toPost, null, failureHandler));
    }

    public void postObject(Object toPost) throws JSONException {
        postObject(toPost, null);
    }

    private JsonObjectRequest buildRequest(String url, Object toJSONify, Response.Listener<JSONObject> successHandler, Response.ErrorListener failureHandler) throws JSONException {
        return buildRequest(url, new JSONObject(new Gson().toJson(toJSONify)), successHandler, failureHandler);
    }

    private JsonObjectRequest buildRequest(String url, JSONObject jsonBody, Response.Listener<JSONObject> successHandler, Response.ErrorListener failureHandler) {
        return new JsonObjectRequest(url, jsonBody, successHandler, failureHandler) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                System.out.println(authManager.getUserToken());
                headers.put("Authorization", authManager.getUserToken());
                headers.put("Content-Type", "application/json");
                headers.put("X-API-Key", apiKey);
                return headers;
            }
        };
    }

    public class Receiver<T> {
        public void acceptDelivery(T delivered) {

        }
    }
}
