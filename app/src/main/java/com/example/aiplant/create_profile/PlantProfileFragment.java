package com.example.aiplant.create_profile;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.aiplant.R;
import com.example.aiplant.cameraandgallery.ImagePicker;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class PlantProfileFragment extends Fragment {

    private String TAG = "PlantProfileFragment";

    static final int REQUEST_CODE = 123;

    private String userID;
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
    private Button createProfileBtn;
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final int REQUEST_GALLERY=2;
    private final int REQUEST_CAMERA=1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_new_plant_profile,container,false);
        profilePicture = view.findViewById(R.id.profilePicPlant_imgView);
        namePlant = view.findViewById(R.id.namePlant_editText);
        bd_day = view.findViewById(R.id.bd_day);
        bd_month = view.findViewById(R.id.bd_month);
        bd_year = view.findViewById(R.id.bd_year);
        birthdayLayout = view.findViewById(R.id.birthday_layout);
        minHumidity = view.findViewById(R.id.minHumidity_editText);
        maxHumidity = view.findViewById(R.id.maxHumidity_editText);
        minSunlight = view.findViewById(R.id.minSunlight_editText);
        maxSunlight = view.findViewById(R.id.maxSunlight_editText);
        minTemperature = view.findViewById(R.id.minTemperature_editText);
        maxTemperature = view.findViewById(R.id.maxTemperature_editText);
        createProfileBtn = view.findViewById(R.id.createProfile_btn);


        profilePicture.setOnClickListener(v -> {

            Intent chooseImageIntent = ImagePicker.getPickImageIntent( getContext() );
            startActivityForResult( chooseImageIntent, REQUEST_CODE );
            checkPermissions();
        });

        createProfileBtn.setOnClickListener(v -> {
//            if(namePlant.getText().toString().equals("")) {
//                namePlant.setError(getString(R.string.error_name));
//                namePlant.requestFocus();
//            }

            createProfile();

        });

        return view;


    }


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_GALLERY | REQUEST_CAMERA);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d( TAG, "request code: " + requestCode );
        Log.d( TAG, "result code: " + resultCode );
        if ( resultCode == RESULT_OK ) {
            if ( requestCode == REQUEST_CODE ) {
                Bitmap bitmap = null;
                try {
                    bitmap = ImagePicker.getImageFromResult( getContext(), resultCode, data );
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

    private void createProfile() {


        PlantProfile profile = new PlantProfile.Builder()
                .withName(namePlant.getText().toString()).build();

        //("name", namePlant.getText().toString());
        Log.d("STITCHH", namePlant.getText().toString());
    }

}
