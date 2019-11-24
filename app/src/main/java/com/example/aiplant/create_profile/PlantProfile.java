package com.example.aiplant.create_profile;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.InputMismatchException;
import java.util.UUID;

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
    private String profileId;
    private String birthday;
    private int minHumid;
    private int maxHumid;
    private int minTemp;
    private int maxTemp;
    private int minSun;
    private int maxSun;

    public PlantProfile (String userID, String name, String profileId, String birthday, int minHumid, int maxHumid, int minTemp,
                         int maxTemp, int minSun, int maxSun){
        this.userID = userID;
        this.name = name;
        this.profileId = profileId;
        this.birthday = birthday;
        this.minHumid = minHumid;
        this.maxHumid = maxHumid;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minSun = minSun;
        this.maxSun = maxSun;

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

    public String getProfileId() {
        return profileId;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getMinHumid() {
        return minHumid;
    }

    public void setMinHumid(int minHumid) {
        this.minHumid = minHumid;
    }

    public int getMaxHumid() {
        return maxHumid;
    }

    public void setMaxHumid(int maxHumid) {
        this.maxHumid = maxHumid;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public int getMinSun() {
        return minSun;
    }

    public void setMinSun(int minSun) {
        this.minSun = minSun;
    }

    public int getMaxSun() {
        return maxSun;
    }

    public void setMaxSun(int maxSun) {
        this.maxSun = maxSun;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(userID);
        dest.writeString(profileId);
        dest.writeString(birthday);
        dest.writeInt(minHumid);
        dest.writeInt(maxHumid);
        dest.writeInt(minTemp);
        dest.writeInt(maxTemp);
        dest.writeInt(minSun);
        dest.writeInt(maxSun);
    }

    public static class Builder{
        private String sUserID;
        private String sProfileId;
        private String sName;
        private String sBirthday;
        private int sMinHumid;
        private int sMaxHumid;
        private int sMinTemp;
        private int sMaxTemp;
        private int sMinSun;
        private int sMaxSun;

        public Builder() {
           // sUserID = uId;
            sProfileId = UUID.randomUUID().toString();

        }

        public Builder withName(String name){
            if(name.length() == 0) {
                throw new InputMismatchException("No name");
            }
            sName = name;
            return this;
        }

        public Builder withAge(String birthday) {
            if (birthday.length() == 0)
                throw new InputMismatchException("No birthday");
            sBirthday = birthday;
            return this;
        }

        public Builder withHumid(int minHumid, int maxHumid){
            if(minHumid<0 || minHumid>maxHumid || maxHumid>101)
                throw new InputMismatchException("Wrong humidity parameters");
            sMinHumid = minHumid;
            sMaxHumid = maxHumid;
            return this;
        }

        public Builder withTemp(int minTemp, int maxTemp){
            if(minTemp<0 || minTemp>maxTemp || maxTemp>30)
                throw new InputMismatchException("Wrong temperature parameters");
            sMinTemp = minTemp;
            sMaxTemp = maxTemp;
            return this;
        }

        public Builder withSun(int minSun, int maxSun){
            if(minSun<25 || minSun>maxSun || maxSun>75)
                throw new InputMismatchException("Wrong sunlight parameters");
            sMinSun = minSun;
            sMaxSun = maxSun;
            return this;
        }

        public PlantProfile build(){
            PlantProfile profile = new PlantProfile();
            profile.userID = sUserID;
            profile.profileId = sProfileId;
            profile.name = sName;
            profile.birthday = sBirthday;
            profile.minHumid = sMinHumid;
            profile.maxHumid = sMaxHumid;
            profile.minTemp = sMinTemp;
            profile.maxTemp = sMaxTemp;
            profile.minSun = sMinSun;
            profile.maxSun = sMaxSun;
            return profile;
        }
    }
}
