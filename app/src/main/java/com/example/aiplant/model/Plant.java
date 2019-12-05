package com.example.aiplant.model;

import android.graphics.drawable.Drawable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;

@Entity
public class Plant {

    @PrimaryKey
    private String plant_id;
    @ColumnInfo(name = "name")
    private String plant_name;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "picture_url")
    private String picture_url;
    @ColumnInfo(name = "age")
    private int age;
    @ColumnInfo(name = "temperature")
    private ArrayList<Integer> temperature;
    @ColumnInfo(name = "humidity")
    private ArrayList<Integer> humidity;
    @ColumnInfo(name = "sunlight")
    private ArrayList<Integer> sunlight;


    public Plant(){

    }

    public Plant(String plant_id, String plant_name, String description, String picture_url,
                 ArrayList<Integer> humidity, ArrayList<Integer> temperature,ArrayList<Integer> sunlight) {

        this.plant_id = plant_id;
        this.plant_name = plant_name;
        this.description = description;
        this.picture_url = picture_url;
        this.temperature = temperature;
        this.humidity = humidity;
        this.sunlight = sunlight;
    }

    private int minTemperature, maxTemperature, minHumidity, maxHumidity, minSunLight, maxSunlight;
    private Drawable image ;


    public Plant(String name, int age, Drawable image) {
        this.plant_name = name;
        this.age = age;
        this.image = image;
    }

    public Plant(String name, int age, int minTemperature, int maxTemperature, int minHumidity, int maxHumidity, int minSunLight, int maxSunlight) {
        this.plant_name = name;
        this.age = age;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
        this.minSunLight = minSunLight;
        this.maxSunlight = maxSunlight;
    }

    public Plant(String name, int age) {
        this.plant_name = name;
        this.age = age;
    }

    public String getPlant_id() {
        return plant_id;
    }

    @Exclude
    public void setPlant_id(String plant_id) {
        this.plant_id = plant_id;
    }

    public String getPlant_name() {
        return plant_name;
    }
    @Exclude
    public void setPlant_name(String plant_name) {
        this.plant_name = plant_name;
    }

    public String getDescription() {
        return description;
    }
    @Exclude
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture_url() {
        return picture_url;
    }
    @Exclude
    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public int getAge() {
        return age;
    }
    @Exclude
    public void setAge(int age) {
        this.age = age;
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

    public int getMinTemperature() {
        return minTemperature;
    }
    @Exclude
    public void setMinTemperature(int minTemperature) {
        this.minTemperature = minTemperature;
    }

    public int getMaxTemperature() {
        return maxTemperature;
    }
    @Exclude
    public void setMaxTemperature(int maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public int getMinHumidity() {
        return minHumidity;
    }
    @Exclude
    public void setMinHumidity(int minHumidity) {
        this.minHumidity = minHumidity;
    }

    public int getMaxHumidity() {
        return maxHumidity;
    }
    @Exclude
    public void setMaxHumidity(int maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    public int getMinSunLight() {
        return minSunLight;
    }
    @Exclude
    public void setMinSunLight(int minSunLight) {
        this.minSunLight = minSunLight;
    }

    public int getMaxSunlight() {
        return maxSunlight;
    }
    @Exclude
    public void setMaxSunlight(int maxSunlight) {
        this.maxSunlight = maxSunlight;
    }

    public Drawable getImage() {
        return image;
    }
    @Exclude
    public void setImage(Drawable image) {
        this.image = image;
    }
}
