package com.example.aiplant.user_profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.aiplant.R;
import com.example.aiplant.cameraandgallery.ImagePicker;
import com.example.aiplant.login.LoginActivity;
import com.example.aiplant.model.Plant;
import com.example.aiplant.model.User;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;


public class User_Profile extends AppCompatActivity implements View.OnClickListener, AccountSettingsFragment.OnFragmentInteractionListener {

    private static final String TAG = "User_Profile";
    private static final int ACTIVITY_NUM = 2, NUM_GRID_COLUMNS = 3, REQUEST_CODE = 11, REQUEST_CAMERA = 22, REQUEST_GALLERY = 33;

    // widgets
    private Button saveUsernameButton, saveProfile_piceButton;
    private TextView userNameTextView, emailHeader;
    private EditText usernameEditText;
    private CircularImageView profilePic;
    private GridView gridView;
    private androidx.appcompat.widget.Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private BottomNavigationView bottomNavigationViewEx;
    private View nav_header_view;
    private ProgressBar mProgressBar;
    //context and view
    private FragmentManager fragmentManager;
    private Context mContext;
    OnGridImageSelectedListener onGridImageSelectedListener;
    private InputMethodManager mInputManager;

    //database
    private MongoDbSetup mongoDbSetup;
    private GoogleSignInClient mGoogleSignInClient;
    private StitchAuth mStitchAuth;
    private StitchUser mStitchUser;
    private StitchAuthListener mStitchAuthListener;

    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ArrayList<Document> docs;
    private Document fetchedDoc;
    private Uri uri;
    private Bitmap bitmap;
    private User user;
    private SharedPreferences prefs;
    private boolean has_changed_profile_image;

    //Methods

    private Bitmap getBitmap() {
        return bitmap;
    }

    private void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private Uri getUri() {
        return uri;
    }

    private void setUri(Uri uri) {
        this.uri = uri;
    }

    private Document getFetchedDoc() {
        return fetchedDoc;
    }

    private void setFetchedDoc(Document fetchedDoc) {
        this.fetchedDoc = fetchedDoc;
    }
    //mongodb

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        mContext = getApplicationContext();
        fragmentManager = getSupportFragmentManager();
        mInputManager = (InputMethodManager) getSystemService((INPUT_METHOD_SERVICE));
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        has_changed_profile_image = prefs.getBoolean("prefs", false);

