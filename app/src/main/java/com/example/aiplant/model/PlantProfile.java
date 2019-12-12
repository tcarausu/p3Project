package com.example.aiplant.model;

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
    private String url;
    private byte[] picture_bytes;
    private int minHumid;
    private int maxHumid;
    private int minTemp;
    private int maxTemp;
    private int minSun;
    private int maxSun;
    private int measured_humidity;
    private int measured_temperature;
    private int measured_sunlight;

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public byte[] getPicture_bytes() {
        return picture_bytes;
    }

    public void setPicture_bytes(byte[] picture_bytes) {
        this.picture_bytes = picture_bytes;
    }

    public int getMeasured_humidity() {
        return measured_humidity;
    }

    public void setMeasured_humidity(int measured_humidity) {
        this.measured_humidity = measured_humidity;
    }

    public int getMeasured_temperature() {
        return measured_temperature;
    }

    public void setMeasured_temperature(int measured_temperature) {
        this.measured_temperature = measured_temperature;
    }

    public int getMeasured_sunlight() {
        return measured_sunlight;
    }

    public void setMeasured_sunlight(int measured_sunlight) {
        this.measured_sunlight = measured_sunlight;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PlantProfile(String userID, String profileId, String name, String birthday,
                        int minHumid, int maxHumid, int minTemp, int maxTemp, int minSun, int maxSun, byte[] picture_bytes
            , int measured_humidity, int measured_temperature, int measured_sunlight) {
        this.name = name;
        this.userID = userID;
        this.profileId = profileId;
        this.birthday = birthday;
        this.picture_bytes = picture_bytes;
        this.minHumid = minHumid;
        this.maxHumid = maxHumid;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minSun = minSun;
        this.maxSun = maxSun;
        this.measured_humidity = measured_humidity;
        this.measured_temperature = measured_temperature;
        this.measured_sunlight = measured_sunlight;
    }

    public PlantProfile(String name, String user_id, String profile_id, String birthday,
                        String picture, byte[] pic_bytes, int min_hum, int max_hum, int min_temp,
                        int max_temp, int min_sun, int max_sun, int measured_humidity, int measured_temperature, int measured_sunlight) {
        this.name = name;
        this.userID = user_id;
        this.profileId = profile_id;
        this.birthday = birthday;
        this.url = picture;
        this.picture_bytes = pic_bytes;
        this.minHumid = min_hum;
        this.maxHumid = max_hum;
        this.minTemp = min_temp;
        this.maxTemp = max_temp;
        this.minSun = min_sun;
        this.maxSun = max_sun;
        this.measured_humidity = measured_humidity;
        this.measured_temperature = measured_temperature;
        this.measured_sunlight = measured_sunlight;
    }

    public PlantProfile(String userID, String profileId, String name, String birthday,
                        int minHumid, int maxHumid, int minTemp, int maxTemp, int minSun, int maxSun, String url, byte[] picture_bytes
            , int measured_humidity, int measured_temperature, int measured_sunlight) {
        this.name = name;
        this.userID = userID;
        this.profileId = profileId;
        this.birthday = birthday;
        this.picture_bytes = picture_bytes;
        this.url = url;
        this.minHumid = minHumid;
        this.maxHumid = maxHumid;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minSun = minSun;
        this.maxSun = maxSun;
        this.measured_humidity = measured_humidity;
        this.measured_temperature = measured_temperature;
        this.measured_sunlight = measured_sunlight;
    }


    public PlantProfile(String userID, String name, String profileId, String birthday, int minHumid, int maxHumid, int minTemp,
                        int maxTemp, int minSun, int maxSun) {
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

    public PlantProfile() {

    }

    protected PlantProfile(Parcel in) {
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

    public static class Builder {
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
        private String url;

        public Builder() {
            // sUserID = uId;
            sProfileId = UUID.randomUUID().toString();

        }

        public Builder withName(String name) {
            if (name.length() == 0) {
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

        public Builder withUrl(String urlToUse) {
            if (urlToUse == null)
                urlToUse = "https://drive.google.com/file/d/1QYW_j4Twu2Vj0dHWDfr9A_LcTZybwUKI/view?usp=sharing";

            url = urlToUse;
            return this;
        }

        public Builder withHumid(int minHumid, int maxHumid) {
            if (minHumid < 0 || minHumid > maxHumid || maxHumid >= 100)
                throw new InputMismatchException("Wrong humidity parameters");
            sMinHumid = minHumid;
            sMaxHumid = maxHumid;
            return this;
        }

        public Builder withTemp(int minTemp, int maxTemp) {
            if (minTemp < 0 || minTemp > maxTemp || maxTemp >= 40)
                throw new InputMismatchException("Wrong temperature parameters");
            sMinTemp = minTemp;
            sMaxTemp = maxTemp;
            return this;
        }

        public Builder withSun(int minSun, int maxSun) {
            if (minSun <= 0 || minSun > maxSun || maxSun >= 100)
                throw new InputMismatchException("Wrong sunlight parameters");
            sMinSun = minSun;
            sMaxSun = maxSun;
            return this;
        }

        public PlantProfile build() {
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
