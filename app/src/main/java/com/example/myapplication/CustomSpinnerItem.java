package com.example.myapplication;

import androidx.annotation.NonNull;

public class CustomSpinnerItem {
    private String id;
    private String name;
    private String first_name;
    private String last_name;
    private String middle_name;
    private String age;
    private String gender;

    private String address;

    public CustomSpinnerItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public CustomSpinnerItem(String id, String name, String first_name, String last_name, String middle_name, String age, String gender, String address) {
        this.id = id;
        this.name = name;
        this.first_name = first_name;
        this.last_name = last_name;
        this.middle_name = middle_name;
        this.age = age;
        this.gender = gender;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
