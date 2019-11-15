package com.example.aiplant.home;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.aiplant.R;
import com.example.aiplant.utility_classes.BottomNavigationViewHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.aiplant.R.drawable;
import static com.example.aiplant.R.drawable.mood_cold;
import static com.example.aiplant.R.drawable.mood_hot;
import static com.example.aiplant.R.drawable.mood_medium;
import static com.example.aiplant.R.id;
import static com.example.aiplant.R.layout;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;
    private static final int REQUEST_CAMERA = 22;
    private static final int REQUEST_GALLERY = 33;
    public static final String LAST_TEXT_NAME = "", LAST_TEXT_TIME_PERIOD = "";


    // widgets
    private BottomNavigationView bottomNavigationViewEx;
    private Button editPlantNameButton, savePlantNameButton, editTimePeriodButton, saveTimePeriodButton, adjustConditions, saveChangesButton;
    private TextView change_picture, flowerNameTextView, flowerTimeTextView, temperature_text, humidity_text, sunlight_text, hum_current, temp_current, light_current;
    private SeekBar humidity, temperature, light;
    private ImageView mood_pic;
    private RelativeLayout home_Layout;
    private CircleImageView profileImage;
    private EditText flowerNameEditText, flowerTimeEditText, hum_min, hum_max, temp_min, temp_max, light_min, light_max;
    private int minimumTemperature = 5, maximumTemperature = 28, minimumHumidity = 25, maximumHumidity = 75, minimumLight = 25, maximumLight = 75;
    private Context mContext;

    private String plantName, plantDate;

    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_home);
        initLayout();
        buttonListeners();

        SeekBar humidity = findViewById(id.humidity_slider);
        SeekBar temperature = findViewById(id.temperature_slider);
        SeekBar light = findViewById(id.sunlight_slider);

        //       changePlantPicture();
        setupBottomNavigationView();
        humidity.setProgress(50);
        humidity.getProgress();
        temperature.setProgress(23);
        temperature.getProgress();
        light.setProgress(40);
        light.getProgress();

        mongoDatabase();

    }

    private void mongoDatabase() {

    }


    public void initLayout() {
        home_Layout = findViewById(id.home_activity);
        profileImage = findViewById(id.profileImage);
        change_picture = findViewById(id.change_picture);
        flowerNameEditText = findViewById(id.flower_pic_name);
        flowerNameEditText.setEnabled(false);
        flowerTimeEditText = findViewById(id.flower_pic_time);
        flowerTimeEditText.setEnabled(false);
        flowerTimeTextView = findViewById(id.flower_pic_time_text_view);
        //Conditions
        temperature_text = findViewById(id.temperature_text);
        humidity_text = findViewById(id.humidity_text);
        sunlight_text = findViewById(id.sunlight_text);

        adjustConditions = findViewById(id.adjust_conditions);
        saveChangesButton = findViewById(id.save_changes);
        mood_pic = findViewById(id.mood_pic);

        //Buttons
        editPlantNameButton = findViewById(id.editFlowerNameButton);
        savePlantNameButton = findViewById(id.saveFlowerNameButton);
        editTimePeriodButton = findViewById(id.editTimePeriodButton);
        saveTimePeriodButton = findViewById(id.saveTimePeriodButton);

    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_GALLERY | REQUEST_CAMERA);
        }
    }


    private void changePlantPicture() {
        String text = "change plant picture";

        View dialogLayout = getLayoutInflater().inflate(R.layout.customized_alert_dialog, null);

        ImageButton cameraButton = dialogLayout.findViewById(R.id.cameraButtonDialog);
        ImageButton galleryButton = dialogLayout.findViewById(R.id.galleryButtonDialog);
        ImageButton cancelButton = dialogLayout.findViewById(R.id.cancelButtonDialog);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogLayout);

        final AlertDialog alertDialog = dialogBuilder.create();
        WindowManager.LayoutParams wlp = alertDialog.getWindow().getAttributes();

