package com.example.aiplant.create_profile;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.aiplant.R;
import com.example.aiplant.cameraandgallery.ImagePicker;

import java.io.IOException;
import java.util.Calendar;

public class PlantProfile extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PlantProfile";
    static final int REQUEST_CODE = 123;
    private ImageView profilePicture;
    private LinearLayout birthdayLayout;
    private EditText minHumidity, maxHumidity, minSunlight ,maxSunlight,
                     minTemperature ,maxTemperature, bd_year, bd_month, bd_day, namePlant;
    private Bitmap picture;
    private ImageButton createProfile;
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final int REQUEST_GALLERY=2;
    private final int REQUEST_CAMERA=1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plant_profile);
       initializeView();

        profilePicture.setOnClickListener(v -> {
            Intent chooseImageIntent = ImagePicker.getPickImageIntent( getApplicationContext() );
            startActivityForResult( chooseImageIntent, REQUEST_CODE );
            checkPermissions();
        });

        createProfile.setOnClickListener(v -> {
            if(namePlant.getText().toString().equals("")) {
                namePlant.setError(getString(R.string.error_name));
                namePlant.requestFocus();
            }
        });


    }

    private void initializeView() {
        profilePicture = findViewById(R.id.profilePicPlant_imgView);
        namePlant = findViewById(R.id.namePlant_editText);
        bd_day = findViewById(R.id.bd_day);
        bd_month = findViewById(R.id.bd_month);
        bd_year = findViewById(R.id.bd_year);
        birthdayLayout = findViewById(R.id.birthday_layout);
        minHumidity = findViewById(R.id.minHumidity_editText);
        maxHumidity = findViewById(R.id.maxHumidity_editText);
        minSunlight = findViewById(R.id.minSunlight_editText);
        maxSunlight = findViewById(R.id.maxSunlight_editText);
        minTemperature = findViewById(R.id.minTemperature_editText);
        maxTemperature = findViewById(R.id.maxTemperature_editText);
        createProfile = findViewById(R.id.createProfile_btn);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_GALLERY | REQUEST_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d( TAG, "request code: " + requestCode );
        Log.d( TAG, "result code: " + resultCode );
        if ( resultCode == RESULT_OK && requestCode == REQUEST_CODE ) {
            Glide.with(this).load(data.getData()).fitCenter().into(profilePicture);

        } else
            Log.d( TAG, "Nothing selected!" );

    }

    @Override
    public void onClick(View view) {

    }
}
