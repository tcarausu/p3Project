package com.example.aiplant.model;

import android.graphics.drawable.Drawable;

public class Plant {
    private String _id, name ;
    private int age;
    private int minTemperature, maxTemperature, minHumidity, maxHumidity, minSunLight, maxSunlight;
    private Drawable image ;

    public Plant(String name, int age, Drawable image) {
        this.name = name;
        this.age = age;
        this.image = image;
    }

    public Plant(String name, int age, int minTemperature, int maxTemperature, int minHumidity, int maxHumidity, int minSunLight, int maxSunlight) {
        this.name = name;
        this.age = age;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
        this.minSunLight = minSunLight;
        this.maxSunlight = maxSunlight;
    }

    public Plant(String name, int age) {
        this.name = name;
        this.age = age;
    }

}
