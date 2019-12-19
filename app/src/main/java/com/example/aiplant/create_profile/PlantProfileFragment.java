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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.aiplant.R;
import com.example.aiplant.cameraAndGallery.ImagePicker;
import com.example.aiplant.cameraAndGallery.PictureConversion;
import com.example.aiplant.device_connection.DeviceConnectionActivity;
import com.example.aiplant.interfcaes.DateValidator;
import com.example.aiplant.model.PlantProfile;
import com.example.aiplant.search.SearchActivity;
import com.example.aiplant.utility_classes.DateValidatorUsingDateFormat;
import com.example.aiplant.utility_classes.MongoDbSetup;

import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class PlantProfileFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE = 123;

    private CircleImageView profilePicture;
    private EditText namePlant;
    private EditText bd_day;
    private EditText bd_month;
    private EditText bd_year;
    private String birthday;
    private EditText minHumidity;
    private EditText maxHumidity;
    private EditText minSunlight;
    private EditText maxSunlight;
    private EditText minTemperature;
    private EditText maxTemperature;
    private Bitmap picture;
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private MongoDbSetup mongoDbSetup;
    private DateValidator validator = new DateValidatorUsingDateFormat("dd/MM/yyyy");
    private PictureConversion converter;
    private ImageButton mCloseButton;
    private Button createProfileBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_new_plant_profile, container, false);

        Bundle bundle = this.getArguments();
        mongoDbSetup = MongoDbSetup.getInstance(getActivity());
        converter = new PictureConversion();

        widgets(view);
        buttonListeners();
        bundleFunctionality(bundle);


        return view;
    }

    private void bundleFunctionality(Bundle bundle) {
        if (bundle != null) {
            StringBuilder stringBuilder = new StringBuilder();
            minHumidity.setText(stringBuilder.append(bundle.getString("minHumidity")).toString());
            stringBuilder.delete(0, stringBuilder.capacity());
            maxHumidity.setText(stringBuilder.append(bundle.getString("maxHumidity")).toString());
            stringBuilder.delete(0, stringBuilder.capacity());
            minSunlight.setText(stringBuilder.append(bundle.getString("minSun")));
            stringBuilder.delete(0, stringBuilder.capacity());
            maxSunlight.setText(stringBuilder.append(bundle.getString("maxSun")));
            stringBuilder.delete(0, stringBuilder.capacity());
            minTemperature.setText(stringBuilder.append(bundle.getString("minTemp")));
            stringBuilder.delete(0, stringBuilder.capacity());
            maxTemperature.setText(stringBuilder.append(bundle.getString("maxTemp")));
            stringBuilder.delete(0, stringBuilder.capacity());
        }
    }

    private void buttonListeners() {
        profilePicture.setOnClickListener(this);
        createProfileBtn.setOnClickListener(this);
        mCloseButton.setOnClickListener(this);
    }

    private void widgets(View view) {
        profilePicture = view.findViewById(R.id.profilePicPlant_imgView);
        namePlant = view.findViewById(R.id.namePlant_editText);
        bd_day = view.findViewById(R.id.bd_day);
        bd_month = view.findViewById(R.id.bd_month);
        bd_year = view.findViewById(R.id.bd_year);
        minHumidity = view.findViewById(R.id.minHumidity_editText);
        maxHumidity = view.findViewById(R.id.maxHumidity_editText);
        minSunlight = view.findViewById(R.id.minSunlight_editText);
        maxSunlight = view.findViewById(R.id.maxSunlight_editText);
        minTemperature = view.findViewById(R.id.minTemperature_editText);
        maxTemperature = view.findViewById(R.id.maxTemperature_editText);
        createProfileBtn = view.findViewById(R.id.createProfile_btn);
        mCloseButton = view.findViewById(R.id.close_button_2);
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CAMERA = 1;
            int REQUEST_GALLERY = 2;
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), permissions, REQUEST_GALLERY | REQUEST_CAMERA);
        }
    }

    private String getBirthday() {
        return birthday;
    }

    private void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String TAG = "PlantProfileFragment";
        Log.d(TAG, "request code: " + requestCode);
        Log.d(TAG, "result code: " + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                Bitmap bitmap = null;
                try {
                    bitmap = ImagePicker.getImageFromResult(getContext(), resultCode, data);
                    setPicture(bitmap);
                    Glide.with(Objects.requireNonNull(getContext())).load(getPicture()).into(profilePicture);
                    profilePicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }

        } else
            Log.d(TAG, "Error on camera/Gallery");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.createProfile_btn:
                validityChecker();
                break;

            case R.id.profilePicPlant_imgView:
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getContext());
                startActivityForResult(chooseImageIntent, REQUEST_CODE);
                checkPermissions();
                break;

            case R.id.close_button_2:
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;
        }
    }

    private void validityChecker() {
        if (mongoDbSetup.checkInternetConnection(Objects.requireNonNull(getActivity()))) {
            setBirthday(bd_day.getText().toString() + "/" + bd_month.getText().toString() + "/" + bd_year.getText().toString());
            if (getPicture() == null) {
                Toast.makeText(getContext(), R.string.please_picture, Toast.LENGTH_SHORT).show();
            } else if (namePlant.getText().toString().length() == 0) {
                namePlant.requestFocus();
                namePlant.setError(getString(R.string.error_empty));
            } else if (bd_day.getText().toString().length() == 0) {
                bd_day.requestFocus();
                bd_day.setError(getString(R.string.error_empty));
            } else if (bd_month.getText().toString().length() == 0) {
                bd_month.setError(getString(R.string.error_empty));
                bd_month.requestFocus();
            } else if (bd_year.getText().toString().length() == 0) {
                bd_year.requestFocus();
                bd_year.setError(getString(R.string.error_empty));
            } else if (!validator.isValid(getBirthday())) {
                Toast.makeText(getContext(), R.string.wrong_date, Toast.LENGTH_SHORT).show();
            } else if (minHumidity.getText().toString().length() == 0) {
                minHumidity.requestFocus();
                minHumidity.setError(getString(R.string.error_empty));
            } else if (Integer.parseInt(minHumidity.getText().toString()) < 0 ||
                    Integer.parseInt(minHumidity.getText().toString()) > Integer.parseInt(maxHumidity.getText().toString())) {
                minHumidity.requestFocus();
                minHumidity.setError(getString(R.string.humidity_min_max_error));
            } else if (maxHumidity.getText().toString().length() == 0) {
                maxHumidity.requestFocus();
                maxHumidity.setError(getString(R.string.error_empty));
            } else if (Integer.parseInt(maxHumidity.getText().toString()) > 100) {
                maxHumidity.requestFocus();
                maxHumidity.setError(getString(R.string.humidity_min_max_error));
            } else if (minTemperature.getText().toString().length() == 0) {
                minTemperature.requestFocus();
                minTemperature.setError(getString(R.string.error_empty));
            } else if (Integer.parseInt(minTemperature.getText().toString()) <= 0 ||
                    Integer.parseInt(minTemperature.getText().toString()) > Integer.parseInt(maxTemperature.getText().toString())) {
                minTemperature.requestFocus();
                minTemperature.setError(getString(R.string.temperature_min_max_error));
            } else if (maxTemperature.getText().toString().length() == 0) {
                maxTemperature.requestFocus();
                maxTemperature.setError(getString(R.string.error_empty));
            } else if (Integer.parseInt(maxTemperature.getText().toString()) > 40) {
                maxTemperature.requestFocus();
                maxTemperature.setError(getString(R.string.temperature_min_max_error));
            } else if (minSunlight.getText().toString().length() == 0) {
                minSunlight.requestFocus();
                minSunlight.setError(getString(R.string.error_empty));
            } else if (Integer.parseInt(minSunlight.getText().toString()) <= 0 ||
                    Integer.parseInt(minSunlight.getText().toString()) > Integer.parseInt(maxSunlight.getText().toString())) {
                minSunlight.requestFocus();
                minSunlight.setError(getString(R.string.sunlight_min_max_error));
            } else if (maxSunlight.getText().toString().length() == 0) {
                maxSunlight.requestFocus();
                maxSunlight.setError(getString(R.string.error_empty));
            } else if (Integer.parseInt(maxSunlight.getText().toString()) > 100) {
                maxSunlight.requestFocus();
                maxSunlight.setError(getString(R.string.sunlight_min_max_error));

            } else {
                createProfile();
                mongoDbSetup.intentWithFlag(Objects.requireNonNull(getActivity()), getActivity(), DeviceConnectionActivity.class);
            }
        } else
            Toast.makeText(getActivity(), getString(R.string.check_internet_connection_create_profile), Toast.LENGTH_LONG).show();
    }

    private void createProfile() {

        PlantProfile profile = new PlantProfile.Builder()
                .withName(namePlant.getText().toString())
                .withAge(getBirthday())
                .withHumid(Integer.parseInt(minHumidity.getText().toString()),
                        Integer.parseInt(maxHumidity.getText().toString()))
                .withTemp(Integer.parseInt(minTemperature.getText().toString()),
                        Integer.parseInt(maxTemperature.getText().toString()))
                .withSun(Integer.parseInt(minSunlight.getText().toString()),
                        Integer.parseInt(maxSunlight.getText().toString()))
                .withUrl(null)
                .build();

        mongoDbSetup.createPlantProfileDocument(
                getResources().getString(R.string.eye_plant_plant_profiles),
                profile.getProfileId(), profile.getName(),
                profile.getBirthday(), profile.getMinHumid(), profile.getMaxHumid(),
                profile.getMinTemp(), profile.getMaxTemp(), profile.getMinSun(),
                profile.getMaxSun(), profile.getUrl(), converter.bitmapToByteArray(getPicture()));
    }
}
