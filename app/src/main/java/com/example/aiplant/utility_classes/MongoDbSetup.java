package com.example.aiplant.utility_classes;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aiplant.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchAuth;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoDatabase;

import org.bson.BsonBinary;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private MongoDbSetup(Context context) {
        synchronized (this) {
            mContext = context;
            Stitch.initialize(mContext);
            mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        }
    }

    public synchronized static MongoDbSetup getInstance(Context context) {

            if (context == null) {
                MongoDbSetup mongoDbSetup = new MongoDbSetup(context);
                runAppClientInit();
                return mongoDbSetup;
            } else runAppClientInit();

        return new MongoDbSetup(context);
    }

    private static void runAppClientInit() {
        initAppClient.run(() ->
                appClient = Stitch.initializeDefaultAppClient("eye-plant-tilrj"));
    }

    public synchronized GoogleSignInClient getGoogleSignInClient() {
        return mGoogleSignInClient;
    }

    public synchronized StitchAuth getStitchAuth() {
        return stitchAuth = getAppClient().getAuth();

    }

    public synchronized StitchUser getStitchUser() {
        return stitchUser = stitchAuth.getUser();
    }

    public synchronized StitchAppClient getAppClient() {
        return appClient;
    }

    private synchronized String getAppClientId() {
        return Objects.requireNonNull(getStitchUser()).getId();
    }

    private synchronized RemoteMongoClient getRemoteMongoDbClient() {

        return appClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
    }

    public synchronized RemoteMongoDatabase getDatabase() {
        return getRemoteMongoDbClient().getDatabase("eye_plant");
    }

    public RemoteMongoCollection<Document> getCollectionByName(String collectionName) {
        return
                getDatabase()
                        .getCollection(collectionName);
    }

    public Document createUserDocument(String id, String displayName,
                                       String userMail, String photoURL, int nrOfPlant, String birthday, BsonBinary edited_pic
    ) {

        return new Document("logged_user_id", id)
                .append("name", displayName)
                .append("email", userMail)
                .append("picture", photoURL)
                .append("number_of_plants", nrOfPlant)
                .append("birthday", birthday)
                .append("edited_pic", edited_pic);
    }

    public void createPlantProfileDocument(String collectionName, String profileId, String plantName, String birthday,
                                           int minHumidity, int maxHumidity, int minTemperature,
                                           int maxTemperature, int minSun,
                                           int maxSun, String url, byte[] picture) {

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
                .append("picture", url)
                .append("measured_humidity", 0)
                .append("measured_temperature", 0)
                .append("measured_sunlight", 0)
                .append("edited_pic", picture);

        getCollectionByName(collectionName).insertOne(createdPlantProfile);
    }

    public synchronized void checkIfExists(RemoteMongoCollection coll, Document documentToCheck) {
        coll.findOne().continueWith(task -> {
            try {
                if (task.getResult(documentToCheck.getClass()) == null) {
                    int number_of_plants = documentToCheck.getInteger("number_of_plants");

                    coll.insertOne(documentToCheck);

                    new User(documentToCheck.getString("logged_user_id"),
                            documentToCheck.getString("name"),
                            documentToCheck.getString("email"), documentToCheck.getString("picture"),
                            number_of_plants, documentToCheck.getString("birthday"));
                }
            } catch (Throwable throwable) {
                Log.d(TAG, "checkIfExists:Error throwable: " + throwable);
            }

            return null;
        });
    }

    public boolean checkInternetConnection(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager.getActiveNetworkInfo().isConnected();
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
