package com.example.aiplant.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.bumptech.glide.Glide;
import com.example.aiplant.R;
import com.example.aiplant.cameraandgallery.ImagePicker;
import com.example.aiplant.create_profile.PlantProfileActivity;
import com.example.aiplant.model.PlantProfile;
import com.example.aiplant.utility_classes.MongoDbSetup;
import com.google.android.gms.tasks.Continuation;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;

import org.bson.BsonBinary;
import org.bson.Document;
import org.bson.types.Binary;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.aiplant.R.drawable.mood_cold;
import static com.example.aiplant.R.drawable.mood_hot;
import static com.example.aiplant.R.drawable.mood_medium;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;


public class HomeFragment extends androidx.fragment.app.Fragment implements View.OnClickListener {

    private static final String TAG = "HomeFragment";
    private static final int ACTIVITY_NUM = 0;
    private static final int REQUEST_CAMERA = 22;
    private static final int REQUEST_GALLERY = 33;
    private static final int REQUEST_CODE = 11;

    private static final String LAST_TEXT_NAME = "", LAST_TEXT_DATE = "";

    private Bundle savedInstanceState;

    //data
    private MongoDbSetup mongoDbSetup;
    private static StitchUser mStitchUser;
    private PlantProfile profileForUser;

    // widgets
    private Button adjustNameAndDateButton, saveNameAndDateButton, adjustConditionsButton, saveChangesButton;
    private TextView change_picture, temperature_text, humidity_text, sunlight_text, hum_current, temp_current, light_current,
            humidity_min_value_text, humidity_current_value_text, humidity_max_value_text,
            temperature_min_value_text, temperature_current_value_text, temperature_max_value_text,
            light_min_value_text, light_current_value_text, light_max_value_text;
    private SeekBar humiditySeekBar, temperatureSeekBar, lightSeekBar;
    private ImageView mood_pic;
    private RelativeLayout home_Layout;
    private CircleImageView profileImage;
    private EditText flowerNameEditText, flowerTimeEditText, hum_min, hum_max, temp_min, temp_max, light_min, light_max;
    private Context mContext;
    private Bitmap picture;
    private boolean has_changed_profile_image;
    private SharedPreferences prefer;

    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Bitmap bitmap;
    private BsonBinary bsonBinary;

    private String user_id;

    private Document plantProfileDoc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getActivity();

        mongoDbSetup = ((HomeActivity) getActivity()).getMongoDbForLaterUse();
        mStitchUser = mongoDbSetup.getStitchUser();
        user_id = mStitchUser.getId();
        prefer = getActivity().getSharedPreferences("prefer", MODE_PRIVATE);
        has_changed_profile_image = prefer.getBoolean("prefer", false);

        initLayout(v);
        buttonListeners();
        checkPermissions();
        disableEditText();

        fetchedDoc();

