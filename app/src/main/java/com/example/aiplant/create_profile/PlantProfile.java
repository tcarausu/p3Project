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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.aiplant.R;
import com.example.aiplant.cameraandgallery.ImagePicker;

import java.io.IOException;
import java.util.Calendar;

public class PlantProfile extends AppCompatActivity {

    private String TAG ="PlantProfile Activity";
    static final int REQUEST_CODE = 123;
    private ImageView profilePicture;
    private EditText namePlant;
    private EditText bd_day;
    private EditText bd_month;
    private EditText bd_year;
    private LinearLayout birthdayLayout;
    private EditText minHumidity;
    private EditText maxHumidity;
    private EditText minSunlight;
    private EditText maxSunlight;
    private EditText minTemperature;
    private EditText maxTemperature;
    private Bitmap picture;
    private Button createProfile;
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final int REQUEST_GALLERY=2;
    private final int REQUEST_CAMERA=1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plant_profile);

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
        if ( resultCode == RESULT_OK ) {
            if ( requestCode == REQUEST_CODE ) {
                Bitmap bitmap = null;
                try {
                    bitmap = ImagePicker.getImageFromResult( getApplicationContext(), resultCode, data );
                } catch ( IOException e ) {
                    //do sth
                }
                profilePicture.setImageBitmap( bitmap );
                picture = bitmap;
                profilePicture.setScaleType( ImageView.ScaleType.CENTER_CROP );
            } else {
                super.onActivityResult( requestCode, resultCode, data );
            }

        } else
            Log.d( TAG, "Error on camera/Gallery" );

    }
}
