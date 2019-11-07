package com.example.aiplant.model;

public class RecyclerViewPlantItem {

    private int mPlantProfilePictureRes;
    private String mPlantName;
    private String mPlantDescription;


    public int getmPlantProfilePictureRes() {
        return mPlantProfilePictureRes;
    }

    public String getmPlantName() {
        return mPlantName;
    }

    public String getmPlantDescription() {
        return mPlantDescription;
    }


    public RecyclerViewPlantItem(int plantProfilePictureRes, String plantName, String plantDescription) {
        mPlantProfilePictureRes = plantProfilePictureRes;
        mPlantName = plantName;
        mPlantDescription = plantDescription;
    }
}
