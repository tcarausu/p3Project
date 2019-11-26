package com.example.aiplant.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.aiplant.R;
import com.example.aiplant.cameraandgallery.ImagePicker;
import com.example.aiplant.model.Plant;
import com.example.aiplant.utility_classes.BottomNavigationViewHelper;
import com.example.aiplant.utility_classes.MongoDbSetup;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.aiplant.R.drawable.mood_cold;
import static com.example.aiplant.R.drawable.mood_hot;
import static com.example.aiplant.R.drawable.mood_medium;
import static com.facebook.FacebookSdk.getApplicationContext;


public class HomeFragment extends androidx.fragment.app.Fragment implements View.OnClickListener {


    private static final String TAG = "HomeFragment";
    private static final int ACTIVITY_NUM = 0;
    private static final int REQUEST_CAMERA = 22;
    private static final int REQUEST_GALLERY = 33;
    private static final int REQUEST_CODE = 11;

    private static final String LAST_TEXT_NAME = "", LAST_TEXT_DATE = "";

    private Plant plant = new Plant();
    private Bundle savedInstanceState;

    //data
    private MongoDbSetup mongoDbSetup;


    // widgets
    private Button adjustNameAndDateButton, saveNameAndDate, adjustConditions, saveChangesButton;
    private TextView change_picture, temperature_text, humidity_text, sunlight_text, hum_current, temp_current, light_current;
    private SeekBar humidity, temperature, light;
    private ImageView mood_pic;
    private RelativeLayout home_Layout;
    private CircleImageView profileImage;
    private EditText flowerNameEditText, flowerTimeEditText, hum_min, hum_max, temp_min, temp_max, light_min, light_max;
    private Context mContext;
    private Bitmap picture;

    private String plantName, plantDate;

    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getActivity();

        mongoDbSetup = ((HomeActivity) getActivity()).getMongoDbForLaterUse();
        initLayout(v);
        buttonListeners();
        checkPermissions();

        SeekBar humidity = v.findViewById(R.id.humidity_slider);
        SeekBar temperature = v.findViewById(R.id.temperature_slider);
        SeekBar light = v.findViewById(R.id.sunlight_slider);

        disableEditText();

        humidity.setProgress(70);
        humidity.getProgress();
        temperature.setProgress(43);
        temperature.getProgress();
        light.setProgress(20);
        light.getProgress();

