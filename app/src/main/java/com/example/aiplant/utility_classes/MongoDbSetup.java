package com.example.aiplant.utility_classes;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiplant.R;
import com.example.aiplant.home.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * File created by tcarau18
 **/
public class MongoDbSetup {

    private static final String TAG = "MongoDbSetup";
    private Context mContext;

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
//    private LoginManager mLoginManager = LoginManager.getInstance();

    private static StitchAppClient appClient ;
//    private static final StitchAppClient appClient =
//            Stitch.initializeDefaultAppClient("eye-plant-tilrj");
//
//    private static final RemoteMongoClient remoteMongoDbClient =
//            appClient.getServiceClient(RemoteMongoClient.factory, String.valueOf(R.string.service_name));
//
//    private static final RemoteMongoCollection<Document> plants_collection =
//            remoteMongoDbClient.getDatabase(String.valueOf(R.string.eye_plant))
//                    .getCollection(String.valueOf(R.string.eye_plant_plants));
//
//    private static final RemoteMongoCollection<Document> users_collection =
//            remoteMongoDbClient.getDatabase(String.valueOf(R.string.eye_plant))
//                    .getCollection(String.valueOf(R.string.eye_plant_users));

    // Configure Google Sign In
    private GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("556950483367-9ekr8qotdiv7md2r1tckudh09damgof0.apps.googleusercontent.com") //token taken from firebase authentication data
            .requestServerAuthCode("556950483367-9ekr8qotdiv7md2r1tckudh09damgof0.apps.googleusercontent.com") //auth code from firebase authentication data
            .requestEmail()
            .build();

    private static GoogleSignInClient mGoogleSignInClient;

//    public LoginManager getLoginManager() {
//        return mLoginManager;
//    }

    private MongoDbSetup(Context context
//            ,StitchAppClient appClient
    ) {
        // Mo.Msaad modification modification
        synchronized (MongoDbSetup.class) {
            mContext = context;
            mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        }
        appClient =
                Stitch.initializeDefaultAppClient("eye-plant-tilrj");
    }

    public static GoogleSignInClient getClient() {
        return mGoogleSignInClient;
    }

    public static MongoDbSetup getInstance(Context context
//            , StitchAppClient appClient
    ) {

        return new MongoDbSetup(context
//                ,appClient
        );
    }


    //--------------------------------------------------------METHODS---------------------------------------------------------------------------------------------------
//    public void updateUsername(String userUID, String username, String dispalyName, String website, String about, long phone, String profile_url) {
//        Log.d(TAG, "updateUsername: updating username to:" + username);
//        myRef.child(mContext.getString(R.string.dbname_users))
//                .child(userUID)
//                .child(mContext.getString(R.string.field_username))
//                .setValue(username);
//
//        myRef.child(mContext.getString(R.string.dbname_users))
//                .child(userUID)
//                .child(mContext.getString(R.string.field_display_name))
//                .setValue(dispalyName);
//
//        myRef.child(mContext.getString(R.string.dbname_users))
//                .child(userUID)
//                .child("website")
//                .setValue(website);
//
//        myRef.child(mContext.getString(R.string.dbname_users))
//                .child(userUID)
//                .child("about")
//                .setValue(about);
//
//        myRef.child(mContext.getString(R.string.dbname_users))
//                .child(userUID)
//                .child("phone_number")
//                .setValue(phone);
//
//        myRef.child(mContext.getString(R.string.dbname_users))
//                .child(userUID)
//                .child("profile_photo")
//                .setValue(profile_url);
//    }

    /**
     * Retrieves the account settings for the User currently logged in
     * Database:user_account_settings node
     * <p>
     * //     * @param dataSnapshot represent the data from database
     *
     * @return the User Account Settings
     */
//    public User getUserSettings(DataSnapshot dataSnapshot) {
//        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from database");
//
//        User user = new User();
//        String userID = mAuth.getCurrentUser().getUid();
//
//        for (DataSnapshot ds : dataSnapshot.getChildren()) {
//
//            //User Account Settings Node
//            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
//                Log.d(TAG, "getUserAccountSettings: dataSnapshot" + ds);
//                try {
//                    user.setUsername(
//                            ds.child(userID)
//                                    .getValue(User.class)
//                                    .getUsername()
//                    );
//
//                    user.setDisplay_name(
//                            ds.child(userID)
//                                    .getValue(User.class)
//                                    .getDisplay_name()
//                    );
//
//                    user.setAbout(
//                            ds.child(userID)
//                                    .getValue(User.class)
//                                    .getAbout()
//                    );
//
//                    user.setWebsite(
//                            ds.child(userID)
//                                    .getValue(User.class)
//                                    .getWebsite()
//                    );
//
//                    user.setFollowers(
//                            ds.child(userID)
//                                    .getValue(User.class)
//                                    .getFollowers()
//                    );
//
//                    user.setFollowing(
//                            ds.child(userID)
//                                    .getValue(User.class)
//                                    .getFollowing()
//                    );
//
//                    user.setNrPosts(
//                            ds.child(userID)
//                                    .getValue(User.class)
//                                    .getNrOfPosts()
//                    );
//
//                    user.setProfile_photo(
//                            ds.child(userID)
//                                    .getValue(User.class)
//                                    .getProfile_photo()
//                    );
//
//                    user.setEmail(
//                            ds.child(userID)
//                                    .getValue(User.class)
//                                    .getEmail()
//                    );
//
//                    user.setPhone_number(
//                            ds.child(userID)
//                                    .getValue(User.class)
//                                    .getPhone_number()
//                    );
//
//                    Log.d(TAG, "getUserAccountSettings: retrieve user account settings information: " + user.toString());
//                } catch (NullPointerException e) {
//                    Log.d(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage());
//                }
//            }
//        }
//
//        return user;
//    }
    public String getTimestamp() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd__HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Copenhagen"));
        return sdf.format(new Date());
    }

//    public void checkUserStateIfNull(Context context, FirebaseAuth auth) {
//
//        Log.d(TAG, "checkUserStateIfNull: is called");
//        if (auth == null || auth.getCurrentUser() == null) {
//            mLoginManager.logOut();
//            auth.signOut();
//        }
//    }

    public static FirebaseAuth getAuth() {
        return mAuth;
    }

    public void checkAuth(Context context, FirebaseAuth auth) {

        if (mAuth == null || mAuth.getCurrentUser() == null) {
            auth.signOut();
//            mLoginManager.logOut();
        } else if (auth != null || mAuth.getCurrentUser() != null) {
            context.startActivity(new Intent(context, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }


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

    public static RemoteMongoCollection<Document> getUsers_collection() {
        return
                getRemoteMongoDbClient().getDatabase("eye_plant")
                        .getCollection("users");
    }

    public void goToWhereverWithFlags(Context activityContext, Context c, Class<? extends AppCompatActivity> cl) {

        activityContext.startActivity(new Intent(c, cl).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }


    public void goToWhereverWithOutFlags(Context activityContext, Context c, Class<? extends AppCompatActivity> cl) {
        activityContext.startActivity(new Intent(c, cl));
    }


}
