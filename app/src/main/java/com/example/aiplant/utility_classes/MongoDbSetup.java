package com.example.aiplant.utility_classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.aiplant.interfcaes.Navigator;
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
import com.mongodb.stitch.core.auth.providers.google.GoogleAuthProvider;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * File created by tcarau18
 **/
public class MongoDbSetup implements Navigator {

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
    private GoogleSignInClient mGoogleSignInClient;

    private MongoDbSetup(Context context) {
        mContext = context;
        synchronized (this) {
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


    public StitchAuth getStitchAuth() {
        return stitchAuth = getAppClient().getAuth();

    }

    public StitchUser getStitchUser() {
        return stitchUser = stitchAuth.getUser();
    }

    public StitchAppClient getAppClient() {
        return appClient;
    }

    private String getAppClientId() {
        return Objects.requireNonNull(getStitchUser()).getId();
    }

    @Override
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
                }
            } catch (Throwable throwable) {
                Log.d(TAG, "checkIfExists:Error throwable: " + throwable);
            }

            return null;
        });
    }

    @Override
    public GoogleSignInClient googleClient() {
        return mGoogleSignInClient;
    }



    @Override
    public boolean checkInternetConnection(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isAvailable() &&
                connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
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

        getCollection(collectionName).insertOne(createdPlantProfile);
    }

    @Override
    public void intentWithFlag(Context sourceContext, Context current_Activity_Context, Class<? extends Activity> destinationClass) {
        sourceContext.startActivity(new Intent(current_Activity_Context, destinationClass)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }


    @Override
    public RemoteMongoClient getRemoteMongoDbClient() {
        return appClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public RemoteMongoCollection<Document> getCollection(String collectionName) {
        return getDatabase("eye_plant").getCollection(collectionName);
    }

    @Override
    public <DocumentT> RemoteMongoCollection<DocumentT> getCollection(String collectionName, Class<DocumentT> documentClass) {
        return getDatabase("eye_plant").getCollection(collectionName, documentClass);
    }

    @Override
    public RemoteMongoDatabase getDatabase(String databaseName) {
        return getRemoteMongoDbClient().getDatabase("eye_plant");
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
