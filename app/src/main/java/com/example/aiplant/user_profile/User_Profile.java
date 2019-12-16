package com.example.aiplant.user_profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.aiplant.R;
import com.example.aiplant.cameraandgallery.ImagePicker;
import com.example.aiplant.cameraandgallery.PictureConversion;
import com.example.aiplant.home.HomeActivity;
import com.example.aiplant.login.LoginActivity;
import com.example.aiplant.model.PlantProfile;
import com.example.aiplant.model.User;
import com.example.aiplant.services.ScheduledFetch;
import com.example.aiplant.utility_classes.BottomNavigationViewHelper;
import com.example.aiplant.utility_classes.GridImageAdapter;
import com.example.aiplant.utility_classes.MongoDbSetup;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.auth.StitchAuth;
import com.mongodb.stitch.android.core.auth.StitchAuthListener;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.core.auth.providers.userpassword.UserPasswordAuthProviderClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.BsonBinary;
import org.bson.Document;
import org.bson.types.Binary;

import java.io.IOException;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

/**
 * @extends AppCompatActivity
 * @implements View.OnClickListener
 **/
public class User_Profile extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "User_Profile";
    private static final int ACTIVITY_NUM = 2, NUM_GRID_COLUMNS = 3, REQUEST_CODE = 11, REQUEST_CAMERA = 22, REQUEST_GALLERY = 33;
    //context and view
    private Button saveUsernameButton, saveProfile_piceButton;
    private TextView userNameTextView, emailHeader;
    private EditText usernameEditText;
    private CircularImageView profilePic;
    private GridView gridView;
    private androidx.appcompat.widget.Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private View nav_header_view;
    private ProgressBar mProgressBar;
    private Context mContext;
    private InputMethodManager mInputManager;
    //database
    private MongoDbSetup mongoDbSetup;
    private GoogleSignInClient mGoogleSignInClient;
    private StitchAuth mStitchAuth;
    private StitchUser mStitchUser;
    private StitchAuthListener mStitchAuthListener;
    //vars
    private Document fetchedDoc, document;
    private Bitmap bitmap;
    private User user;
    private SharedPreferences prefs;
    private boolean has_changed_profile_image;
    private PictureConversion pictureConverter;
    private Thread t2;
    private String logged_user_id;
    private ProgressDialog progressDialog;
    private ImagePicker imagePicker;

    //Methods
    private Bitmap getBitmap() {
        return bitmap;
    }

    private void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private Document getFetchedDoc() {
        return fetchedDoc;
    }

    private void setFetchedDoc(Document fetchedDoc) {
        this.fetchedDoc = fetchedDoc;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        mContext = getApplicationContext();
        mongoDbSetup = MongoDbSetup.getInstance(getApplicationContext());
        connectDb();
        initLayout();
        setup();
        setupBottomNavigationView();
        t2.run();
        buttonListeners();
        navigationViewClickListener();
    }

    /**
     * @use setup context and other variables initialization
     **/
    private void setup() {
        try {
            mInputManager = (InputMethodManager) getSystemService((INPUT_METHOD_SERVICE));
            prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            has_changed_profile_image = prefs.getBoolean("prefs", false);
            pictureConverter = new PictureConversion();
            imagePicker = new ImagePicker();
            t2 = new Thread() {
                @Override
                public void run() {
                    fetchUserData();
                    fetchUPlants();
                }
            };
        }catch (Exception e){
            Log.d(TAG, "setup: "+e.getLocalizedMessage());
        }
    }

    /**
     * @use finds every view of the layout by its id
     **/
    private void initLayout() {
        //topLayout widgets
        mDrawerLayout = findViewById(R.id.drawer_layout);
        usernameEditText = findViewById(R.id.username_editText);
        userNameTextView = findViewById(R.id.userNameTextView);
        saveUsernameButton = findViewById(R.id.saveUserNameButton);
        profilePic = findViewById(R.id.profilePicture);
        saveProfile_piceButton = findViewById(R.id.saveProfilePictureButton);
        mProgressBar = findViewById(R.id.prgressBar);

        //gridView
        gridView = findViewById(R.id.grid_view_user_profile);
        //nav_view
        mNavigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolBar);
        //heaDER_LAYOUT
        nav_header_view = mNavigationView.getHeaderView(0);
        emailHeader = nav_header_view.findViewById(R.id.textView_emailDisplay_header);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.settings);
        toggle.setHomeAsUpIndicator(drawable);
        DrawerArrowDrawable arrowDrawable = new DrawerArrowDrawable(mContext);
        arrowDrawable.setColor(getResources().getColor(R.color.red));
        arrowDrawable.setSpinEnabled(true);
        arrowDrawable.mutate();
        toggle.setDrawerArrowDrawable(arrowDrawable);
        toggle.syncState();
    }

    /**
     * @use listeners for buttons
     **/
    private void buttonListeners() {
        saveUsernameButton.setOnClickListener(this);
        profilePic.setOnClickListener(this);
        mNavigationView.setOnClickListener(this);
        saveProfile_piceButton.setOnClickListener(this);
    }

    /**
     * @use connecting to database by getting an instance of MongoDbSetup class
     **/
    private void connectDb() {
        try {
            mGoogleSignInClient = mongoDbSetup.getGoogleSignInClient();
            mStitchAuth = mongoDbSetup.getStitchAuth();
            mStitchUser = mStitchAuth.getUser();
            logged_user_id = mongoDbSetup.getStitchUser().getId();
        }catch (Exception e){
            Log.d(TAG, "connectDb: "+e.getLocalizedMessage());
        }
    }

    /**
     * @use deleting user data from the auth database
     **/
    private void deleteAccountData() {
        try {
            String logged_user_id = mStitchUser.getId();
            RemoteMongoCollection user_coll = mongoDbSetup.getCollectionByName(getResources().getString(R.string.eye_plant_users));
            if (mongoDbSetup.checkInternetConnection(mContext)) {
                user_coll.findOne(new Document("logged_user_id", logged_user_id)).continueWith(task -> {
                    if (task.isSuccessful()) {
                        backToNormal();
                        progressDialog.dismiss();
                        mStitchAuth.removeUserWithId(logged_user_id);
                        mStitchAuth.logoutUserWithId(logged_user_id);
                        mongoDbSetup.goToWhereverWithFlags(mContext, mContext, LoginActivity.class);
                        return user_coll.deleteOne((Document) task.getResult());
                    } else
                        backToNormal();
                    progressDialog.dismiss();
                    mStitchAuth.removeUserWithId(logged_user_id);
                    mStitchAuth.logoutUserWithId(logged_user_id);
                    mongoDbSetup.goToWhereverWithFlags(mContext, mContext, LoginActivity.class);
                    return user_coll;
                }).addOnFailureListener(e -> {
                    Log.d(TAG, "then: Error: " + e.getLocalizedMessage());
                });
            } else
                Toast.makeText(mContext, getString(R.string.check_internet_connection_display_profile), Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Log.d(TAG, "deleteAccountData: "+e.getLocalizedMessage());
        }
    }

    private void deleteUserPlants() {
        try {
            if (mongoDbSetup.checkInternetConnection(mContext)) {
                stopService(getIntent().setClass(mContext, ScheduledFetch.class));//        getSystemService(ScheduledFetch.class).onDestroy();
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Removing User");
                progressDialog.setMessage("Wiping all user data in process, please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setIcon(R.drawable.ai_plant);
                progressDialog.show();

                String user_id = mStitchUser.getId();
                RemoteMongoCollection user_Plants_coll = mongoDbSetup.getCollectionByName(getResources().getString(R.string.eye_plant_plant_profiles));
                user_Plants_coll.findOne(eq("user_id", user_id)).continueWith(task -> {
                    if (task.isSuccessful()) { //if we find a document
                        deleteAccountData();
                        return user_Plants_coll.deleteOne((Document) task.getResult());
                    } else //if no such document we just return the collection itself.
                        deleteAccountData(); //we still need to call the method so we delete the user's data from users_collection
                    return user_Plants_coll;

                }).addOnFailureListener(e -> {
                    Log.d(TAG, "then: Error: " + e.getLocalizedMessage());
                });
            } else
                Toast.makeText(mContext, getString(R.string.check_internet_connection_display_profile), Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Log.d(TAG, "deleteUserPlants: "+e.getLocalizedMessage());
        }
    }

    private void backToNormal() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("prefs", false);
        editor.apply();
    }

    /**
     * @use handles the reset password request on database
     **/
    private void handlePasswordReset() {
        if (mongoDbSetup.checkInternetConnection(mContext)) {
            String providerName = mStitchUser.getLoggedInProviderName();
            if (!providerName.contains("google")) {
                String mail = getFetchedDoc().getString("email");
                Log.d(TAG, "handlePasswordReset: email: " + mail);
                UserPasswordAuthProviderClient emailPassClient = Stitch.getDefaultAppClient().getAuth().getProviderClient(UserPasswordAuthProviderClient.factory);

                emailPassClient.sendResetPasswordEmail(mail).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "handlePasswordReset: task is successful: " + task.isSuccessful());
                                Toast.makeText(mContext, getResources().getString(R.string.sent_reset_email), Toast.LENGTH_SHORT).show();
                            }
                        }
                ).addOnFailureListener(e ->
                        Log.d(TAG, "onFailure: Error: " + e.getCause()));
            }
        }else Toast.makeText(mContext, getString(R.string.check_internet_connection_display_profile), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        //handling for the drawerLayout so we exit it instead of exiting the activity
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();//exits if back pressed
    }

    /**
     * @use sets the shared pref to true so we can read from the right field
     * on database that contains the binary value of the user's bitmap
     **/
    private void changedProfileImage() {
        if (mongoDbSetup.checkInternetConnection(mContext)) {
            if (!has_changed_profile_image) {//this is used to make Glide read from the entry edited_pic not picture to avoid errors. we set the boolean to true
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("prefs", true);
                editor.apply();
            }
        }else Toast.makeText(mContext, getString(R.string.check_internet_connection_display_profile), Toast.LENGTH_LONG).show();
    }

    /**
     * @use hides the editText and replaces it with textView
     **/
    private void hideEditText() {
        userNameTextView.setVisibility(View.INVISIBLE);
        usernameEditText.setVisibility(View.VISIBLE);
        saveUsernameButton.setVisibility(View.VISIBLE);
    }

    /**
     * @use updates the users picture on database and sets the image view
     **/
    private void editUserPicture() {
        if (mongoDbSetup.checkInternetConnection(mContext)) {
            String logged_user_id = mStitchUser.getId();
            RemoteMongoCollection user_coll = mongoDbSetup.getCollectionByName(getResources().getString(R.string.eye_plant_users));
            byte[] pwr = pictureConverter.bitmapToByteArray(getBitmap());
            BsonBinary bsonBinary = new BsonBinary(pwr);
            Log.d(TAG, "editUserPicture: bson: " + pwr.length);
            Log.d(TAG, "editUserPicture: bson: " + bsonBinary.getData().length);

            user_coll.findOne(eq("logged_user_id", logged_user_id))
                    .continueWithTask((Continuation<RemoteUpdateResult, Task<Document>>) task -> {
                        if (task.isSuccessful()) {
                            user_coll.updateOne(null, set("edited_pic", bsonBinary), new RemoteUpdateOptions());
                            changedProfileImage();
                            saveProfile_piceButton.setVisibility(View.INVISIBLE);
                        } else
                            Log.d(TAG, "then: Error: " + task.getException());
                        return null;
                    }).addOnFailureListener(e -> {
                saveProfile_piceButton.setVisibility(View.INVISIBLE);
                Log.d(TAG, "then: Error: " + e.getCause());
            });
        }else Toast.makeText(mContext, getString(R.string.check_internet_connection_display_profile), Toast.LENGTH_LONG).show();

    }

    /**
     * @use updates username extracted from the editText field on database
     **/
    private void editUserNameData() {
        if (mongoDbSetup.checkInternetConnection(mContext)) {
        String logged_user_id = mStitchUser.getId();
        String new_user_name = usernameEditText.getText().toString();
        RemoteMongoCollection user_coll = mongoDbSetup.getCollectionByName(getResources().getString(R.string.eye_plant_users));
        user_coll.findOne(eq("logged_user_id", logged_user_id))
                .continueWithTask((Continuation<RemoteUpdateResult, Task<Document>>) task -> { //<-looping through database to find the requested data->
                    if (task.isSuccessful()) {
                        user_coll.updateOne(null, set("name", new_user_name), new RemoteUpdateOptions()); // <-Updating the data in case its found->
                        userNameTextView.setText(new_user_name);
                    }
                    return null;
                }).addOnFailureListener(e ->
                Log.d(TAG, "onFailure: Error: " + e.getCause()));//<-Catching the exception in case of error->
        }else Toast.makeText(mContext, getString(R.string.check_internet_connection_display_profile), Toast.LENGTH_LONG).show();
    }

    /**
     * @use fetch user data according to the user_id from database
     **/
    private void fetchUserData() {

        try {
            if (mongoDbSetup.checkInternetConnection(mContext)) {
                this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                String logged_user_id = mStitchAuth.getUser().getId();
                RemoteMongoCollection user_coll = mongoDbSetup.getCollectionByName(getResources().getString(R.string.eye_plant_users));
                mProgressBar.setVisibility(View.VISIBLE);
                user_coll.findOne(new Document("logged_user_id", logged_user_id)).continueWith(task -> {
                    if (task.isSuccessful())
                        fetchedDoc = (Document) task.getResult();
                    setFetchedDoc(fetchedDoc);
                    setUpUserInfo(fetchedDoc);
                    new Handler().postDelayed(() -> {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        userNameTextView.setVisibility(View.VISIBLE);
                    }, Toast.LENGTH_SHORT);

                    return null;


                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: error: " + e.getCause()));
            }else Toast.makeText(mContext, getString(R.string.check_internet_connection_display_profile), Toast.LENGTH_LONG).show();
        } catch (Exception e) {

            Log.d(TAG, "fetchUserData: " + e.getCause());
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @use fetch user's plants data according to the user_id from database
     **/
    private void fetchUPlants() {
        try {
            if (mongoDbSetup.checkInternetConnection(mContext)) {
            String logged_user_id = mStitchAuth.getUser().getId();
            RemoteMongoCollection user_coll = mongoDbSetup.getCollectionByName(getResources().getString(R.string.eye_plant_plant_profiles));
            user_coll.findOne(new Document("user_id", logged_user_id)).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    document = (Document) task.getResult();
                    setupGridView(document);
                }
            }).addOnFailureListener(e ->
                    Log.d(TAG, "fetchPlantData error: " + e.getCause()));
            }else Toast.makeText(mContext, getString(R.string.check_internet_connection_display_profile), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d(TAG, "fetchPlantData error: " + e.getCause());
        }
    }

    /**
     * @param userDoc document fetched from database
     * @use sets data extracted from the document for User Object
     **/
    private void setUpUserInfo(Document userDoc) {
        try {
            if (mongoDbSetup.checkInternetConnection(mContext)) {
            String id = userDoc.getString("logged_user_id");
            String name = userDoc.getString("name");
            String email = userDoc.getString("email");
            String photoURL = userDoc.getString("picture");
            int num_of_plants = userDoc.getInteger("number_of_plants");
            String birthday = userDoc.getString("birthday");
            Binary edited_pic = userDoc.get("edited_pic", Binary.class);// get binary

            //conversion
            byte[] data = edited_pic.getData(); // get data
            Bitmap b = pictureConverter.byteArrayToBitmap(data); // convert to bitmap
            user = new User(id, name, email, photoURL, num_of_plants, birthday, data);// create a user

            userNameTextView.setText(name);
            emailHeader.setText(email);

            if (has_changed_profile_image) {
                Glide.with(mContext).load(b).fitCenter().into(profilePic);
            } else
                Glide.with(mContext).load(photoURL).fitCenter().into(profilePic);
            }else Toast.makeText(mContext, getString(R.string.check_internet_connection_display_profile), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d(TAG, "setUpUserInfo: Error: " + e.getCause());
        }
    }

    /**
     * @use save username extracted from the editText field
     **/
    private void saveUsername() {
        if (mongoDbSetup.checkInternetConnection(mContext)) {
        String username = userNameTextView.getText().toString();
        usernameEditText.setText(username);
        hideEditText();
        saveUsernameButton.setOnClickListener(view -> {
            if (TextUtils.isEmpty(username)) {
                usernameEditText.setError("Nothing here!");
            } else {
                String newUserName = usernameEditText.getText().toString();
                userNameTextView.setText(newUserName);
                Log.d(TAG, "Typed username: " + username);
                usernameEditText.setVisibility(View.INVISIBLE);
                saveUsernameButton.setVisibility(View.INVISIBLE);
                userNameTextView.setVisibility(View.VISIBLE);
                userNameTextView.setText(username);
                hideKeyboard();
                editUserNameData();// update the username
            }
        });
        }
        else Toast.makeText(mContext, getString(R.string.check_internet_connection_display_profile), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Bitmap bitmap1 = null;

            try {
                bitmap1 = imagePicker.getImageFromResult(this, resultCode, data);
                setBitmap(bitmap1);
                Glide.with(mContext).load(bitmap1).fitCenter().into(profilePic);
                Log.d(TAG, "onActivityResult: error getting bitmap: " + bitmap.getDensity());
                saveProfile_piceButton.setVisibility(View.VISIBLE);
                profilePic.refreshDrawableState();

            } catch (Exception e) {
                Log.e(TAG, "onActivityResult Error: " + e.getMessage());
            }
        } else Toast.makeText(mContext, "canceled", Toast.LENGTH_SHORT).show();
    }

    /**
     * @use opens a choice dialog for the user to chose from gallery or camera
     **/
    private void optionDialog() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(mContext);
        startActivityForResult(chooseImageIntent, REQUEST_CODE);
    }

    /**
     * @use adding an authLister to our user so we avoid the listener timeout
     **/
    private void authListenerData() {
        try {
            mStitchAuthListener = new StitchAuthListener() {

                @Override
                public void onListenerRegistered(StitchAuth auth) {
                    Log.d(TAG, "onListenerRegistered: ");
                }

                @Override
                public void onUserLoggedIn(StitchAuth auth, StitchUser loggedInUser) {
                    Log.d(TAG, "onUserLoggedIn: ");
                    mStitchAuth.addAuthListener(this);
                }

                @Override
                public void onUserRemoved(StitchAuth auth, StitchUser removedUser) {
                    mStitchAuth.removeAuthListener(this);
                    try {
                        auth.close();
                    } catch (IOException e) {
                        Log.d(TAG, "onUserRemoved: error: " + e.getCause());
                    }

                }

                @Override
                public void onUserAdded(StitchAuth auth, StitchUser addedUser) {
                    mStitchAuth.addAuthListener(this);
                }
            };
        }catch (Exception e){
            Log.d(TAG, "authListenerData: "+e.getLocalizedMessage());
        }

    }

    /**
     * @use disconnects the user from StitchAuth and redirects him to Login activity
     **/
    private void signOut() {
        try {
            if (mongoDbSetup.checkInternetConnection(mContext)) {
                stopService(getIntent().setClass(mContext, ScheduledFetch.class));//        getSystemService(ScheduledFetch.class).onDestroy();
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Signing out");
                progressDialog.setMessage("Closing the session, please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setIcon(R.drawable.ai_plant);
                progressDialog.show();
                authListenerData();

                String providerType = mStitchUser.getLoggedInProviderName();
                Log.d(TAG, "signOut: providerType: " + providerType);
                if (providerType.contains("google")) {
                    Log.d(TAG, "signOut: " + mStitchAuth.isLoggedIn());
                    hideKeyboard();
                    logOutGoogle();
                    progressDialog.dismiss();
                    new Handler().postDelayed(() ->
                            mongoDbSetup.goToWhereverWithFlags(mContext, mContext, LoginActivity.class), 500);

                } else {
                    hideKeyboard();
                    String id = mStitchUser.getId();
                    logoutEMailUser();
                    new Handler().postDelayed(() -> {
                        progressDialog.dismiss();
                        mongoDbSetup.goToWhereverWithFlags(mContext, mContext, LoginActivity.class);
                    }, 1000);
                }
            } else
                Toast.makeText(mContext, getString(R.string.check_internet_connection_display_profile), Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Log.d(TAG, "signOut: "+e.getLocalizedMessage());
        }
    }

    private void logOutGoogle() {
        try {
            if (mongoDbSetup.checkInternetConnection(mContext)) {
                String id = mStitchUser.getId();
                mStitchAuth.logoutUserWithId(id).continueWithTask(((Continuation) task -> {
                    if (task.isSuccessful()) {
                        return mStitchAuth.logoutUserWithId(id);
                    } else return mStitchAuth.logout();
                })).addOnFailureListener(e -> {
                    Log.d(TAG, "setupGridView: error; " + e.getMessage());//<-catch exception to prevent app crush -->
                });
            } else
                Toast.makeText(mContext, getString(R.string.check_internet_connection_display_profile), Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Log.d(TAG, "logOutGoogle: "+e.getLocalizedMessage());
        }
    }

    private void logoutEMailUser() {
        try {
            String id = mStitchUser.getId();
            mStitchAuth.logoutUserWithId(id).continueWithTask(((Continuation) task -> {
                if (task.isSuccessful()) {
                    return mStitchAuth.logoutUserWithId(id);
                } else return mStitchAuth.logout();
            })).addOnFailureListener(e -> {
                Log.d(TAG, "setupGridView: error; " + e.getMessage());//<-catch exception to prevent app crush -->
            });
        }catch (Exception e){
            Log.d(TAG, "logoutEMailUser: error: "+e.getLocalizedMessage());
        }
    }

    /**
     * @param document<>< fetched doc from the database   />
     *                    After fetcheing the document, we use it to create an object of a Plant
     *                    Then we populate the ArrayList() with the bitmap.
     **/
    private void setupGridView(Document document) {
        try {
            Log.d(TAG, "setupGridView: Setting up GridView");
            if (mongoDbSetup.checkInternetConnection(mContext)) {
                try {
                    String user_id = document.getString("user_id");//<-extracting data from the extracted document-->
                    String plant_id = document.getString("profile_id");
                    String plant_name = document.getString("name");
                    String birthday = document.getString("birthday");
                    ArrayList<Integer> humidity = document.get("humidity", ArrayList.class);
                    ArrayList<Integer> temperature = document.get("temperature", ArrayList.class);
                    ArrayList<Integer> sunlight = document.get("sunlight", ArrayList.class);
                    int hum_min = humidity.get(0), hum_max = humidity.get(1);
                    int tem_min = temperature.get(0), tem_max = temperature.get(1);
                    int light_min = sunlight.get(0), light_max = sunlight.get(1);
                    int measured_humidity = document.get("measured_humidity", Integer.class);
                    int measured_temperature = document.get("measured_temperature", Integer.class);
                    int measured_sunlight = document.get("measured_sunlight", Integer.class);
                    Binary pic = document.get("edited_pic", Binary.class);
                    byte[] data = pic.getData(); // get data
                    Bitmap b = pictureConverter.byteArrayToBitmap(data);//<-converting the data converted previously to bitmap -->
                    PlantProfile p_Profile = new PlantProfile(user_id, plant_id, plant_name, birthday, hum_min, hum_max, tem_min, tem_max,//<-ecreate user object-->
                            light_min, light_max, data, measured_humidity, measured_temperature, measured_sunlight);

                    final ArrayList<PlantProfile> plantProfiles = new ArrayList<>();//<-setup grid view -->
                    ArrayList<Bitmap> bitmaps = new ArrayList<>();
                    plantProfiles.add(p_Profile);
                    bitmaps.add(b);
                    int gridWidth = getResources().getDisplayMetrics().widthPixels;
                    int imageWidth = gridWidth / NUM_GRID_COLUMNS;
                    gridView.setColumnWidth(imageWidth);
                    GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, bitmaps);
                    gridView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    gridView.setOnItemClickListener((parent, view, position, id) -> {//<-clicklistener on position in gridView-->
                        mongoDbSetup.goToWhereverWithFlags(mContext, mContext, HomeActivity.class);
                    });
                } catch (Exception e) {
                    Log.d(TAG, "setupGridView: error; " + e.getMessage());//<-catch exception to prevent app crush -->
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();//<-Notify user to check internet-->
            }
        }catch (Exception e){
            Log.d(TAG, "setupGridView: "+e.getLocalizedMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.saveUserNameButton:
                saveUsername();
                break;

            case R.id.profilePicture:
                optionDialog();
                break;

            case R.id.saveProfilePictureButton:
                editUserPicture();
                break;
        }
    }

    /**
     * Bottom Navigation View setup
     */
    public void setupBottomNavigationView() {
        try {
            BottomNavigationViewHelper bnh = new BottomNavigationViewHelper();
            BottomNavigationView bottomNavigationViewEx = findViewById(R.id.bottomNavigationBar);
            BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
            bnh.enableNavigation(mContext, bottomNavigationViewEx);
            Menu menu = bottomNavigationViewEx.getMenu();
            MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
            menuItem.setChecked(true);
        }catch (Exception e){
            Log.d(TAG, "setupBottomNavigationView: "+e.getLocalizedMessage());
        }
    }

    /**
     * @use hides the keyboard for a specified view
     * in our case for the whole activity, since we get focus of the parent layout
     **/
    private void hideKeyboard() {
        try {
            View v = mDrawerLayout.findFocus();//its better
            mInputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }catch (Exception e){
            Log.d(TAG, "hideKeyboard: "+e.getLocalizedMessage());
        }
    }

    /**
     * @use navigation view clickListener
     **/
    private void navigationViewClickListener() {
        try {
            mNavigationView.setNavigationItemSelectedListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.chengeUserNameItem:
                        menuItem.setChecked(true);
                        new Handler().postDelayed(() -> menuItem.setChecked(false), 500);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        saveUsername();
                        break;

                    case R.id.changeProfPictureItem:
                        menuItem.setChecked(true);
                        new Handler().postDelayed(() -> menuItem.setChecked(false), 500);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        optionDialog();
                        break;

                    case R.id.signOutItem:
                        menuItem.setChecked(true);
                        new Handler().postDelayed(() -> menuItem.setChecked(false), 500);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        signOut();
                        break;

                    case R.id.resetPassItem:
                        menuItem.setChecked(true);
                        new Handler().postDelayed(() -> menuItem.setChecked(false), 500);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        handlePasswordReset();
                        break;

                    case R.id.delete_accountItem:
                        menuItem.setChecked(true);
                        new Handler().postDelayed(() -> menuItem.setChecked(false), 500);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        deleteUserPlants();//todo make 3 threads and make them run

                        break;
                }
                return false;
            });
        }catch (Exception e){
            Log.d(TAG, "navigationViewClickListener: "+e.getLocalizedMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mongoDbSetup.checkInternetConnection(mContext)) {
            Toast.makeText(getApplicationContext(), getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mongoDbSetup.checkInternetConnection(mContext)) {
            Toast.makeText(getApplicationContext(), getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }
}