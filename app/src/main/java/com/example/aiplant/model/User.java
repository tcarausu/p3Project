package com.example.aiplant.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.mongodb.Block;

import org.bson.BsonBinary;

import java.io.Serializable;

@Entity
public class User   implements Serializable, Block {

    @PrimaryKey
    private String id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "picture")
    private String picture;
    @ColumnInfo(name = "number_of_plants")
    private int number_of_plants;
    @ColumnInfo(name = "birthday")
    private String birthday;
    @ColumnInfo(name = "edited_pic")
    private BsonBinary edited_pic ;

    private byte[] edited_pics ;
    public byte[] getEdited_pics() {
        return edited_pics;
    }


    public User(String id, String name, String email, String picture, int number_of_plants, String birthday) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.number_of_plants = number_of_plants;
        this.birthday = birthday;
    }

    public User(String id, String name, String email, String picture, int number_of_plants, String birthday, BsonBinary edited_pic) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.number_of_plants = number_of_plants;
        this.birthday = birthday;
        this.edited_pic = edited_pic;
    }

    public User(String id, String name, String email, String picture, int number_of_plants, String birthday, byte[] edited_pic) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.number_of_plants = number_of_plants;
        this.birthday = birthday;
        this.edited_pics = edited_pic;
    }

    public BsonBinary getEdited_pic() {
        return edited_pic;
    }

    public User() {}

    public String getId() {
        return id;
    }
    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Exclude
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    @Exclude
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }
    @Exclude
    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getNumber_of_plants() {
        return number_of_plants;
    }
    @Exclude
    public void setNumber_of_plants(String num_plants) {
        this.number_of_plants = number_of_plants;
    }

    public String getBirthday() {
        return birthday;
    }
    @Exclude
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Override
    public void apply(Object o) {

    }
}
