package com.example.aiplant.utility_classes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiplant.R;
import com.example.aiplant.home.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;


public class MongoDbSetup {

    private static final String TAG = "MongoDbSetup";
    private Context mContext;

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static StitchAppClient appClient;
    private static RemoteMongoClient mongoClient;
    private static String databaseName = "eye_plant";

    // Configure Google Sign In
    private GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("556950483367-9ekr8qotdiv7md2r1tckudh09damgof0.apps.googleusercontent.com") //token taken from firebase authentication data
            .requestServerAuthCode("556950483367-9ekr8qotdiv7md2r1tckudh09damgof0.apps.googleusercontent.com") //auth code from firebase authentication data
            .requestEmail()
            .build();

    private static Once once = new Once();

    private static GoogleSignInClient mGoogleSignInClient;

    public MongoDbSetup(Context context) {
        synchronized (MongoDbSetup.class) {
            mContext = context;
            mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        }
    }

    public static GoogleSignInClient getClient() {
        return mGoogleSignInClient;
    }

    public static MongoDbSetup getInstance(Context context) {

        return new MongoDbSetup(context);
    }

    public static void runAppClientInit() {
        once.run(() ->
                appClient =
                        Stitch.initializeDefaultAppClient("eye-plant-tilrj")

        );

    }

    public static String getAppClientId (){
         return Objects.requireNonNull(appClient.getAuth().getUser()).getId();
    }

    public static StitchAppClient getAppClient() {
        return appClient;
    }

    private static RemoteMongoClient getRemoteMongoDbClient() {

        return appClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
    }

    public static RemoteMongoCollection<Document> getPlants_Collection() {
        return
                getRemoteMongoDbClient().getDatabase("eye_plant")
                        .getCollection("plants");
    }

    public static RemoteMongoCollection<Document> getUsers_collection(String databaseName, String collectionName) {
        return
                getRemoteMongoDbClient().getDatabase(databaseName)
                        .getCollection(collectionName);
    }

    public void goToWhereverWithFlags(Context activityContext, Context c, Class<? extends AppCompatActivity> cl) {

        activityContext.startActivity(new Intent(c, cl).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }


    public void goToWhereverWithOutFlags(Context activityContext, Context c, Class<? extends AppCompatActivity> cl) {
        activityContext.startActivity(new Intent(c, cl));
    }

    public static Document createUserDocument(String id, String displayName,
                                              String userMail, String photoURL, int nrOfPlant, String birthday) {

        return new Document(
                "logged_user_id",
                id)
                .append(
                        "name",
                        displayName)
                .append(
                        "email",
                        userMail)
                .append("picture",
                        photoURL)
                .append("number_of_plants",
                        nrOfPlant)
                .append("birthday",
                        birthday);
    }

    public static void createPlantProfileDocument( String collectionName, String profileId, String plantName, String birthday,
                                                   int minHumidity, int maxHumidity, int minTemperature,
                                                   int maxTemperature, int minSun,
                                                   int maxSun) {
       // String profileId =  UUID.randomUUID().toString();

        Document doc = new Document("user_id",getAppClientId())
                .append("profile_id", profileId)
                .append("name", plantName)
                        .append("birthday", birthday)
                .append(
                        "humidity min",
                        minHumidity)
                .append("humidity max",
                        maxHumidity)
                .append("temperature min",
                        minTemperature)
                .append("temperature max",
                        maxTemperature)
                .append("sunlight min", minSun)
                .append("sunlight max", maxSun);
               // .append("photo URL", photoURL);

        getUsers_collection(databaseName, collectionName).insertOne(doc);


    }


    public static class Once {
        private AtomicBoolean done = new AtomicBoolean();

        void run(Runnable task) {
            if (done.get()) return;
            if (done.compareAndSet(false, true)) {
                task.run();
            }
        }
    }
}
