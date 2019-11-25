package com.example.aiplant.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;

@Entity
public class Plant_Profile {


    @PrimaryKey
    private String user_id;

    @ColumnInfo(name = "plant_id")
    private String plant_id;

    @ColumnInfo(name = "name_of_plant")
    private String name_of_plant;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "picture")
    private String picture;

    @ColumnInfo(name = "temperature")
    private ArrayList<Integer> temperature;
    @ColumnInfo(name = "humidity")
    private ArrayList<Integer> humidity;
    @ColumnInfo(name = "sunlight")
    private ArrayList<Integer> sunlight;

    public Plant_Profile(String user_id, String plant_id,
                         String name_of_plant, String description,
                         String picture, ArrayList<Integer> temperature,
                         ArrayList<Integer> humidity, ArrayList<Integer> sunlight) {
        this.user_id = user_id;
        this.plant_id = plant_id;
        this.name_of_plant = name_of_plant;
        this.description = description;
        this.picture = picture;
        this.temperature = temperature;
        this.humidity = humidity;
        this.sunlight = sunlight;
    }

    public String getUser_id() {
        return user_id;
    }

    @Exclude
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPlant_id() {
        return plant_id;
    }

    @Exclude
    public void setPlant_id(String plant_id) {
        this.plant_id = plant_id;
    }

    public String getName_of_plant() {
        return name_of_plant;
    }

    @Exclude
    public void setName_of_plant(String name_of_plant) {
        this.name_of_plant = name_of_plant;
    }

    public String getDescription() {
        return description;
    }

    @Exclude
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    @Exclude
    public void setPicture(String picture) {
        this.picture = picture;
    }

    public ArrayList<Integer> getTemperature() {
        return temperature;
    }

    @Exclude

    public void setTemperature(ArrayList<Integer> temperature) {
        this.temperature = temperature;
    }

    public ArrayList<Integer> getHumidity() {
        return humidity;
    }

    @Exclude

    public void setHumidity(ArrayList<Integer> humidity) {
        this.humidity = humidity;
    }

    public ArrayList<Integer> getSunlight() {
        return sunlight;
    }

    @Exclude
    public void setSunlight(ArrayList<Integer> sunlight) {
        this.sunlight = sunlight;
    }

}