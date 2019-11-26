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

    @ColumnInfo(name = "date_of_creation")
    private String date_of_creation;

    @ColumnInfo(name = "picture")
    private String picture;

    @ColumnInfo(name = "temperature")
    private ArrayList<Integer> temperature;
    @ColumnInfo(name = "humidity")
    private ArrayList<Integer> humidity;
    @ColumnInfo(name = "sunlight")
    private ArrayList<Integer> sunlight;

    @ColumnInfo(name = "measured_humidity")
    private int measured_humidity;

    @ColumnInfo(name = "date_of_creation")
    private int measured_temperature;

    @ColumnInfo(name = "picture")
    private int measured_sunlight;

    public Plant_Profile(String user_id, String plant_id,
                         String name_of_plant,
                         String picture, String date_of_creation, ArrayList<Integer> temperature,
                         ArrayList<Integer> humidity, ArrayList<Integer> sunlight,
                         int measured_humidity, int measured_temperature, int measured_sunlight) {
        this.user_id = user_id;
        this.plant_id = plant_id;
        this.name_of_plant = name_of_plant;
        this.date_of_creation = date_of_creation;
        this.picture = picture;
        this.temperature = temperature;
        this.humidity = humidity;
        this.sunlight = sunlight;
        this.measured_humidity = measured_humidity;
        this.measured_temperature = measured_temperature;
        this.measured_sunlight = measured_sunlight;
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

    public String getDate_of_creation() {
        return date_of_creation;
    }

    @Exclude
    public void setDate_of_creation(String date_of_creation) {
        this.date_of_creation = date_of_creation;
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


    public int getMeasured_humidity() {
        return measured_humidity;
    }

    @Exclude
    public void setMeasured_humidity(int measured_humidity) {
        this.measured_humidity = measured_humidity;
    }

    public int getMeasured_temperature() {
        return measured_temperature;
    }

    @Exclude
    public void setMeasured_temperature(int measured_temperature) {
        this.measured_temperature = measured_temperature;
    }

    public int getMeasured_sunlight() {
        return measured_sunlight;
    }

    @Exclude
    public void setMeasured_sunlight(int measured_sunlight) {
        this.measured_sunlight = measured_sunlight;
    }
}