package com.example.myapplication;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AccountDataRequest {

    private static final String API_URL = "https://itsmenewbie03.is-a.dev/appt/api/accounts/employees/info";

    public interface ResponseCallback {
        void onSuccess(JSONObject response);
    }

    public void makeRequest(RequestQueue requestQueue, String accessToken, ResponseCallback callback) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (callback != null) {
                            callback.onSuccess(response); // Trigger callback with response
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API", "Error: " + error.networkResponse.statusCode + " | " + new String(error.networkResponse.data));
                        Log.e("API", "TOKEN: " + accessToken);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}
