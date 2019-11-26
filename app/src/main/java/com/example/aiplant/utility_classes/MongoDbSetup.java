package com.example.aiplant.utility_classes;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.aiplant.home.HomeActivity;
import com.example.aiplant.model.PlantProfile;
import com.example.aiplant.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchAuth;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoIterable;

import org.bson.Document;
import org.bson.types.Binary;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * File created by tcarau18
 **/
public class MongoDbSetup {

    private static final String TAG = "MongoDbSetup";
    private Context mContext;

    private static StitchAppClient appClient;
    private static StitchAuth stitchAuth;
    private static StitchUser stitchUser;

    // Configure Google Sign In
    private GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("556950483367-9ekr8qotdiv7md2r1tckudh09damgof0.apps.googleusercontent.com") //token taken from firebase authentication data
            .requestServerAuthCode("556950483367-9ekr8qotdiv7md2r1tckudh09damgof0.apps.googleusercontent.com") //auth code from firebase authentication data
            .requestEmail()
            .build();

    private static InitAppClient initAppClient = new InitAppClient();

    private static GoogleSignInClient mGoogleSignInClient;
    private List<Document> plantsList;
    private List<Document> plant_profiles;

    private MongoDbSetup(Context context) {
        synchronized (MongoDbSetup.class) {
            mContext = context;
            mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        }

    }

    public static GoogleSignInClient getGoogleSignInClient() {
        return mGoogleSignInClient;
    }

    public StitchAuth getStitchAuth() {
        return stitchAuth = getAppClient().getAuth();

    }

    public StitchUser getStitchUser() {
        return stitchUser = stitchAuth.getUser();
    }

    public static MongoDbSetup getInstance(Context context) {

        return new MongoDbSetup(context);
    }

    public void runAppClientInit() {
        initAppClient.run(() ->
                appClient =
                        Stitch.initializeDefaultAppClient("eye-plant-tilrj")

        );

    }

    public StitchAppClient getAppClient() {
        return appClient;
    }

    public String getAppClientId() {
        return Objects.requireNonNull(getStitchUser()).getId();
    }

    private static RemoteMongoClient getRemoteMongoDbClient() {

        return appClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
    }

    public RemoteMongoCollection<Document> getCollectionByName(String collectionName) {
        return
                getRemoteMongoDbClient().getDatabase("eye_plant")
                        .getCollection(collectionName);
    }

    public Document createUserDocument(String id, String displayName,
                                       String userMail, String photoURL, int nrOfPlant, String birthday) {

        return new Document("logged_user_id", id)
                .append("name", displayName)
                .append("email", userMail)
                .append("picture", photoURL)
                .append("number_of_plants", nrOfPlant)
                .append("birthday", birthday);
    }

    public static byte[] mBitmapToArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap arrayToBitmap(byte[] array) {
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
        return compressedBitmap;
    }

    public void createPlantProfileDocument(String collectionName, String profileId, String plantName, String birthday,
                                           int minHumidity, int maxHumidity, int minTemperature,
                                           int maxTemperature, int minSun,
                                           int maxSun, byte[] picture) {

        List<Integer> humidity = new ArrayList<>();
        humidity.add(0, minHumidity);
        humidity.add(1, maxHumidity);
        List<Integer> temperature = new ArrayList<>();
        temperature.add(0, minTemperature);
        temperature.add(1, maxTemperature);
        List<Integer> sunlight = new ArrayList<>();
        sunlight.add(0, minSun);
        sunlight.add(1, maxSun);


        Document createdPlantProfile = new Document("user_id", getAppClientId())
                .append("profile_id", profileId)
                .append("name", plantName)
                .append("birthday", birthday)
                .append("humidity", humidity)
                .append("temperature", temperature)
                .append("sunlight", sunlight)
                .append("picture", picture)
                .append("measured_humidity", 0)
                .append("measured_temperature", 0)
                .append("measured_sunlight", 0);

        getCollectionByName(collectionName).insertOne(createdPlantProfile);
    }

    public void fetchUserData(StitchUser stitchUser, TextView userNameTextView
            , CircularImageView profilePic) {
        {

            try {

                RemoteMongoCollection user_coll = getCollectionByName("users");
                user_coll.findOne(new Document("logged_user_id", stitchUser.getId())).continueWith(task -> {

                    if (!task.isSuccessful()) {
                        Log.d(TAG, "then: " + task.getException());
                    }
                    try {
                        Document fetchedDoc = (Document) task.getResult();
                        Log.d(TAG, "then: " + fetchedDoc.toJson());

                        String name = fetchedDoc.getString("name");
                        String email = fetchedDoc.getString("email");
                        String photoURL = fetchedDoc.getString("picture");

                        Glide.with(mContext).load(photoURL).fitCenter().into(profilePic);

                        userNameTextView.setText(name);

                    } catch (Throwable throwable) {
                        Log.d(TAG, "then: throwable: " + throwable);
                    }
                    return null;
                });
            } catch (Exception e) {
                Log.d(TAG, "fetchUserData: ERROR: " + e.getMessage());
            }
        }
    }

    public void findPlantsList() {
        try {

            RemoteMongoCollection<Document> plants = getCollectionByName("plants");
            RemoteMongoIterable<Document> plantIterator = plants.find();

            final List<Document> docs = new ArrayList<>();

            plantIterator
                    .forEach(document -> {
                        docs.add(document);
                        Log.d(TAG, "doc" + docs.toString());
                        Log.d(TAG, "doc" + docs.size());
                    })

                    .addOnCompleteListener(task -> {
                        Log.d(TAG, "doc" + docs.toString());

                        setListOfPlants(docs);
                    });
        } catch (Throwable e) {
            Log.e(TAG, "NullPointerException: " + e.getMessage());
        }
    }

