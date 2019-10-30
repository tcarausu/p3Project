package com.example.aiplant.user_profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.aiplant.R;
import com.example.aiplant.login.LoginActivity;
import com.example.aiplant.model.Plant;
import com.example.aiplant.utility_classes.BottomNavigationViewHelper;
import com.example.aiplant.utility_classes.GridImageAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class User_Profile extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "User_Profile";
    private static final int ACTIVITY_NUM = 2;
    private static final int NUM_GRID_COLUMNS = 3;
    private static final int REQUEST_CAMERA = 22;
    private static final int REQUEST_GALLERY = 33;

    // widgets
    private Button editUsernameButton, saveUsernameButton;
    private ImageButton optionButton;
    private TextView userNameTextView;
    private EditText usernameEditText; //usernameEditText
    private GridView gridView;
    private CircularImageView profilePic;
    private RelativeLayout home_Layout;

    //context and others
    private FragmentManager fragmentManager;
    Drawable drawable ;
    private Context mContext;
    OnGridImageSelectedListener onGridImageSelectedListener;

    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //variables
    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        mContext = getApplicationContext();

        fragmentManager = getSupportFragmentManager();
        initLayout();
        checkPermissions();
        buttonListeners();
        setupBottomNavigationView();
        setupGridView();

        //How to get a drawable
         drawable = ContextCompat.getDrawable(mContext, R.drawable.logo_eye_plant);
         gridView.setBackground(drawable);

    }

    public void initLayout() {
        home_Layout = findViewById(R.id.home_activity);
        usernameEditText = findViewById(R.id.username_editText);
        userNameTextView = findViewById(R.id.userNameTextView);
        editUsernameButton = findViewById(R.id.editUserNameButton);
        saveUsernameButton = findViewById(R.id.saveUserNameButton);
        profilePic = findViewById(R.id.profilePicture);
        optionButton = findViewById(R.id.optionButton);
        gridView = findViewById(R.id.grid_view_user_profile);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_GALLERY | REQUEST_CAMERA);
        }
    }

    public void buttonListeners() {
        editUsernameButton.setOnClickListener(this);
        saveUsernameButton.setOnClickListener(this);
        profilePic.setOnClickListener(this);
        optionButton.setOnClickListener(this);
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
            Log.d(TAG, "Typed username: "+userName);
            usernameEditText.setVisibility(View.INVISIBLE);
            saveUsernameButton.setVisibility(View.INVISIBLE);
            userNameTextView.setVisibility(View.VISIBLE);
            editUsernameButton.setVisibility(View.VISIBLE);
            userNameTextView.setText(userName);

        } else {
            usernameEditText.setError("Nothing here!");
        }
    }

    private void takePicture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else
            Toast.makeText(mContext, "Battery is low...", Toast.LENGTH_SHORT).show();
    }

    private void selectPicture() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Todo: continue here with results from the camera
        if (resultCode == RESULT_OK && (requestCode == REQUEST_CAMERA || requestCode == REQUEST_GALLERY)) {
            //Todo: we can add other implementation here, like loading the image to database
            Uri uri = Uri.parse(data.getData().toString());
            Glide.with(this).load(uri).fitCenter().into(profilePic);
            profilePic.refreshDrawableState();
        } else Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    private void openChoiceDialog() {

        CharSequence[] options = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select image source:");
        builder.setCancelable(true);
        builder.setIcon(R.mipmap.eye_logo);
        builder.setItems(options, (dialog, which) -> {

            if (options[which].equals("Camera")) {
                takePicture();
            } else if (options[which].equals("Gallery")) {
                selectPicture();

            } else if (options[which].equals("Cancel")) {
                dialog.dismiss();
            }
        });

        builder.create();
        builder.show();

    }
    private void customizeddialog(String s1, String s2, String s3) {

        CharSequence[] options = {s1, s2, s3};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile menu:");
        builder.setIcon(R.mipmap.eye_logo);
        builder.setItems(options, (dialog, which) -> {

            if (options[which].equals("Account settings")) {
                //Todo: continue with fragment
                Toast.makeText(mContext,"account settings pressed",Toast.LENGTH_SHORT).show();

            } else if (options[which].equals("Sign out")) {
                signOut();

            } else if (options[which].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();

    }

    private void signOut() {
        Intent intent = new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.editUserNameButton:
                hideEditText();
                break;

            case R.id.saveUserNameButton:
                saveUsername();
                break;

            case R.id.profilePicture:
                profileDialog();

                break;
            case R.id.optionButton:
                //TODO continue with this implementation
                topBarDialog();
                break;
        }
//            switch (v.getId()) {
//
//                case R.id.button_id_log_in:
////                    signInWithEmail();
//
//                    break;
//                case R.id.textView_id_forgotPass_logIn:
//
//                    Fragment fragmentForgotPass = fragmentManager.findFragmentById(R.id.useThisFragmentID);
//
//                    if (fragmentForgotPass == null) {
//                        fragmentForgotPass = new ForgotPassFragment();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.addToBackStack(null);
//                        fragmentTransaction.add(R.id.useThisFragmentID, fragmentForgotPass).commit();
//                    }
//
//                    break;
//
//                case R.id.sign_up:
//                    Fragment fragmentRegister = fragmentManager.findFragmentById(R.id.useThisFragmentID);
//                    if (fragmentRegister == null) {
//                        fragmentRegister = new SignUpFragment();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.addToBackStack(null);
//
//                        fragmentTransaction.add(R.id.useThisFragmentID, fragmentRegister).commit();
//                    }
//
//                    break;
//
//
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

        GridImageAdapter adapter = new GridImageAdapter(this, R.layout.layout_grid_imageview,
                "", imgURLs);

        gridView.setAdapter(adapter);
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
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigationBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(getApplicationContext(), bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setTitle("Profile");
        menuItem.setChecked(true);

    }

    public interface OnGridImageSelectedListener {
        void onGridImageSelected(Plant plant, int activityNr);
    }




    private void profileDialog() {
        View dialogLayout = getLayoutInflater().inflate(R.layout.customized_alert_dialog, null);

        ImageButton cameraButton = dialogLayout.findViewById(R.id.cameraButtonDialog);
        ImageButton galleryButton = dialogLayout.findViewById(R.id.galleryButtonDialog);
        ImageButton cancelButton = dialogLayout.findViewById(R.id.cancelButtonDialog);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogLayout);

        final AlertDialog alertDialog = dialogBuilder.create();
        WindowManager.LayoutParams wlp = alertDialog.getWindow().getAttributes();

//        wlp.windowAnimations = R.style.AlertDialogAnimation;
        wlp.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        alertDialog.getWindow().setAttributes(wlp);
        alertDialog.setCanceledOnTouchOutside(true);
        // Setting transparent the background (layout) of alert dialog
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();


        cameraButton.setOnClickListener(v -> {
            takePicture();
            alertDialog.dismiss();
        });

        galleryButton.setOnClickListener(v -> {
            selectPicture();
            alertDialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }






    private void topBarDialog() {
        View dialogLayout = getLayoutInflater().inflate(R.layout.top_bar_dialog_layout, null);

        TextView accountSettingsTextView = dialogLayout.findViewById(R.id.accountSettingsTextView);
        TextView signOutTextView = dialogLayout.findViewById(R.id.signOutTextView);
        TextView cancelTextView = dialogLayout.findViewById(R.id.cancelTextView);


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogLayout);

        final AlertDialog alertDialog = dialogBuilder.create();
        WindowManager.LayoutParams wlp = alertDialog.getWindow().getAttributes();

        //wlp.windowAnimations = R.style.AlertDialogAnimation;
        wlp.gravity =  Gravity.RIGHT;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        alertDialog.getWindow().setAttributes(wlp);
        alertDialog.setCanceledOnTouchOutside(true);
        // Setting transparent the background (layout) of alert dialog
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();


        accountSettingsTextView.setOnClickListener(v -> {
            Toast.makeText(mContext,"Account settings clicked",Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });

        signOutTextView.setOnClickListener(v -> {
            signOut();
            alertDialog.dismiss();
        });

        cancelTextView.setOnClickListener(v -> {
            Toast.makeText(mContext,"Cancel button clicked",Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });
    }

}