        return v;
    }

    private void fetchedDoc() {
        try {
            String value = mStitchUser.getId(),
                    key = "user_id",
                    collectionName = "plant_profiles";
            RemoteMongoCollection<Document> collection = mongoDbSetup.getCollectionByName(collectionName);
            collection.findOne(eq(key, value)).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Document doc1 = task.getResult();
                    setPlantProfileDoc(doc1);
                    if (doc1 != null) {
                        setupPlantProfile(getPlantProfileDoc());
                    } else {
                        mongoDbSetup.goToWhereverWithFlags(mContext, mContext, PlantProfileActivity.class);
                    }
                } else {
                    mongoDbSetup.goToWhereverWithFlags(mContext, mContext, PlantProfileActivity.class);
                }

            }).addOnFailureListener(e -> Log.d(TAG, "onFailure: Error: " + e.getCause()));

        } catch (Exception e) {
            Log.d(TAG, "fetchedDoc: error: " + e.getCause());
        }
    }

    private void setupPlantProfile(Document profile) {
        String profile_id = profile.getString("profile_id");
        String user_id = profile.getString("user_id");
        String name = profile.getString("name");
        String birthday = profile.getString("birthday");
        int measured_humidity = profile.getInteger("measured_humidity");
        int measured_temperature = profile.getInteger("measured_temperature");
        int measured_sunlight = profile.getInteger("measured_sunlight");

        String picture = profile.getString("picture");

        Binary edited_pic = profile.get("edited_pic", Binary.class);
        byte[] pic_bytes = edited_pic.getData();
        Bitmap bitmap = BitmapFactory.decodeByteArray(pic_bytes, 0, pic_bytes.length);

        ArrayList humidityArray = profile.get("humidity", ArrayList.class);
        ArrayList temperatureArray = profile.get("temperature", ArrayList.class);
        ArrayList sunlightArray = profile.get("sunlight", ArrayList.class);

        int min_hum = (int) humidityArray.get(0);
        int max_hum = (int) humidityArray.get(1);
        int min_temp = (int) temperatureArray.get(0);
        int max_temp = (int) temperatureArray.get(1);
        int min_sun = (int) sunlightArray.get(0);
        int max_sun = (int) sunlightArray.get(1);

        profileForUser = new PlantProfile(name, user_id, profile_id, birthday, picture,
                pic_bytes, min_hum, max_hum, min_temp, max_temp, min_sun, max_sun, measured_humidity, measured_temperature, measured_sunlight);

        flowerNameEditText.setText(profileForUser.getName());
        flowerTimeEditText.setText(profileForUser.getBirthday());

        humiditySeekBar.setProgress(profileForUser.getMeasured_humidity());
        temperatureSeekBar.setProgress(profileForUser.getMeasured_temperature());
        lightSeekBar.setProgress(profileForUser.getMeasured_sunlight());

        hum_current.setText(String.valueOf(profileForUser.getMeasured_humidity()));
        temp_current.setText(String.valueOf(profileForUser.getMeasured_temperature()));
        light_current.setText(String.valueOf(profileForUser.getMeasured_sunlight()));

        hum_min.setText(String.valueOf(profileForUser.getMinHumid()));
        hum_max.setText(String.valueOf(profileForUser.getMaxHumid()));

        temp_min.setText(String.valueOf(profileForUser.getMinTemp()));
        temp_max.setText(String.valueOf(profileForUser.getMaxTemp()));

        light_min.setText(String.valueOf(profileForUser.getMinSun()));
        light_max.setText(String.valueOf(profileForUser.getMaxSun()));

        if (has_changed_profile_image) {
            Glide.with(getActivity()).load(bitmap).centerCrop().into(profileImage);
        } else {
            if (picture == null) {
                profileImage.setImageResource(R.drawable.add_pic);
            } else {
                Glide.with(getActivity()).load(picture).centerCrop().into(profileImage);
            }
            if (bitmap != null) {
                Glide.with(getActivity()).load(bitmap).centerCrop().into(profileImage);
            }
        }

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
        saveNameAndDateButton = v.findViewById(R.id.save_name_and_date);
        adjustConditionsButton = v.findViewById(R.id.adjust_conditions);
        saveChangesButton = v.findViewById(R.id.save_changes);

        //Sliders
        humiditySeekBar = v.findViewById(R.id.humidity_slider);
        temperatureSeekBar = v.findViewById(R.id.temperature_slider);
        lightSeekBar = v.findViewById(R.id.sunlight_slider);

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

        humidity_min_value_text = v.findViewById(R.id.humidity_min_value_text);
        humidity_current_value_text = v.findViewById(R.id.humidity_current_value_text);
        humidity_max_value_text = v.findViewById(R.id.humidity_max_value_text);

        temperature_min_value_text = v.findViewById(R.id.temperature_min_value_text);
        temperature_current_value_text = v.findViewById(R.id.temperature_current_value_text);
        temperature_max_value_text = v.findViewById(R.id.temperature_max_value_text);

        light_min_value_text = v.findViewById(R.id.light_min_value_text);
        light_current_value_text = v.findViewById(R.id.light_current_value_text);
        light_max_value_text = v.findViewById(R.id.light_max_value_text);

        humiditySeekBar = v.findViewById(R.id.humidity_slider);
        temperatureSeekBar = v.findViewById(R.id.temperature_slider);
        lightSeekBar = v.findViewById(R.id.sunlight_slider);

        mood_pic.setImageResource(R.drawable.mood_happy);
    }

    private void buttonListeners() {

        adjustNameAndDateButton.setOnClickListener(this);
        saveNameAndDateButton.setOnClickListener(this);

        humiditySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hum_current.setText(String.valueOf(profileForUser.getMeasured_humidity()));
                if (progress <= profileForUser.getMinHumid()) {
                    Toast toast = Toast.makeText(mContext, getString(R.string.plant_has_little_water), Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_medium);
                } else if (progress >= profileForUser.getMaxHumid()) {
                    Toast toast = Toast.makeText(mContext, getString(R.string.plant_has_too_much_water), Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_medium);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        temperatureSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                temp_current.setText(String.valueOf(profileForUser.getMeasured_temperature()));

                if (progress <= profileForUser.getMinTemp()) {
                    Toast toast = Toast.makeText(mContext, getString(R.string.plant_is_cold), Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_cold);
                } else if (progress >= profileForUser.getMaxTemp()) {
                    Toast toast = Toast.makeText(mContext, getString(R.string.plant_is_too_hot), Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_hot);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        lightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                light_current.setText(String.valueOf(profileForUser.getMeasured_sunlight()));

                if (progress <= profileForUser.getMinSun()) {
                    Toast toast = Toast.makeText(mContext, getString(R.string.plant_needs_less_light), Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_medium);
                } else if (progress >= profileForUser.getMaxSun()) {
                    Toast toast = Toast.makeText(mContext, getString(R.string.plant_needs_more_light), Toast.LENGTH_SHORT);
                    toast.show();
                    mood_pic.setImageResource(mood_medium);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        adjustConditionsButton.setOnClickListener(this);

        saveChangesButton.setOnClickListener(this);

        profileImage.setOnClickListener(this);
        change_picture.setOnClickListener(this);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(permissions, REQUEST_GALLERY | REQUEST_CAMERA);
        }
    }

    private void disableEditText() {
        //name and date
        flowerNameEditText.setEnabled(false);
        flowerTimeEditText.setEnabled(false);
        //min and max values
        hum_min.setEnabled(false);
        hum_max.setEnabled(false);
        temp_min.setEnabled(false);
        temp_max.setEnabled(false);
        light_min.setEnabled(false);
        light_max.setEnabled(false);
        //current values
        humiditySeekBar.setEnabled(false);
        temperatureSeekBar.setEnabled(false);
        lightSeekBar.setEnabled(false);

    }

    private void alertDialog() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(mContext);
        startActivityForResult(chooseImageIntent, REQUEST_CODE);
        checkPermissions();
    }

    private Bitmap getBitmap() {
        return picture;
    }

    private void setBitmap(Bitmap bitmap) {
        this.picture = bitmap;
    }

    private byte[] mBitmapToArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap arrayToBitmap(byte[] array) {
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
        return compressedBitmap;
    }

    private void changedProfileImage() {
        if (!has_changed_profile_image) {//this is used to make Glide read from the entry edited_pic not picture to avoid errors. we set the boolean to true
            SharedPreferences.Editor editor = prefer.edit();
            editor.putBoolean("prefer", true);
            editor.apply();
            fetchedDoc();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                Bitmap bitmap = null;
                try {
                    bitmap = ImagePicker.getImageFromResult(mContext, resultCode, data);
                    setBitmap(bitmap);
                    Glide.with(mContext).load(bitmap).fitCenter().into(profileImage);
                    profileImage.refreshDrawableState();
                    saveNameAndDateButton.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    //do sth
                    Log.e(TAG, "Exception" + e.getMessage());
                }
            } else {

                super.onActivityResult(requestCode, resultCode, data);
            }

        } else
            Log.d(TAG, "Error on camera/Gallery");
    }

    private void saveNameAndTime() {
        RemoteMongoCollection plantProfileColl = mongoDbSetup.getCollectionByName(getString(R.string.eye_plant_plant_profiles));

        plantProfileColl.findOne(eq("user_id", user_id)).continueWithTask((Continuation) task -> {
            String plantName = flowerNameEditText.getText().toString();
            String plantDate = flowerTimeEditText.getText().toString();

            if (task.isSuccessful()) {

                if (getBitmap() != null) {
                    bitmap = getBitmap();
                    byte[] pwr = mBitmapToArray(bitmap);
                    bsonBinary = new BsonBinary(pwr);

                    plantProfileColl.updateOne(null, set("edited_pic", bsonBinary), new RemoteUpdateOptions());

                    changedProfileImage();

                    reloadHome();
                }
                if (!TextUtils.isEmpty(flowerNameEditText.getText().toString()) && !TextUtils.isEmpty(flowerTimeEditText.getText().toString())) {
                    plantProfileColl.updateOne(null, set("name", plantName), new RemoteUpdateOptions());
                    plantProfileColl.updateOne(null, set("birthday", plantDate), new RemoteUpdateOptions());

                    reloadHome();
                }
                if (TextUtils.isEmpty(flowerNameEditText.getText())) {
                    flowerNameEditText.setError(getString(R.string.choose_a_user_name));

                    adjustNameAndDate();
                }
                if (TextUtils.isEmpty(flowerTimeEditText.getText())) {
                    flowerTimeEditText.setError(getString(R.string.choose_a_birthday));
                    adjustNameAndDate();
                }

            }

            return null;
        }).addOnFailureListener(e -> Log.d(TAG, "onFailure: Error: " + e.getCause()));

    }

    private void saveChanges() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("Are you sure you want to change conditions?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                (dialog, id) ->
                {
                    /// TODO: 11/29/2019  code for updating conditions in the database
                    RemoteMongoCollection plantProfileColl = mongoDbSetup.getCollectionByName("plant_profiles");
                    plantProfileColl.findOne(eq("user_id", user_id)).continueWithTask((Continuation) task -> {
                        if (task.isSuccessful()) {
                            ArrayList<Integer> humidityValues = new ArrayList<>();

                            String minH = hum_min.getText().toString();
                            String maxH = hum_max.getText().toString();

                            ArrayList<Integer> temperatureValues = new ArrayList<>();

                            String minT = temp_min.getText().toString();
                            String maxT = temp_max.getText().toString();

                            ArrayList<Integer> sunlightValues = new ArrayList<>();

                            String minS = light_min.getText().toString();
                            String maxS = light_max.getText().toString();

                            if (checkEmptyInput(minH, maxH, minT, maxT, minS, maxS)) {
                                if (checkValueInput(minH, maxH, minT, maxT, minS, maxS)) {
                                    humidityValues.add(Integer.valueOf(minH));
                                    humidityValues.add(Integer.valueOf(maxH));

                                    temperatureValues.add(Integer.valueOf(minT));
                                    temperatureValues.add(Integer.valueOf(maxT));

                                    sunlightValues.add(Integer.valueOf(minS));
                                    sunlightValues.add(Integer.valueOf(maxS));

                                    plantProfileColl.updateOne(null, set("humidity", humidityValues), new RemoteUpdateOptions());
                                    plantProfileColl.updateOne(null, set("temperature", temperatureValues), new RemoteUpdateOptions());
                                    plantProfileColl.updateOne(null, set("sunlight", sunlightValues), new RemoteUpdateOptions());
                                    reloadHome();
                                } else adjustConditions();
                            } else adjustConditions();

                        } else
                            Log.e(TAG, "error");
                        return null;
                    })
                            .addOnFailureListener(e ->
                                    Log.d(TAG, "onFailure: Error: " + e.getCause()));

                    dialog.cancel();
                });

        builder1.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());
        hum_min.setEnabled(false);
        hum_max.setEnabled(false);
        temp_min.setEnabled(false);
        temp_max.setEnabled(false);
        light_min.setEnabled(false);
        light_max.setEnabled(false);
        saveChangesButton.setVisibility(View.GONE);

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private boolean checkEmptyInput(String minH, String maxH, String minT, String maxT, String minS, String maxS) {
        boolean minH_empty = TextUtils.isEmpty(minH);
        boolean maxH_empty = TextUtils.isEmpty(maxH);

        boolean minT_empty = TextUtils.isEmpty(minT);
        boolean maxT_empty = TextUtils.isEmpty(maxT);

        boolean minS_empty = TextUtils.isEmpty(minS);
        boolean maxS_empty = TextUtils.isEmpty(maxS);

        if (minH_empty || maxH_empty) {
            hum_max.setError(getString(R.string.humidity_min_max_error));
            return false;
        } else if (minT_empty || maxT_empty) {
            temp_max.setError(getString(R.string.temperature_min_max_error));
            return false;
        } else if (minS_empty || maxS_empty) {
            light_max.setError(getString(R.string.sunlight_min_max_error));
            return false;
        } else return true;
    }

    private boolean checkValueInput(String minH, String maxH, String minT, String maxT, String minS, String maxS) {
        boolean m = Integer.valueOf(minH) <= 0 || Integer.valueOf(maxH) >= 100;
        boolean t = Integer.valueOf(minT) <= 0 || Integer.valueOf(maxT) >= 40;
        boolean s = Integer.valueOf(minS) <= 0 || Integer.valueOf(maxS) >= 100;

        if (m && t && s) {
            hum_max.setError(getString(R.string.humidity_min_max_error));
            temp_max.setError(getString(R.string.temperature_min_max_error));
            light_max.setError(getString(R.string.sunlight_min_max_error));
            return false;
        } else if (m || t || s) {
            if (m) {
                hum_max.setError(getString(R.string.humidity_min_max_error));
            }
            if (t) {
                temp_max.setError(getString(R.string.temperature_min_max_error));
            }
            if (s) {
                light_max.setError(getString(R.string.sunlight_min_max_error));
            }
            return false;
        } else return true;
    }

    private void reloadHome() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.useThisFragmentID, new HomeFragment())
                .commit();
    }

    private void adjustNameAndDate() {
        flowerNameEditText.setEnabled(true);
        flowerTimeEditText.setEnabled(true);
        saveNameAndDateButton.setVisibility(View.VISIBLE);
    }

    private void adjustConditions() {
        hum_min.setEnabled(true);
        hum_max.setEnabled(true);
        temp_min.setEnabled(true);
        temp_max.setEnabled(true);
        light_min.setEnabled(true);
        light_max.setEnabled(true);
        saveChangesButton.setVisibility(View.VISIBLE);
    }

    private Document getPlantProfileDoc() {
        return plantProfileDoc;
    }

    private void setPlantProfileDoc(Document plantProfileDoc) {
        this.plantProfileDoc = plantProfileDoc;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.adjust_name_and_date:
                adjustNameAndDate();
                break;

            case R.id.adjust_conditions:
                adjustConditions();
                break;

            case R.id.save_name_and_date:
                saveNameAndTime();
                break;

            case R.id.save_changes:
                saveChanges();
                break;

            case R.id.profileImage:
            case R.id.change_picture:
                alertDialog();
                break;
        }
    }

}