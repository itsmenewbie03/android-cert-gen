package com.example.myapplication;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String firstName;
    private String lastName;
    private String gender;
    private String address;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject info = new JSONObject();
        info.put("first_name", firstName.trim());
        info.put("last_name", lastName.trim());
        info.put("gender", gender.trim().toLowerCase());
        info.put("address", address.trim());
        // TODO: make this dynamic
        info.put("phone_number", "1234567890");
        info.put("date_of_birth", "01/01/1980");
        info.put("period_of_residency", "20 years");
        return info;
    }
}
