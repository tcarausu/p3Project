package com.example.aiplant.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey
    private String id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "picture")
    private String picture;
    @ColumnInfo(name = "number_of_plants")
    private int number_of_plants;
    @ColumnInfo(name = "birthday")
    private String birthday;


    public User(String id, String name, String email, String picture, int number_of_plants, String birthday) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.number_of_plants = number_of_plants;
        this.birthday = birthday;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getNumber_of_plants() {
        return number_of_plants;
    }

    public void setNumber_of_plants(int number_of_plants) {
        this.number_of_plants = number_of_plants;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
