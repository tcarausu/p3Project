package com.example.aiplant.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.aiplant.R;
import com.example.aiplant.cameraAndGallery.ImagePicker;
import com.example.aiplant.cameraAndGallery.PictureConversion;
import com.example.aiplant.interfcaes.DateValidator;
import com.example.aiplant.login.LoginActivity;
import com.example.aiplant.model.PlantProfile;
import com.example.aiplant.search.SearchActivity;
import com.example.aiplant.services.NotificationService;
import com.example.aiplant.services.ScheduledFetch;
import com.example.aiplant.utility_classes.DateValidatorUsingDateFormat;
import com.example.aiplant.utility_classes.MongoDbSetup;
import com.google.android.gms.tasks.Continuation;
import com.mongodb.stitch.android.core.auth.StitchAuth;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;

import org.bson.BsonBinary;
import org.bson.Document;
import org.bson.types.Binary;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class HomeFragment extends androidx.fragment.app.Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "HomeFragment";
    private static final int REQUEST_CAMERA = 22;
    private static final int REQUEST_GALLERY = 33;
    private static final int REQUEST_CODE = 11;

    //ValuesToUse
    private AtomicInteger requestCode = new AtomicInteger();
    private String currentDay = "Day_Of_The_week";
    private static final String LAST_TEXT_NAME = "", LAST_TEXT_DATE = "";

    private Bundle savedInstanceState;
    //data
    private MongoDbSetup mongoDbSetup;
    private static StitchUser mStitchUser;
    private PlantProfile profileForUser;

    // widgets
    private Button saveNameAndDateButton, saveChangesButton;
    private TextView change_picture, temperature_text, humidity_text, sunlight_text, hum_current, temp_current, light_current,
            humidity_min_value_text, humidity_current_value_text, humidity_max_value_text,
            temperature_min_value_text, temperature_current_value_text, temperature_max_value_text,
            light_min_value_text, light_current_value_text, light_max_value_text, textBtn;
    private SeekBar humiditySeekBar, temperatureSeekBar, lightSeekBar;
    private Spinner spinner;
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
    private RelativeLayout topLayout, bottomLayout, textLayout;
    //validators
    private DateValidator validator = new DateValidatorUsingDateFormat("dd/MM/yyyy");
    private boolean moodH, moodS, moodT;

    //Fetch
    private NotificationService mNotificationService;
    private PictureConversion pictureConverter;
    private ImagePicker imagePicker;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private StitchAuth mStitchAuth;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        setup();
        initLayout(v);
        dialog = new ProgressDialog(mContext);
        dialog.setIcon(R.mipmap.eye_logo);
        dialog.setTitle("Loading your plants...");
        dialog.show();

        checkPermissions();
        disableEditText();
        getActivity().startService(new Intent(mContext, ScheduledFetch.class));
        fetchedDoc();
        refreshDrawableState();
        buttonListeners();
        spinnerListener();
        return v;
    }

    private void spinnerListener() {
        spinnerAdapter = ArrayAdapter.createFromResource(mContext, R.array.spinner_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setNotifyOnChange(true);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setup() {
        try {
            mContext = getActivity();
            mongoDbSetup = ((HomeActivity) getActivity()).getMongoDbForLaterUse();
            mStitchAuth = mongoDbSetup.getAppClient().getAuth();
            mStitchUser = mStitchAuth.getUser();
            user_id = mStitchUser.getId();
            prefer = getActivity().getSharedPreferences("prefer", MODE_PRIVATE);
            has_changed_profile_image = prefer.getBoolean("prefer", false);
            mNotificationService = new NotificationService();
            imagePicker = new ImagePicker();
            pictureConverter = new PictureConversion();
        } catch (Exception e) {
            Log.d(TAG, "setup: error: " + e.getLocalizedMessage());
            mStitchAuth.logout();
            mongoDbSetup.intentWithFlag(mContext, mContext, LoginActivity.class);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void displayLayout() {
        topLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
        textLayout.setVisibility(View.GONE);
    }

    private void displayNothing() {
        topLayout.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
        textLayout.setVisibility(View.VISIBLE);
    }

    /**
     * This method find and fetches data from database based on the Stitch user's Id, and Plant Collection.
     * <p>
     * It checks for null and successful retrieval of the document from the database. If that is successful then
     * it sets up the Plant Profile with that information. Otherwise display error.
     */
    private void fetchedDoc() {
        try {
            String value = mStitchUser.getId(), key = "user_id", collectionName = "plant_profiles";
            if (mongoDbSetup.checkInternetConnection(Objects.requireNonNull(getActivity()))) {
                RemoteMongoCollection<Document> collection = mongoDbSetup.getCollection(collectionName);
                collection.findOne(eq(key, value)).continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Document document = task.getResult();
                        dialog.dismiss();
                        displayLayout();

                        setupPlantProfile(document);
                        return task;
                    } else
                        dialog.dismiss();
                    displayNothing();
                    return null;

                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: Error: " + e.getCause()));
            } else {
                Toast.makeText(getActivity(), getString(R.string.check_internet_connection_display_profile), Toast.LENGTH_LONG).show();
                displayNothing();
            }
        } catch (Exception e) {
            Log.d(TAG, "fetchedDoc: error: " + e.getCause());
        }
    }

    /**
     * @param profile Document fetched by the system corresponding to the user profile.
     *                Takes as a parameter the Fetched Document from the Plant profile, if any.
     *                then the document feeds all the parameters to a Plant Profile, which is later on used to feed all the Layout's widgets.
     */
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

        PlantProfile profileForUser = new PlantProfile(name, user_id, profile_id, birthday, picture,
                pic_bytes, min_hum, max_hum, min_temp, max_temp, min_sun, max_sun, measured_humidity, measured_temperature,
                measured_sunlight);

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

        HumiditySeekBarData(profileForUser);
        TemperatureSeekBarData(profileForUser);
        SunlightSeekBarData(profileForUser);

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

        if (isMoodT() && isMoodS() && isMoodH()) {
            mood_pic.setImageResource(R.drawable.happy_leafo);
        } else mood_pic.setImageResource(R.drawable.unhappy_leafo);

    }

    private void initLayout(View v) {
        spinner = v.findViewById(R.id.spinnerHomeFragment);
        topLayout = v.findViewById(R.id.top_layout);
        bottomLayout = v.findViewById(R.id.bottom_layout);
        textLayout = v.findViewById(R.id.text_layout);
        textBtn = v.findViewById(R.id.create_textbtn);
        textLayout.setVisibility(View.GONE);


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

        //Buttons
        saveNameAndDateButton = v.findViewById(R.id.save_name_and_date);
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

        mood_pic = v.findViewById(R.id.mood_pic);
        mood_pic.setImageResource(R.drawable.happy_leafo);
    }

    private void buttonListeners() {
        saveNameAndDateButton.setOnClickListener(this);
        saveChangesButton.setOnClickListener(this);
        profileImage.setOnClickListener(this);
        change_picture.setOnClickListener(this);
        textBtn.setOnClickListener(this);
        textLayout.setOnClickListener(this);
    }

    private void HumiditySeekBarData(PlantProfile profileForUser) {
        int progress = profileForUser.getMeasured_humidity();
        humiditySeekBar.setProgress(progress);

        if (progress <= profileForUser.getMinHumid()) {
            mNotificationService.createNotification(mContext, getString(R.string.humidity_min_max_error), getString(R.string.humidity_min_max_error), requestCode);
            setMoodH(false);
        } else if (progress >= profileForUser.getMaxHumid()) {

            setMoodH(false);

            mNotificationService.createNotification(mContext, getString(R.string.humidity_min_max_error), getString(R.string.humidity_min_max_error), requestCode);
        } else setMoodH(true);
    }

    private void TemperatureSeekBarData(PlantProfile profileForUser) {
        int progress = profileForUser.getMeasured_temperature();
        temperatureSeekBar.setProgress(progress);
        if (progress <= profileForUser.getMinTemp()) {
            setMoodT(false);

            mNotificationService.createNotification(mContext, getString(R.string.temperature_min_max_error), getString(R.string.temperature_min_max_error), requestCode);
        } else if (progress >= profileForUser.getMaxTemp()) {

            setMoodT(false);

            mNotificationService.createNotification(mContext, getString(R.string.temperature_min_max_error), getString(R.string.temperature_min_max_error), requestCode);

        } else
            setMoodT(true);
    }

    private void SunlightSeekBarData(PlantProfile profileForUser) {
        int progress = profileForUser.getMeasured_sunlight();
        lightSeekBar.setProgress(progress);
        if (progress <= profileForUser.getMinSun()) {
            setMoodS(false);

            mNotificationService.createNotification(mContext, getString(R.string.plant_needs_more_light), getString(R.string.plant_needs_more_light), requestCode);

        } else if (progress >= profileForUser.getMaxSun()) {
            setMoodS(false);
            mNotificationService.createNotification(mContext, getString(R.string.plant_needs_less_light), getString(R.string.plant_needs_less_light), requestCode);
        } else setMoodS(true);
    }

    /**
     * Method asking the user for giving the app permissions to access camera and gallery
     */
    private void checkPermissions() {
        new Handler().post(() -> {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(permissions, REQUEST_GALLERY | REQUEST_CAMERA);
            }
        });
    }

    /**
     * Method responsible for refreshing the drawable state of the HomeFragment
     */
    private void refreshDrawableState() {
        change_picture.refreshDrawableState();
        temperature_text.refreshDrawableState();
        humidity_text.refreshDrawableState();
        sunlight_text.refreshDrawableState();
        hum_current.refreshDrawableState();
        temp_current.refreshDrawableState();
        light_current.refreshDrawableState();
        humidity_min_value_text.refreshDrawableState();
        humidity_current_value_text.refreshDrawableState();
        humidity_max_value_text.refreshDrawableState();
        temperature_min_value_text.refreshDrawableState();
        temperature_current_value_text.refreshDrawableState();
        temperature_max_value_text.refreshDrawableState();
        light_min_value_text.refreshDrawableState();
        light_current_value_text.refreshDrawableState();
        light_max_value_text.refreshDrawableState();
        humiditySeekBar.refreshDrawableState();
        temperatureSeekBar.refreshDrawableState();
        lightSeekBar.refreshDrawableState();
        profileImage.refreshDrawableState();

    }

    /**
     * Method responsible for disabling all of the edit texts in HomeFragment
     */
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

    private void changedProfileImage() {
        if (!has_changed_profile_image) {//this is used to make Glide read from the entry edited_pic not picture to avoid errors. we set the boolean to true
            SharedPreferences.Editor editor = prefer.edit();
            editor.putBoolean("prefer", true);
            editor.apply();
            fetchedDoc();
        }
    }

    /**
     * Method responsible for refreshing the home page
     */
    private void refreshPage() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                Bitmap bitmap = null;
                try {
                    bitmap = imagePicker.getImageFromResult(mContext, resultCode, data);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


    }

    /**
     * Method responsible for saving name and data of plant, changed by the user.
     * <p>
     * It gets data from Plant Profile Collection, if task is successful it continues by checking the bitmap,
     * in case tht is successful it updates picture and proceed to check for empty or invalid input for name and date.
     * If both are not empty and the date is valid, it proceeds by updating the data.
     */
    private void saveNameAndTime() {
        RemoteMongoCollection plantProfileColl = mongoDbSetup.getCollection(getString(R.string.eye_plant_plant_profiles));
        plantProfileColl.findOne(eq("user_id", user_id)).continueWithTask((Continuation) task -> {
            String plantName = flowerNameEditText.getText().toString();
            String plantDate = flowerTimeEditText.getText().toString();
            if (task.isSuccessful()) {
                if (getBitmap() != null) {
                    bitmap = getBitmap();
                    byte[] pwr = pictureConverter.bitmapToByteArray(bitmap);
                    bsonBinary = new BsonBinary(pwr);

                    plantProfileColl.updateOne(null, set("edited_pic", bsonBinary), new RemoteUpdateOptions());

                    changedProfileImage();

                    refreshDrawableState();
                }
                if (TextUtils.isEmpty(flowerNameEditText.getText())) {
                    flowerNameEditText.setError(getString(R.string.choose_a_user_name));

                    adjustNameAndDate();
                    refreshDrawableState();
                }
                if (TextUtils.isEmpty(flowerTimeEditText.getText())) {
                    flowerTimeEditText.setError(getString(R.string.choose_a_birthday));
                    adjustNameAndDate();
                    flowerTimeEditText.requestFocus();
                }
                if (!validator.isValid(flowerTimeEditText.getText().toString())) {
                    flowerTimeEditText.setError(getString(R.string.choose_a_birthday_properly));
                    adjustNameAndDate();
                    flowerTimeEditText.requestFocus();
                } else if (!TextUtils.isEmpty(flowerNameEditText.getText().toString())
                        && !TextUtils.isEmpty(flowerTimeEditText.getText().toString()) && validator.isValid(flowerTimeEditText.getText().toString())) {
                    plantProfileColl.updateOne(null, set("name", plantName), new RemoteUpdateOptions());
                    plantProfileColl.updateOne(null, set("birthday", plantDate), new RemoteUpdateOptions());
                    refreshDrawableState();

                    Toast.makeText(mContext, getString(R.string.saving_changes), Toast.LENGTH_LONG).show();
                    refreshPage();
                }
            }

            return null;
        }).addOnFailureListener(e -> Log.d(TAG, "onFailure: Error: " + e.getCause()));

    }

    /**
     * Method responsible for saving minimum and maximum conditions of plant, changed by the user.
     * <p>
     * This method takes input for minimum and maximum of all our 3 measurements: Humidity, Temperature and
     * Sunlight. After the user thinks that the input for the measurements are fine he can proceed to update them.
     * <p>
     * He is displayed an dialogue with the "Yes" and "No", where he can select and update the values if the minimum
     * and maximum are not less or more then the applicable for each field.
     * If that's appropriate he updates the values, if not he is requested to change them, because he gets appropriate errors.
     */
    private void saveChanges() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("Are you sure you want to change conditions?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                (dialog, id) ->
                {
                    /// TODO: 11/29/2019  code for updating conditions in the database
                    RemoteMongoCollection plantProfileColl = mongoDbSetup.getCollection("plant_profiles");
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
                                    refreshDrawableState();
                                } else adjustConditions();
                            } else adjustConditions();


                            //refresh if data is filled
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

    /**
     * @param minH minimum humidity value for verification
     * @param maxH maximum humidity value for verification
     * @param minT minimum temperature value for verification
     * @param maxT maximum temperature value for verification
     * @param minS minimum sunlight value for verification
     * @param maxS maximum sunlight value for verification
     * @return boolean True if there is no empty field, and false is any case has an empty input.
     */
    private boolean checkEmptyInput(String minH, String maxH, String minT, String maxT, String minS, String maxS) {
        Log.d(TAG, "checkEmptyInput: ");
        boolean minH_empty = TextUtils.isEmpty(minH);
        boolean maxH_empty = TextUtils.isEmpty(maxH);
        boolean minT_empty = TextUtils.isEmpty(minT);
        boolean maxT_empty = TextUtils.isEmpty(maxT);
        boolean minS_empty = TextUtils.isEmpty(minS);
        boolean maxS_empty = TextUtils.isEmpty(maxS);

        if (minH_empty || maxH_empty) {
            hum_max.setError(getString(R.string.humidity_min_max_error));
            hum_max.setFocusable(true);
            return false;
        } else if (minT_empty || maxT_empty) {
            temp_max.setError(getString(R.string.temperature_min_max_error));
            temp_max.setFocusable(true);
            return false;
        } else if (minS_empty || maxS_empty) {
            light_max.setError(getString(R.string.sunlight_min_max_error));
            light_max.setFocusable(true);
            return false;
        } else return true;
    }

    /**
     * @param minH minimum humidity value for verification
     * @param maxH maximum humidity value for verification
     * @param minT minimum temperature value for verification
     * @param maxT maximum temperature value for verification
     * @param minS minimum sunlight value for verification
     * @param maxS maximum sunlight value for verification
     * @return boolean True if there is issues with the measurements, and false is any case are either bigger or smaller.
     */
    private boolean checkValueInput(String minH, String maxH, String minT, String maxT, String minS, String maxS) {
        boolean m = Integer.valueOf(minH) <= 0 || Integer.valueOf(maxH) >= 100;
        boolean t = Integer.valueOf(minT) <= 0 || Integer.valueOf(maxT) >= 40;
        boolean s = Integer.valueOf(minS) <= 0 || Integer.valueOf(maxS) >= 100;

        if (m && t && s) {
            hum_max.setError(getString(R.string.humidity_min_max_error));
            temp_max.setError(getString(R.string.temperature_min_max_error));
            light_max.setError(getString(R.string.sunlight_min_max_error));
            hum_max.setFocusable(true);
            return false;
        } else if (m || t || s) {
            if (m) {
                hum_max.setError(getString(R.string.humidity_min_max_error));
                hum_max.setFocusable(true);
            }
            if (t) {
                temp_max.setError(getString(R.string.temperature_min_max_error));
                temp_max.setFocusable(true);
            }
            if (s) {
                light_max.setError(getString(R.string.sunlight_min_max_error));
                light_max.setFocusable(true);
            }
            return false;
        } else return true;
    }

    private void adjustNameAndDate() {
        flowerNameEditText.setEnabled(true);
        flowerNameEditText.requestFocus();
        flowerTimeEditText.setEnabled(true);
        saveNameAndDateButton.setVisibility(View.VISIBLE);
    }

    private void adjustConditions() {
        hum_min.setEnabled(true);
        hum_min.requestFocus();
        hum_max.setEnabled(true);
        temp_min.setEnabled(true);
        temp_max.setEnabled(true);
        light_min.setEnabled(true);
        light_max.setEnabled(true);
        saveChangesButton.setVisibility(View.VISIBLE);
        saveChangesButton.setEnabled(true);
    }

    private boolean isMoodH() {
        return moodH;
    }

    private void setMoodH(boolean moodH) {
        this.moodH = moodH;
    }

    private boolean isMoodS() {
        return moodS;
    }

    private void setMoodS(boolean moodS) {
        this.moodS = moodS;
    }

    private boolean isMoodT() {
        return moodT;
    }

    private void setMoodT(boolean moodT) {
        this.moodT = moodT;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


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
            case R.id.text_layout:
            case R.id.create_textbtn:
                ((HomeActivity) getActivity()).bnh.setBottomNavigationState(1);
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;

        }
    }

    private void removePlant() {
        getActivity().stopService(getActivity().getIntent().setClass(mContext, ScheduledFetch.class));//        getSystemService(ScheduledFetch.class).onDestroy();
        android.app.ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Deleting plant");
        progressDialog.setMessage("Removing  the plant user from storage, please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIcon(R.drawable.ai_plant);
        progressDialog.show();

        String user_id = mStitchUser.getId();
        RemoteMongoCollection user_Plants_coll = mongoDbSetup.getCollection(getResources().getString(R.string.eye_plant_plant_profiles));
        user_Plants_coll.findOne(eq("user_id", user_id)).continueWith(task -> {
            if (task.isSuccessful() && task.getException() == null) { //if we find a document
                displayNothing();
                progressDialog.dismiss();
                return user_Plants_coll.deleteOne((Document) task.getResult());
            } else //if no such document we just return the collection itself.
                progressDialog.dismiss();


            return user_Plants_coll;

        }).addOnFailureListener(e -> {
            Log.d(TAG, "then: Error: " + e.getLocalizedMessage());

        });


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        adapterView.clearDisappearingChildren();

        switch (i) {
            case 0:
                adapterView.setSelection(0);// i want an empty place so we can
                spinnerAdapter.notifyDataSetChanged();
                break;

            case 1:
                refreshPage();//refresh
                adapterView.clearFocus();
                adapterView.setSelection(0);//put the selected position back to Zero
                spinnerAdapter.notifyDataSetChanged();
                break;

            case 2:
                adjustNameAndDate();
                adapterView.clearFocus();
                adapterView.setSelection(0);
                spinnerAdapter.notifyDataSetChanged();

                break;
            case 3:
                adjustConditions();
                adapterView.clearFocus();
                adapterView.setSelection(0);
                spinnerAdapter.notifyDataSetChanged();
                break;
            case 4:
                removePlant();
                adapterView.clearFocus();
                adapterView.setSelection(0);
                spinnerAdapter.notifyDataSetChanged();
                break;

            case 5:
                adapterView.clearFocus();
                adapterView.setSelection(0);
                spinnerAdapter.notifyDataSetChanged();
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        adapterView.onCancelPendingInputEvents();
        adapterView.clearFocus();
        adapterView.clearDisappearingChildren();

    }
}