    public void bluetoothConnectivity() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(mContext, "Device doesn't Support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "it has it", Toast.LENGTH_SHORT).show();

        }
    }

    public void findPlantProfileList(
            EditText flowerName, EditText timeOfCreation,
            SeekBar hum_current, SeekBar temp_current, SeekBar light_current
            , CircleImageView imageView
    ) {
        try {

            RemoteMongoCollection<Document> plant_profiles = getCollectionByName("plant_profiles");
            RemoteMongoIterable<Document> plantIterator = plant_profiles.find();

            final List<Document> docs = new ArrayList<>();

            plantIterator
                    .forEach(document -> {
                        docs.add(document);
                        Log.d(TAG, "doc" + docs.size());
                    })

                    .addOnCompleteListener(task -> {
                        Log.d(TAG, "doc" + docs.size());

                        setupPlantProfileList(docs, flowerName, timeOfCreation, hum_current, temp_current, light_current,imageView);

                    });
        } catch (Throwable e) {
            Log.e(TAG, "NullPointerException: " + e.getMessage());
        }
    }

    public List<PlantProfile> setupPlantProfileList(List<Document> docs,
                                                    EditText flowerName, EditText timeOfCreation,
                                                    SeekBar hum_current, SeekBar temp_current, SeekBar light_current
            , CircleImageView imageView
    ) {
        try {

            for (Document profile : docs) {
                String profile_id = profile.getString("profile_id");
                String user_id = profile.getString("user_id");
                String name = profile.getString("name");
                String birthday = profile.getString("birthday");
                int measured_humidity = profile.getInteger("measured_humidity");
                int measured_temperature = profile.getInteger("measured_temperature");
                int measured_sunlight = profile.getInteger("measured_sunlight");

                Binary picture = profile.get("picture", Binary.class);
                byte[] pic_bytes = picture.getData();
                Bitmap bitmap = BitmapFactory.decodeByteArray(pic_bytes, 0, pic_bytes.length);

                ArrayList<Integer> humidity = profile.get("humidity", ArrayList.class);
                ArrayList<Integer> temperature = profile.get("temperature", ArrayList.class);
                ArrayList<Integer> sunlight = profile.get("sunlight", ArrayList.class);

                int min_hum = humidity.get(0);
                int max_hum = humidity.get(1);
                int min_temp = temperature.get(0);
                int max_temp = temperature.get(1);
                int min_sun = sunlight.get(0);
                int max_sun = sunlight.get(1);
                Log.d(TAG, "profile" + user_id);


                PlantProfile profileForUser = new PlantProfile(user_id, profile_id, name, birthday
                        , min_hum, max_hum, min_temp, max_temp, min_sun, max_sun, pic_bytes, measured_humidity, measured_temperature, measured_sunlight);

                flowerName.setText(profileForUser.getName());
                timeOfCreation.setText(profileForUser.getBirthday());
                hum_current.setProgress(profileForUser.getMeasured_humidity());
                temp_current.setProgress(profileForUser.getMeasured_temperature());
                light_current.setProgress(profileForUser.getMeasured_sunlight());

                Glide.with(mContext).load(bitmap).centerCrop().into(imageView);
                Log.d(TAG, "setupPlantProfileList: plantProfile: " + plant_profiles);
            }

            return null;

        } catch (Throwable e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return null;
    }

    private void setListOfPlants(List<Document> docs) {
        this.plantsList = docs;
    }

    public List<Document> getPlantsList() {
        return plantsList;
    }

    private void setListOfPlantProfiles(List<Document> docs) {
        this.plant_profiles = docs;
    }

    public List<Document> getPlantProfilesList() {
        return plant_profiles;
    }

    public void checkIfExists(RemoteMongoCollection coll, Document documentToCheck) {
        coll.findOne().continueWith(task -> {
            try {
                if (task.getResult(documentToCheck.getClass()) == null) {
                    int number_of_plants = documentToCheck.getInteger("number_of_plants");

                    coll.insertOne(documentToCheck);

                    new User(documentToCheck.getString("logged_user_id"),
                            documentToCheck.getString("name"),
                            documentToCheck.getString("email"), documentToCheck.getString("picture"),
                            number_of_plants, documentToCheck.getString("birthday"));

                    goToWhereverWithFlags(mContext, mContext, HomeActivity.class);
                } else goToWhereverWithFlags(mContext, mContext, HomeActivity.class);
            } catch (Throwable throwable) {
                Log.d(TAG, "checkIfExists:Error throwable: " + throwable);
            }

            return null;
        });
    }

    public void updateOne() {

        getCollectionByName("plants").updateOne(eq("plant_id", "5dcd80de1e36d318eb99fe8c"),
                combine(set("size.uom", "cm")));

    }

    public void deleteOne() {
        getCollectionByName("plants").deleteOne(eq("plant_id", "5dcd80de1e36d318eb99fe8c"));

    }

    public void goToWhereverWithFlags(Context activityContext, Context c, Class<? extends
            AppCompatActivity> cl) {

        activityContext.startActivity(new Intent(c, cl).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goToWhereverWithOutFlags(Context activityContext, Context c, Class<? extends
            AppCompatActivity> cl) {
        activityContext.startActivity(new Intent(c, cl));
    }

    public static class InitAppClient {
        private AtomicBoolean done = new AtomicBoolean();

        void run(Runnable task) {
            if (done.get()) return;
            if (done.compareAndSet(false, true)) {
                task.run();
            }
        }
    }
}