        initLayout();
        checkPermissions();
        setupBottomNavigationView();
        setupGridView();
        buttonListeners();
        connectDb();
        navigationViewClickListener();

    }

    private void navigationViewClickListener() {
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
                    Toast.makeText(mContext, "Delete account clicked", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> menuItem.setChecked(false), 500);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    deleteAccountData();
                    break;
            }
            return false;
        });
    }

    private void deleteAccount() {
        String id = mStitchUser.getId();
        mStitchAuth.removeUser().addOnCompleteListener(task -> {
            if (task.isSuccessful())
                mStitchAuth.removeUserWithId(id);
            mStitchAuth.logoutUserWithId(id);
            mongoDbSetup.goToWhereverWithFlags(mContext, mContext, LoginActivity.class);
        }).addOnFailureListener(e -> {
            Log.d(TAG, "deleteAccount: error: " + e.getCause());
        });
    }

    private void deleteAccountData() {
        String logged_user_id = mStitchUser.getId();
        RemoteMongoCollection user_coll = mongoDbSetup.getCollectionByName(getResources().getString(R.string.eye_plant_users));

        user_coll.deleteOne(eq("logged_user_id", logged_user_id)).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                user_coll.deleteOne(getFetchedDoc());
            deleteAccount();
            backToNormal();

        }).addOnFailureListener(e -> {
            Log.d(TAG, "then: Error: " + e.getCause());
        });
    }

    private void backToNormal() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("prefs", false);
        editor.apply();
    }

    private void handlePasswordReset() {
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
    }

    private void connectDb() {
        mongoDbSetup = MongoDbSetup.getInstance(getApplicationContext());
        Stitch.initialize(mContext);
        mongoDbSetup.runAppClientInit();
        mGoogleSignInClient = MongoDbSetup.getGoogleSignInClient();
        mStitchAuth = mongoDbSetup.getStitchAuth();
        mStitchUser = mStitchAuth.getUser();

        fetchUserData();
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

    public void initLayout() {
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
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_GALLERY | REQUEST_CAMERA);
        }
    }

    private void buttonListeners() {
        saveUsernameButton.setOnClickListener(this);
        profilePic.setOnClickListener(this);
        mNavigationView.setOnClickListener(this);
        saveProfile_piceButton.setOnClickListener(this);
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

    private void changedProfileImage() {
        if (!has_changed_profile_image) {//this is used to make Glide read from the entry edited_pic not picture to avoid errors. we set the boolean to true
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("prefs", true);
            editor.apply();
        }
    }

    private void hideEditText() {
        userNameTextView.setVisibility(View.INVISIBLE);
        usernameEditText.setVisibility(View.VISIBLE);
        saveUsernameButton.setVisibility(View.VISIBLE);
    }

    private void editUserPicture() {
        String logged_user_id = mStitchUser.getId();
        RemoteMongoCollection user_coll = mongoDbSetup.getCollectionByName(getResources().getString(R.string.eye_plant_users));
        byte[] pwr = mBitmapToArray(getBitmap());
        BsonBinary bsonBinary = new BsonBinary(pwr);
        Log.d(TAG, "editUserPicture: bson: " + pwr.length);
        Log.d(TAG, "editUserPicture: bson: " + bsonBinary.getData().length);

        user_coll.findOne(eq("logged_user_id", logged_user_id)).continueWithTask((Continuation<RemoteUpdateResult, Task<Document>>) task -> {
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

    }

    private void editUserNameData() {
        String logged_user_id = mStitchUser.getId();
        String new_user_name = usernameEditText.getText().toString();
        RemoteMongoCollection user_coll = mongoDbSetup.getCollectionByName(getResources().getString(R.string.eye_plant_users));
        user_coll.findOne(eq("logged_user_id", logged_user_id))
                .continueWithTask((Continuation<RemoteUpdateResult, Task<Document>>) task -> {
                    if (task.isSuccessful()) {
                        user_coll.updateOne(null, set("name", new_user_name), new RemoteUpdateOptions());
                    }
                    return null;
                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: Error: " + e.getCause()));
    }

    private void fetchUserData() {
        try {
            this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            String logged_user_id = mStitchAuth.getUser().getId();
            RemoteMongoCollection user_coll = mongoDbSetup.getCollectionByName(getResources().getString(R.string.eye_plant_users));
            mProgressBar.setVisibility(View.VISIBLE);
            user_coll.findOne(new Document("logged_user_id", logged_user_id)).continueWith(task -> {
                if (task.isSuccessful())
                    fetchedDoc = (Document) task.getResult();
                setFetchedDoc(fetchedDoc);
                setUpUserInfo(fetchedDoc);
                new Handler().postDelayed(() -> mProgressBar.setVisibility(View.INVISIBLE), Toast.LENGTH_SHORT);

                return null;


            }).addOnFailureListener(e -> Log.d(TAG, "onFailure: error: " + e.getCause()));

        } catch (Exception e) {

            Log.d(TAG, "fetchUserData: " + e.getCause());
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param userDoc document fetched from database
     * @throws NullPointerException if any of the elements is null.
     * @use sets data fetched from database for User Object
     **/
    private void setUpUserInfo(Document userDoc) {
        try {

            String id = userDoc.getString("logged_user_id");
            String name = userDoc.getString("name");
            String email = userDoc.getString("email");
            String photoURL = userDoc.getString("picture");
            int num_of_plants = userDoc.getInteger("number_of_plants");
            String birthday = userDoc.getString("birthday");
            Binary edited_pic = userDoc.get("edited_pic", Binary.class);// get binary

            //conversion
            byte[] data = edited_pic.getData(); // get data
            Bitmap b = arrayToBitmap(data); // convert to bitmap
            user = new User(id, name, email, photoURL, num_of_plants, birthday, data);// create a user

            userNameTextView.setText(name);
            emailHeader.setText(email);

            if (has_changed_profile_image) {
                Glide.with(mContext).load(b).fitCenter().into(profilePic);
            } else
                Glide.with(mContext).load(photoURL).fitCenter().into(profilePic);

        } catch (Exception e) {
            Log.d(TAG, "setUpUserInfo: Error: " + e.getCause());
        }
    }

    private void saveUsername() {

        hideEditText();
        String username = usernameEditText.getText().toString();
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Nothing here!");
        } else {
            Log.d(TAG, "Typed username: " + username);
            usernameEditText.setVisibility(View.INVISIBLE);
            saveUsernameButton.setVisibility(View.INVISIBLE);
            userNameTextView.setVisibility(View.VISIBLE);
            userNameTextView.setText(username);
            hideKeyboard();
            editUserNameData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Bitmap bitmap1 = null;

            try {
                bitmap1 = ImagePicker.getImageFromResult(this, resultCode, data);
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

    private void optionDialog() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(mContext);
        startActivityForResult(chooseImageIntent, REQUEST_CODE);
        checkPermissions();
    }

    private void signOut() {

        String providerType = mStitchUser.getLoggedInProviderName();
        Log.d(TAG, "signOut: providerType: " + providerType);
        if (providerType.contains("google")) {
            hideKeyboard();
            mGoogleSignInClient.signOut();
            mStitchAuth.logout();
            Log.d(TAG, "signOut: " + mStitchAuth.isLoggedIn());
            new Handler().postDelayed(() ->
                    mongoDbSetup.goToWhereverWithFlags(mContext, mContext, LoginActivity.class), 500);

        } else {
            hideKeyboard();
            String id = mStitchUser.getId();
            mStitchAuth.logoutUserWithId(id);
            mStitchAuth.logout();
            new Handler().postDelayed(() ->
                    mongoDbSetup.goToWhereverWithFlags(mContext, mContext, LoginActivity.class), 500);
        }
    }

    private void setupGridView() {
        Log.d(TAG, "setupGridView: Setting up GridView");

//        try {
        final ArrayList<Plant> posts = new ArrayList<>();


//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//            Query query = reference
//                    .child(getString(R.string.dbname_posts))
//                    .child(mAuth.getCurrentUser().getUid());
//
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//                        Plant post = new Plant();
//                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
//                        post.setmDescription(objectMap.get(getString(R.string.field_description)).toString());
//                        post.setmFoodImgUrl(objectMap.get(getString(R.string.field_food_photo)).toString());
//                        post.setUserId(objectMap.get(getString(R.string.field_user_id)).toString());
//                        post.setmRecipe(objectMap.get(getString(R.string.field_recipe)).toString());
//                        post.setmIngredients(objectMap.get(getString(R.string.field_ingredients)).toString());
//                        post.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
//                        post.setPostId(objectMap.get(getString(R.string.field_post_id)).toString());
//
//                        List<Like> likeList = new ArrayList<>();
//                        for (DataSnapshot ds : singleSnapshot
//                                .child(getString(R.string.field_likes)).getChildren()) {
//                            Like like = new Like();
//                            like.setUser_id(ds.getValue(Like.class).getUser_id());
//                            likeList.add(like);
//                        }
//                        post.setLikeList(likeList);
//                        posts.add(post);
//                    }

        //setup  our grid image
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;

        gridView.setColumnWidth(imageWidth);

        ArrayList<String> imgURLs = new ArrayList<>();
//                    for (int i = 0; i < posts.size(); i++) {
//                        imgURLs.add(posts.get(i).getmFoodImgUrl());
//                    }
        String testURL1 = "https://images.homedepot-static.com/productImages/a0592d4a-af16-41a7-969d-d96ee38bc57a/svn/dark-brown-sunnydaze-decor-plant-pots-dg-844-64_1000.jpg";
        String testURL2 = "https://homebnc.com/homeimg/2017/02/02-front-door-flower-pots-ideas-homebnc.jpg";
        String testURL3 = "https://i.pinimg.com/236x/5e/af/81/5eaf818de906fb0375e1049d0c7e69a5--pink-geranium-geranium-pots.jpg";
        String testURL4 = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTj_aHmQHzRu9pUQcNDO5X3T8h9X_fyyf8NcEmScFi12UcDrf6OvA&s";
        String testURL5 = "https://images-na.ssl-images-amazon.com/images/I/41La2UGe7vL.jpg";
        String testURL6 = "https://steemitimages.com/640x0/https://img.esteem.ws/cw9ugt462u.jpg";
        String testURL7 = "https://d2gr5kl7dt2z3t.cloudfront.net/blog/wp-content/uploads/2015/01/bonsai-514906_640-446x600.jpg";
        String testURL8 = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_0jkWxGA7fdAUzOkOutZPNoicW71m8BRGWuG-0pV0Uqt_ZdU6&s";
        imgURLs.add(testURL1);
        imgURLs.add(testURL2);
        imgURLs.add(testURL3);
        imgURLs.add(testURL4);
        imgURLs.add(testURL5);
        imgURLs.add(testURL6);
        imgURLs.add(testURL7);
        imgURLs.add(testURL8);
        GridImageAdapter adapter = new GridImageAdapter(this, R.layout.layout_grid_imageview,
                "", imgURLs);

        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        gridView.setOnItemClickListener((parent, view, position, id) ->
                onGridImageSelectedListener.onGridImageSelected(posts.get(position), ACTIVITY_NUM));

//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    Log.d(TAG, "onCancelled: Query Cancelled");
//
//                }
//            });
//        } catch (Exception e) {
//            Toast.makeText(getActivity(), "Error: Nothing to display", Toast.LENGTH_SHORT).show();
//
//            firebaseMethods.goToWhereverWithFlags(getActivity(), getActivity(), AddPostActivity.class);
//        }
    }

    /**
     * Bottom Navigation View setup
     */
    private void setupBottomNavigationView() {
        bottomNavigationViewEx = findViewById(R.id.bottomNavigationBar);
        BottomNavigationViewHelper.enableNavigation(getApplicationContext(), bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void hideKeyboard() {
        View v = getCurrentFocus().getRootView().findFocus();//.keyboardNavigationClusterSearch(getCurrentFocus(), Path.Direction.CW.);
        mInputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public interface OnGridImageSelectedListener {
        void onGridImageSelected(Plant plant, int activityNr);
    }
    //    public void updateOne() {
//        RemoteMongoCollection user_coll = mongoDbSetup.getUsers_collection();
//        Document seek = new Document("plant_id", "5dcd80de1e36d318eb99fe8c");
//        String new_user_name = usernameEditText.getText().toString();
//
//        user_coll.updateOne(eq("logged_user_id", client_id), set("name", new_user_name)).continueWith(new Continuation() {
//            @Override
//            public Object then(@NonNull Task task) throws Exception {
//                if (!task.isSuccessful()) {
//
//                } else
//                    Toast.makeText(mContext, "name updated", Toast.LENGTH_SHORT).show();
//                return null;
//            }
//        });
//
//    }
//    private void profileDialog() {
//        View dialogLayout = getLayoutInflater().inflate(R.layout.customized_alert_dialog, null);
//        ImageButton cameraButton = dialogLayout.findViewById(R.id.cameraButtonDialog);
//        ImageButton galleryButton = dialogLayout.findViewById(R.id.galleryButtonDialog);
//        ImageButton cancelButton = dialogLayout.findViewById(R.id.cancelButtonDialog);
//
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        dialogBuilder.setUpUserInfo(dialogLayout);
//        dialogBuilder.setTitle("Chose an action");
//
//        final AlertDialog alertDialog = dialogBuilder.create();
//        WindowManager.LayoutParams wlp = alertDialog.getWindow().getAttributes();
//
//        wlp.windowAnimations = R.anim.slide_down_anim;
//        wlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
//        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        alertDialog.getWindow().setAttributes(wlp);
//        alertDialog.setCanceledOnTouchOutside(true);
//        // Setting transparent the background (layout) of alert dialog
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        alertDialog.show();
//
//        cameraButton.setOnClickListener(v -> {
//            takePicture();
//            alertDialog.dismiss();
//        });
//
//        galleryButton.setOnClickListener(v -> {
//            selectPicture();
//            alertDialog.dismiss();
//        });
//
//        cancelButton.setOnClickListener(v -> {
//            alertDialog.dismiss();
//        });
//    }
//    private void topBarDialog() {
//        View dialogLayout = getLayoutInflater().inflate(R.layout.top_bar_dialog_layout, null);
//
//        TextView accountSettingsTextView = dialogLayout.findViewById(R.id.accountSettingsTextView);
//        TextView signOutTextView = dialogLayout.findViewById(R.id.signOutTextView);
//        TextView cancelTextView = dialogLayout.findViewById(R.id.cancelTextView);
//
//
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        dialogBuilder.setUpUserInfo(dialogLayout);
//
//        final AlertDialog alertDialog = dialogBuilder.create();
//        WindowManager.LayoutParams wlp = alertDialog.getWindow().getAttributes();
//
//        //wlp.windowAnimations = R.style.AlertDialogAnimation;
//        wlp.gravity =  Gravity.RIGHT|Gravity.TOP;
//        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        alertDialog.getWindow().setAttributes(wlp);
//        alertDialog.setCanceledOnTouchOutside(true);
//        // Setting transparent the background (layout) of alert dialog
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        alertDialog.show();
//
//
//        accountSettingsTextView.setOnClickListener(v -> {
//
////            Fragment AccountSettingFragment = fragmentManager.findFragmentById(R.id.useThisFragmentID);
//////
////                    if (AccountSettingFragment == null)
////                        AccountSettingFragment = new AccountSettingsFragment();
////                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////                        fragmentTransaction.addToBackStack(null);
////                        fragmentTransaction.add(R.id.useThisFragmentID, AccountSettingFragment).commit();
//            alertDialog.dismiss();
//        });
//
//        signOutTextView.setOnClickListener(v -> {
//            signOut();
//            alertDialog.dismiss();
//        });
//
//        cancelTextView.setOnClickListener(v -> {
//            Toast.makeText(mContext,"Cancel button clicked",Toast.LENGTH_SHORT).show();
//            alertDialog.dismiss();
//        });
//    }
//    private void openChoiceDialog() {
//
//        CharSequence[] options = {"Camera", "Gallery", "Cancel"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Select image source:");
//        builder.setCancelable(true);
//        builder.setIcon(R.mipmap.eye_logo);
//        builder.setItems(options, (dialog, which) -> {
//
//            if (options[which].equals("Camera")) {
//                takePicture();
//            } else if (options[which].equals("Gallery")) {
//                selectPicture();
//
//            } else if (options[which].equals("Cancel")) {
//                dialog.dismiss();
//            }
//        });
//
//        builder.create();
//        builder.show();
//
//    }
//    private void customizeddialog(String s1, String s2, String s3) {
//
//        CharSequence[] options = {s1, s2, s3};
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Profile menu:");
//        builder.setIcon(R.mipmap.eye_logo);
//        builder.setItems(options, (dialog, which) -> {
//
//            if (options[which].equals("Account settings")) {
//                //Todo: continue with fragment
//                Toast.makeText(mContext,"account settings pressed",Toast.LENGTH_SHORT).show();
//
//            } else if (options[which].equals("Sign out")) {
//                signOut();
//
//            } else if (options[which].equals("Cancel")) {
//                dialog.dismiss();
//            }
//        });
//        builder.create();
//        builder.show();
//
//        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.logo_eye_plant);
//        setBackgroundResource(R.drawable.logo_eye_plant);


//How to get a drawable
//        drawable1 = ContextCompat.getDrawable(mContext, R.drawable.african_violet);
//        drawable2 = ContextCompat.getDrawable(mContext, R.drawable.poisettia_indoors);
//        drawable3 = ContextCompat.getDrawable(mContext, R.drawable.begonia);
//        drawable4 = ContextCompat.getDrawable(mContext, R.drawable.bromeliads);
//}
//    Fragment fragmentForgotPass = fragmentManager.findFragmentById(R.id.useThisFragmentID);
//
//                    if (fragmentForgotPass == null) {
//                        fragmentForgotPass = new ForgotPassFragment();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.addToBackStack(null);
//                        fragmentTransaction.add(R.id.useThisFragmentID, fragmentForgotPass).commit();
//                    }
//    private void takePicture() {
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null) {
//            startActivityForResult(cameraIntent, REQUEST_CAMERA);
//        } else Toast.makeText(mContext, "Nothing selected..", Toast.LENGTH_SHORT).show();
//    }
//
//    private void selectPicture() {
//        Intent galleryIntent = new Intent();
//        galleryIntent.setType("image/*");
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_GALLERY);
//    }
//}
    //    private void fetchPlantsData() {
//        Document plantDOc = new Document("plant_id", "");
//
//        try {
//            RemoteMongoCollection plants_coll = mongoDbSetup.getPlants_Collection();
//            plants_coll.find(Document.class).iterator().continueWith(new Continuation() {
//                @Override
//                public Object then(@NonNull Task task) throws Exception {
//                    if (!task.isSuccessful()) {
//                        Log.d(TAG, "then: TaskErrror: " + task.getException());
//                    } else
//
//                        docs = new ArrayList<>();
//                    Document document = docs.get(0);
//
//                    Log.d(TAG, "then: document: " + document.getString("plant_id"));
//                    Log.d(TAG, "then: document: " + document.getString("plant_name"));
//                    Log.d(TAG, "then: document: " + document.getString("description"));
//
//                    return plants_coll.find().into(docs);
//                }
//            });
//
//            Document document1 = docs.get(0);
//            Log.d(TAG, "then: document: " + document1.getString("plant_id"));
//            Log.d(TAG, "then: document: " + document1.getString("plant_name"));
//            Log.d(TAG, "then: document: " + document1.getString("description"));
//        } catch (Exception e) {
//            Log.d(TAG, "fetchPlantsData: Error: " + e.getMessage());
//        }
//    }
    //    private void uploadImageToGridFs(Uri uri) {
//// Get the input stream
//        try {
//            InputStream streamToUploadFrom = new FileInputStream(uri.getPath());
//            // Create some custom options
//            GridFSUploadOptions options = new GridFSUploadOptions()
//                    .chunkSizeBytes(358400)
//                    .metadata(new Document(mStitchUser.getId(), streamToUploadFrom));
//
//            ObjectId fileId = users_profileBucket.uploadFromStream("users_profilePictures", streamToUploadFrom, options);
//        } catch (FileNotFoundException | NullPointerException e) {
//            // handle exception
//            Log.d(TAG, "uploadImageToGridFs: Error: " + e.getCause());
//        }
//    }
//private void readFromGridsFs() {
//    users_profileBucket.find().forEach(
//            new Block<GridFSFile>() {
//                public void apply(final GridFSFile gridFSFile) {
//                    Log.d(TAG, "apply: fileName: " + gridFSFile.getFilename());
//                }
//            });
//}
    //    private void uploadProifleImage(Uri uri) {
//        //
//        if (uri == null) {
//            Glide.with(this).load(user.getPicture()).centerInside().into(profilePic);
//        } else {
//
//            Glide.with(this).load(uri).centerInside().into(profilePic);
//            final ProgressDialog progressDialog = new ProgressDialog(this, "Uploading...", R.color.green_dark);
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.show();
//            FirebaseStorage storage = FirebaseStorage.getInstance();
//            StorageReference profile_pic_Ref = storage.getReference().child("/users_profiles/").child(mStitchUser.getId()).child("profile picture");
//
////            profilePicStorage = storage.getReference().child("/users_profiles/").child(mStitchUser.getId()).child("profile picture");
//
//            profile_pic_Ref.putFile(uri).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    profile_pic_Ref.getDownloadUrl().addOnCompleteListener(task1 -> {
//                        if (task1.isSuccessful()) {
//
//                            String uploadedImageURL = task1.getResult().toString();
//
//                            Log.d(TAG, "UploadProfilePic: URL= " + uploadedImageURL);
////                            editUserPicture(uploadedImageURL);
//                            progressDialog.dismiss();
//                            saveProfile_piceButton.setVisibility(View.INVISIBLE);
//                            fetchUserData();
//                        }
//                    }).addOnFailureListener(e -> {
//                        progressDialog.dismiss();
//                        Toast.makeText(this, "Failed, " + e.getCause(), Toast.LENGTH_SHORT).show();
//                        saveProfile_piceButton.setVisibility(View.INVISIBLE);
//                    });
//                }
//
//            }).addOnFailureListener(e -> {
//                progressDialog.dismiss();
//                Toast.makeText(this, "Upload failed, " + e.getCause(), Toast.LENGTH_SHORT).show();
//                saveProfile_piceButton.setVisibility(View.INVISIBLE);
//
//            }).addOnCanceledListener(() -> {
//                Toast.makeText(this, "Canceled by user, ", Toast.LENGTH_SHORT).show();
//                saveProfile_piceButton.setVisibility(View.INVISIBLE);
//
//            }).addOnProgressListener(taskSnapshot -> {
//                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
//                progressDialog.setTitle(" " + (int) progress + "%");
//
//            });
//        }
//    }

}