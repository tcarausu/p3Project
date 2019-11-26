package com.example.aiplant.user_profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.example.aiplant.utility_classes.BottomNavigationViewHelper;
import com.example.aiplant.utility_classes.GridImageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.util.ArrayList;

public class User_Profile extends AppCompatActivity implements View.OnClickListener, AccountSettingsFragment.OnFragmentInteractionListener {

    private static final String TAG = "User_Profile";
    private static final int ACTIVITY_NUM = 2;
    private static final int NUM_GRID_COLUMNS = 3;
    private static final int REQUEST_CAMERA = 22;
    private static final int REQUEST_GALLERY = 33;
    private static final int REQUEST_CODE = 11;

    // widgets
    private ImageButton editUsernameButton, saveUsernameButton;
    private TextView userNameTextView;
    private EditText usernameEditText; //usernameEditText
    private GridView gridView;
    private CircularImageView profilePic;
    private androidx.appcompat.widget.Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private BottomNavigationView bottomNavigationViewEx;
    //context and others
    private FragmentManager fragmentManager;
    private Context mContext;
    OnGridImageSelectedListener onGridImageSelectedListener;
    private InputMethodManager mInputManager;

    //vars
    private String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //variables
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        mContext = getApplicationContext();
        fragmentManager = getSupportFragmentManager();
        mInputManager = (InputMethodManager) getSystemService((INPUT_METHOD_SERVICE));
        initLayout();
        checkPermissions();
        buttonListeners();
        setupBottomNavigationView();
        setupGridView();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.chengeUserNameItem:
                    menuItem.setChecked(true);
                    Toast.makeText(mContext, "change username clicked", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> menuItem.setChecked(false), 500);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    //todo
                    break;

                case R.id.changeProfPictureItem:
                    menuItem.setChecked(true);
                    Toast.makeText(mContext, "change picture clicked", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> menuItem.setChecked(false), 500);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    //todo
                    break;

                case R.id.reset_deviceItem:
                    //todo
                    menuItem.setChecked(true);
                    Toast.makeText(mContext, "Reset device clicked", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> menuItem.setChecked(false), 500);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.signOutItem:
                    //todo
                    menuItem.setChecked(true);
                    Toast.makeText(mContext, "Sign out clicked", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> menuItem.setChecked(false), 500);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.resetPassItem:
                    //todo
                    menuItem.setChecked(true);
                    Toast.makeText(mContext, "Reset pass clicked", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> menuItem.setChecked(false), 500);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.delete_accountItem:
                    //todo
                    menuItem.setChecked(true);
                    Toast.makeText(mContext, "Delete account clicked", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> menuItem.setChecked(false), 500);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    break;
            }
            return false;
        });

    }

    public void initLayout() {
        usernameEditText = findViewById(R.id.username_editText);
        userNameTextView = findViewById(R.id.userNameTextView);
        saveUsernameButton = findViewById(R.id.saveUserNameButton);
        profilePic = findViewById(R.id.profilePicture);
        gridView = findViewById(R.id.grid_view_user_profile);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolBar);
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

    public void buttonListeners() {
        saveUsernameButton.setOnClickListener(this);
        profilePic.setOnClickListener(this);
    }

    private void hideEditText() {
        userNameTextView.setVisibility(View.INVISIBLE);
        usernameEditText.setVisibility(View.VISIBLE);
        editUsernameButton.setVisibility(View.INVISIBLE);
        saveUsernameButton.setVisibility(View.VISIBLE);
    }

    private void saveUsername() {

        //TODO can add other filters here later for database
        userName = usernameEditText.getText().toString();

        if (!TextUtils.isEmpty(userName)) {
            Log.d(TAG, "Typed username: " + userName);
            usernameEditText.setVisibility(View.INVISIBLE);
            saveUsernameButton.setVisibility(View.INVISIBLE);
            userNameTextView.setVisibility(View.VISIBLE);
            editUsernameButton.setVisibility(View.VISIBLE);
            userNameTextView.setText(userName);

        } else {
            usernameEditText.setError("Nothing here!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Todo: continue here with results from the camera
        Log.d(TAG, "request code: " + requestCode);
        Log.d(TAG, "result code: " + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                Bitmap bitmap = null;
                try {
                    bitmap = ImagePicker.getImageFromResult(getApplicationContext(), resultCode, data);
                } catch (IOException e) {
                    //do sth
                }
               // profilePic.setImageBitmap(bitmap);
                //Bitmap picture = bitmap;
                Glide.with(getApplicationContext()).load(bitmap).into(profilePic);
               // profilePic.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }

        } else
            Log.d(TAG, "Error on camera/Gallery");
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
        }

    }

    private void optionDialog() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(mContext);
        startActivityForResult(chooseImageIntent, REQUEST_CODE);
        checkPermissions();
    }

    private void signOut() {
        Intent intent = new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
        String testURL5 = "https://i.pinimg.com/236x/5e/af/81/5eaf818de906fb0375e1049d0c7e69a5--pink-geranium-geranium-pots.jpg";
        String testURL6 = "https://i.pinimg.com/236x/5e/af/81/5eaf818de906fb0375e1049d0c7e69a5--pink-geranium-geranium-pots.jpg";
        String testURL7 = "https://i.pinimg.com/236x/5e/af/81/5eaf818de906fb0375e1049d0c7e69a5--pink-geranium-geranium-pots.jpg";
        String testURL8 = "https://i.pinimg.com/236x/5e/af/81/5eaf818de906fb0375e1049d0c7e69a5--pink-geranium-geranium-pots.jpg";
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
    public void setupBottomNavigationView() {
        bottomNavigationViewEx = findViewById(R.id.bottomNavigationBar);
        BottomNavigationViewHelper.enableNavigation(getApplicationContext(), bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public interface OnGridImageSelectedListener {
        void onGridImageSelected(Plant plant, int activityNr);
    }


//    private void profileDialog() {
//        View dialogLayout = getLayoutInflater().inflate(R.layout.customized_alert_dialog, null);
//        ImageButton cameraButton = dialogLayout.findViewById(R.id.cameraButtonDialog);
//        ImageButton galleryButton = dialogLayout.findViewById(R.id.galleryButtonDialog);
//        ImageButton cancelButton = dialogLayout.findViewById(R.id.cancelButtonDialog);
//
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        dialogBuilder.setView(dialogLayout);
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
//        dialogBuilder.setView(dialogLayout);
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
}