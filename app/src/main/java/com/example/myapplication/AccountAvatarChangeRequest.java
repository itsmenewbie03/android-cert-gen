package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountAvatarChangeRequest {

    private final String endpoint = "https://itsmenewbie03.is-a.dev/appt/api/accounts/employees/change_avatar";

    public void changeAvatar(Context context, String b64_image, String accessToken, VolleyListener listener) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("base64_image", b64_image);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, endpoint, requestBody,
                    listener::onSuccess,
                    error -> {
                        try {
                            listener.onError(new JSONObject(new String(error.networkResponse.data)).getString("message"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + accessToken);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(request);

        } catch (JSONException e) {
            listener.onError(e.getMessage());
        }
    }

    public interface VolleyListener {
        void onSuccess(JSONObject response);

        void onError(String message);
    }
}