//        wlp.windowAnimations = R.style.AlertDialogAnimation;
        wlp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        alertDialog.getWindow().setAttributes(wlp);
        alertDialog.setCanceledOnTouchOutside(true);
        // Setting transparent the background (layout) of alert dialog
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();


        cameraButton.setOnClickListener(v -> {
            takePicture();
            alertDialog.dismiss();
        });

        galleryButton.setOnClickListener(v -> {
            selectPicture();
            alertDialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    private void takePicture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else
            Toast.makeText(mContext, "Battery is low...", Toast.LENGTH_SHORT).show();
    }

    private void selectPicture() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Todo: continue here with results from the camera
        if (resultCode == RESULT_OK && (requestCode == REQUEST_CAMERA || requestCode == REQUEST_GALLERY)) {
            //Todo: we can add other implementation here, like loading the image to database
            Uri uri = Uri.parse(data.getData().toString());
            Glide.with(this).load(uri).fitCenter().into(profileImage);
            profileImage.refreshDrawableState();
        } else Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    private void hideEditTextName() {
        flowerNameEditText.setEnabled(true);
        editPlantNameButton.setVisibility(View.INVISIBLE);
        savePlantNameButton.setVisibility(View.VISIBLE);
    }

    private void savePlantName() {

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        flowerNameEditText.setText(pref.getString(LAST_TEXT_NAME, ""));
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    pref.edit().putString(LAST_TEXT_NAME, s.toString()).apply();
                }
                editPlantNameButton.setVisibility(View.VISIBLE);
                savePlantNameButton.setVisibility(View.INVISIBLE);

            }
        });

    }

    private void hideEditTextTime() {
        flowerTimeEditText.setEnabled(true);
        editTimePeriodButton.setVisibility(View.INVISIBLE);
        saveTimePeriodButton.setVisibility(View.VISIBLE);
    }

    private void saveTimePeriod() {
        plantDate = flowerTimeEditText.getText().toString();

        if (!TextUtils.isEmpty(plantDate)) {
            flowerTimeEditText.setVisibility(View.INVISIBLE);
            saveTimePeriodButton.setVisibility(View.INVISIBLE);
            flowerTimeTextView.setVisibility(View.VISIBLE);
            editTimePeriodButton.setVisibility(View.VISIBLE);
            flowerTimeTextView.setText(plantDate);

        } else {
            flowerTimeEditText.setError("No date!");
        }
    }

    public void buttonListeners() {

        editTimePeriodButton.setOnClickListener(this);

        humidity = findViewById(id.humidity_slider);
        temperature = findViewById(id.temperature_slider);
        light = findViewById(id.sunlight_slider);
        hum_min = findViewById(id.humidity_min_value);
        hum_current = findViewById(id.humidity_current_value);
        hum_max = findViewById(id.humidity_max_value);
        temp_min = findViewById(id.temperature_min_value);
        temp_current = findViewById(id.temperature_current_value);
        temp_max = findViewById(id.temperature_max_value);
        light_min = findViewById(id.light_min_value);
        light_current = findViewById(id.light_current_value);
        light_max = findViewById(id.light_max_value);

        hum_min.setText(minimumHumidity + "%");
        hum_min.setEnabled(false);
        hum_max.setText(maximumHumidity + "%");
        hum_max.setEnabled(false);

        temp_min.setText(minimumTemperature + "°C");
        temp_min.setEnabled(false);
        temp_max.setText(maximumTemperature + "°C");
        temp_max.setEnabled(false);

        light_min.setText(minimumLight + "lux");
        light_min.setEnabled(false);
        light_max.setText(maximumLight + "lux");
        light_max.setEnabled(false);


        //   humidity.setEnabled(false);
        //     temperature.setEnabled(false);
        //     light.setEnabled(false);


        humidity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hum_current.setText(progress + "%");
                if (progress <= minimumHumidity) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Water your plant!", Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_medium);
                } else if (progress >= maximumHumidity) {
                    Toast toast = Toast.makeText(getApplicationContext(), "There is too much water in your plant", Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_medium);
                } else {
                    mood_pic.setImageResource(drawable.mood_happy);

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

            //            int minimumValue = 20;
//            int progressChanged = minimumValue;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                temp_current.setText(progress + "°C");

                if (progress <= minimumTemperature) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Your plant is too cold. Increase room temperature!", Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_cold);
                } else if (progress >= maximumTemperature) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Your plant is too hot. Decrease room temperature!", Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_hot);
                } else {
                    mood_pic.setImageResource(drawable.mood_happy);
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

                if (progress <= minimumLight) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Your plant needs more light!", Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_medium);
                } else if (progress >= maximumLight) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Your plant needs less light!", Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_medium);
                } else {
                    mood_pic.setImageResource(drawable.mood_happy);
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

        change_picture.setOnClickListener(v -> changePlantPicture());
    }


    public void saveChanges() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
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

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case id.editFlowerNameButton:
                hideEditTextName();
                break;

            case id.saveFlowerNameButton:
                savePlantName();
                break;

            case id.editTimePeriodButton:
                hideEditTextTime();
                break;

            case id.saveTimePeriodButton:
                saveTimePeriod();
                break;
        }
    }

    /**
     * Bottom Navigation View setup
     */
    public void setupBottomNavigationView() {
        bottomNavigationViewEx = findViewById(id.bottomNavigationBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(getApplicationContext(), bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }

}