        mongoDbSetup.findPlantsList();
        mongoDbSetup.findPlantProfileList(flowerNameEditText, flowerTimeEditText, humidity, temperature, light,profileImage);
        return v;
    }

    private void initLayout(View v) {
        home_Layout = v.findViewById(R.id.home_activity);
        profileImage = v.findViewById(R.id.profileImage);

        //Top plant profile
        change_picture = v.findViewById(R.id.change_picture);
        flowerNameEditText = v.findViewById(R.id.flower_name);
        flowerTimeEditText = v.findViewById(R.id.flower_time);

        //Conditions
        temperature_text = v.findViewById(R.id.temperature_text);
        humidity_text = v.findViewById(R.id.humidity_text);
        sunlight_text = v.findViewById(R.id.sunlight_text);

        mood_pic = v.findViewById(R.id.mood_pic);

        //Buttons
        adjustNameAndDateButton = v.findViewById(R.id.adjust_name_and_date);
        saveNameAndDate = v.findViewById(R.id.save_name_and_date);
        adjustConditions = v.findViewById(R.id.adjust_conditions);
        saveChangesButton = v.findViewById(R.id.save_changes);

        //Sliders
        humidity = v.findViewById(R.id.humidity_slider);
        temperature = v.findViewById(R.id.temperature_slider);
        light = v.findViewById(R.id.sunlight_slider);

        //Min and max value
        hum_min = v.findViewById(R.id.humidity_min_value);
        hum_current = v.findViewById(R.id.humidity_current_value);
        hum_max = v.findViewById(R.id.humidity_max_value);
        temp_min = v.findViewById(R.id.temperature_min_value);
        temp_current = v.findViewById(R.id.temperature_current_value);
        temp_max = v.findViewById(R.id.temperature_max_value);
        light_min = v.findViewById(R.id.light_min_value);
        light_current = v.findViewById(R.id.light_current_value);
        light_max = v.findViewById(R.id.light_max_value);

    }

    private void disableEditText() {

        flowerNameEditText.setEnabled(false);
        flowerTimeEditText.setEnabled(false);

        hum_min.setText(plant.getMinHumidity() + "%");
        hum_min.setEnabled(false);
        hum_max.setText(plant.getMaxHumidity() + "%");
        hum_max.setEnabled(false);

        temp_min.setText(plant.getMinTemperature() + "°C");
        temp_min.setEnabled(false);
        temp_max.setText(plant.getMaxTemperature() + "°C");
        temp_max.setEnabled(false);

        light_min.setText(plant.getMinSunLight() + "lux");
        light_min.setEnabled(false);
        light_max.setText(plant.getMaxSunlight() + "lux");
        light_max.setEnabled(false);

        humidity.setEnabled(false);
        temperature.setEnabled(false);
        light.setEnabled(false);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(permissions, REQUEST_GALLERY | REQUEST_CAMERA);
        }
    }

    private void alertDialog() {

        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getApplicationContext());
        startActivityForResult(chooseImageIntent, REQUEST_CODE);
        checkPermissions();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.d(TAG, "request code: " + requestCode);
        Log.d(TAG, "result code: " + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                Bitmap bitmap = null;
                try {
                    bitmap = ImagePicker.getImageFromResult(getApplicationContext(), resultCode, data);
                } catch (IOException e) {
                    //do sth
                }
                profileImage.setImageBitmap(bitmap);
                picture = bitmap;
                profileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }

        } else
            Log.d(TAG, "Error on camera/Gallery");
    }


    private void adjustNameAndDate() {
        flowerNameEditText.setEnabled(true);
        flowerTimeEditText.setEnabled(true);
        saveNameAndDate.setVisibility(View.VISIBLE);
    }

    private void saveNameAndTime() {

        final SharedPreferences prefName = PreferenceManager.getDefaultSharedPreferences(mContext);
        flowerNameEditText.setText(prefName.getString(LAST_TEXT_NAME, ""));
        flowerNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                flowerNameEditText.setEnabled(false);
                //  flowerNameEditText.setText(LAST_TEXT_NAME);
                prefName.edit().putString(LAST_TEXT_NAME, s.toString()).apply();
            }
        });

        final SharedPreferences prefDate = PreferenceManager.getDefaultSharedPreferences(mContext);
        flowerTimeEditText.setText(prefDate.getString(LAST_TEXT_DATE, ""));
        flowerTimeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                flowerTimeEditText.setEnabled(false);
                //  flowerNameEditText.setText(LAST_TEXT_NAME);
                prefDate.edit().putString(LAST_TEXT_DATE, s.toString()).apply();
            }
        });
    }

    private void buttonListeners() {


        adjustNameAndDateButton.setOnClickListener(this);
        saveNameAndDate.setOnClickListener(this);

        humidity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hum_current.setText(progress + "%");
                if (progress <= plant.getMinHumidity()) {
                    Toast toast = Toast.makeText(mContext, "Water your plant!", Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_medium);
                } else if (progress >= plant.getMaxHumidity()) {
                    Toast toast = Toast.makeText(mContext, "There is too much water in your plant", Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_medium);
                } else {
                    mood_pic.setImageResource(R.drawable.mood_happy);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        temperature.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                temp_current.setText(progress + "°C");

                if (progress <= plant.getMinTemperature()) {
                    Toast toast = Toast.makeText(mContext, "Your plant is too cold. Increase room temperature!", Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_cold);
                } else if (progress >= plant.getMaxTemperature()) {
                    Toast toast = Toast.makeText(mContext, "Your plant is too hot. Decrease room temperature!", Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_hot);
                } else {
                    mood_pic.setImageResource(R.drawable.mood_happy);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        light.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                light_current.setText(progress + "lux");

                if (progress <= plant.getMinSunLight()) {
                    Toast toast = Toast.makeText(mContext, "Your plant needs more light!", Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_medium);
                } else if (progress >= plant.getMaxSunlight()) {
                    Toast toast = Toast.makeText(mContext, "Your plant needs less light!", Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_medium);
                } else {
                    mood_pic.setImageResource(R.drawable.mood_happy);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        adjustConditions.setOnClickListener(v -> {
            hum_min.setEnabled(true);
            hum_max.setEnabled(true);
            temp_min.setEnabled(true);
            temp_max.setEnabled(true);
            light_min.setEnabled(true);
            light_max.setEnabled(true);
            saveChangesButton.setVisibility(View.VISIBLE);
        });

        saveChangesButton.setOnClickListener(v -> saveChanges());

        change_picture.setOnClickListener(v -> alertDialog());
    }


    public void saveChanges() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("Are you sure you want to change conditions?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                (dialog, id) ->
                        dialog.cancel());
        saveChangesButton.setVisibility(View.GONE);

        builder1.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());
        saveChangesButton.setVisibility(View.GONE);

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.adjust_name_and_date:
                adjustNameAndDate();
                break;

            case R.id.save_name_and_date:
                saveNameAndTime();
                break;

        }
    }
}

