
package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResidentUpdateRequest {

    private final String endpoint = "https://itsmenewbie03.is-a.dev/appt/api/data/resident/update";

    public void updateUser(Context context, String target, CustomSpinnerItem update_data, String accessToken, VolleyListener listener) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("resident_id", target);
            JSONObject update_object = new JSONObject();
            update_object.put("first_name", update_data.getFirst_name());
            update_object.put("last_name", update_data.getLast_name());
            update_object.put("gender", update_data.getGender());
            update_object.put("address", update_data.getAddress());
            requestBody.put("update", update_object);
            // LOG the request body
            Log.d("Request Body", requestBody.toString());
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
