package com.example.aiplant.create_profile;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.InputMismatchException;

public class PlantProfile implements Serializable, Parcelable {


    public static final Creator<PlantProfile> CREATOR = new Creator<PlantProfile>() {
        @Override
        public PlantProfile createFromParcel(Parcel in) {
            return new PlantProfile(in);
        }

        @Override
        public PlantProfile[] newArray(int size) {
            return new PlantProfile[size];
        }
    };

    private String name;
    private String userID;

    public PlantProfile (String userID, String name){
        this.userID=userID;
        this.name=name;
    }

    public PlantProfile(){

    }

    protected PlantProfile(Parcel in){
        userID = in.readString();
        name = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(userID);
    }

    public static class Builder{
        private String sUserID;
        private String sName;

        public Builder() {}

        public Builder withName(String name){
            if(name.length() == 0) {
                throw new InputMismatchException("No name");
            }
            sName = name;
            return this;
        }

        public PlantProfile build(){
            PlantProfile profile = new PlantProfile();
            profile.userID = sUserID;
            profile.name = sName;
            return profile;
        }
    }
}
