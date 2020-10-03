package com.example.listify;


import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;

public class Requestor {

    private final String DEV_BASEURL = "https://datoh7woc9.execute-api.us-east-2.amazonaws.com/Development";

    AuthManager authManager;
    String apiKey;
    OkHttpClient client;

    Requestor(AuthManager authManager, String apiKey) {
        this.authManager = authManager;
        this.apiKey = apiKey;
        client = new OkHttpClient();
    }

    public <T> void getObject(String id, Class<T> classType, Receiver<T> receiver) {
        getObject(id, classType, receiver, null);
    }

    public <T> void getObject(String id, Class<T> classType, Receiver<T> successHandler, RequestErrorHandler failureHandler) {
        String getURL = DEV_BASEURL + "/" + classType.getSimpleName() + "?id=" + id;
        Request postRequest = buildBaseRequest(getURL, "GET", null);
        launchCall(postRequest, successHandler, classType, failureHandler);
    }

    public void postObject(Object toPost) throws JSONException {
        postObject(toPost, null);
    }

    public void postObject(Object toPost, RequestErrorHandler failureHandler) throws JSONException {
        String postURL = DEV_BASEURL + "/" + toPost.getClass().getSimpleName();
        Request postRequest = buildBaseRequest(postURL, "POST", new Gson().toJson(toPost));
        launchCall(postRequest, null, null, failureHandler);
    }


    private void launchCall(Request toLaunch, Receiver receiver, Class classType, RequestErrorHandler failureHandler) {
        client.newCall(toLaunch).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String responseString = response.body().string();
                if (receiver != null) {
                    if (classType == null) {
                        Log.e("Requestor Contract Error", "classType while receiver populated");
                    }
                    try {
                        receiver.acceptDelivery(new Gson().fromJson(responseString, classType));
                    } catch (JsonSyntaxException e) {
                        System.out.println(e);
                        Log.e("API response was not proper JSON", responseString);
                        throw new JsonSyntaxException(e);
                    }
                }
                Log.d("API Response", responseString);
            }
            @Override
            public void onFailure(@NotNull Call call, IOException e) {
                if (failureHandler != null) {
                    failureHandler.acceptError(e);
                } else {
                    Log.e("Network Error", e.getLocalizedMessage());
                }
            }
        });
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private Request buildBaseRequest(String url, String method, String bodyJSON) {
        Request.Builder requestBase = addAuthHeaders(new Request.Builder().url(url));
        if (method == "GET") {
            requestBase.get();
        } else {
            requestBase.method(method, RequestBody.create(bodyJSON, JSON));
        }
        return requestBase.build();
    }

    private Request.Builder addAuthHeaders(Request.Builder toAuthorize) {
        toAuthorize.addHeader("Authorization", authManager.getUserToken());
        toAuthorize.addHeader("X-API-Key", apiKey);
        return toAuthorize;
    }

    public interface Receiver<T> {
        void acceptDelivery(T delivered);
    }

    public interface RequestErrorHandler {
        void acceptError(IOException error);
    }
}
