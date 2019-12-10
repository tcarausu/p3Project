package com.example.aiplant.model;

public class RecyclerViewPlantItem {

    private int mPlantProfilePictureRes;
    private String plantProfileUrl;
    private String mPlantName;
    private String mPlantDescription;
    private String minSun;
    private String maxSun;
    private String minTemp;

    public String getMinSun() {
        return minSun;
    }

    public String getMaxSun() {
        return maxSun;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getMinHumidity() {
        return minHumidity;
    }

    public String getMaxHumidity() {
        return maxHumidity;
    }

    private String maxTemp;
    private String minHumidity;
    private String maxHumidity;

    public int getmPlantProfilePictureRes() {
        return mPlantProfilePictureRes;
    }

    public String getmPlantName() {
        return mPlantName;
    }

    public String getmPlantDescription() {
        return mPlantDescription;
    }


    public String getPlantProfileUrl() {
        return plantProfileUrl;
    }


    public RecyclerViewPlantItem(int plantProfilePictureRes, String plantName, String plantDescription) {
        mPlantProfilePictureRes = plantProfilePictureRes;
        mPlantName = plantName;
        mPlantDescription = plantDescription;
    }

    public RecyclerViewPlantItem(String plantProfilePic, String plantName, String plantDescription, String minSun,String maxSun,String minTemp,String maxTemp,String minHumidity,String maxHumidity) {
        plantProfileUrl = plantProfilePic;
        mPlantName = plantName;
        mPlantDescription = plantDescription;
        this.minSun = minSun;
        this.maxSun = maxSun;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
    }
}
