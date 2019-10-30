package com.example.aiplant.model;

public class Plant {
    private String name ;
    private int age;
    private int minTemperature, maxTemperature, minHumidity, maxHumidity, minSunLight, maxSunlight;

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
