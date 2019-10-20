package com.example.aiplant.user_profile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.aiplant.R;
import com.example.aiplant.utility_classes.BottomNavigationViewHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User_Profile extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "User_Profile";
    private static final int ACTIVITY_NUM = 1;

    // widgets
    private Button loginButton;
    private TextView user_profile_pic_name, user_profile_pic_time;

    private ImageView flower_pic, mood_pic;
    private RelativeLayout home_Layout;
    private FragmentManager fragmentManager;
    private boolean isVerified;
    private Context mContext;
//    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        initLayout();
        buttonListeners();

        setupBottomNavigationView();

    }


    public void initLayout() {
        home_Layout = findViewById(R.id.home_activity);
        user_profile_pic_name = findViewById(R.id.user_profile_pic_name);
        user_profile_pic_time = findViewById(R.id.user_profile_pic_time);

        fragmentManager = getSupportFragmentManager();

    }

    public void buttonListeners() {

//        findViewById(R.id.button_id_log_in).setOnClickListener(this);
//        findViewById(R.id.googleSignInButton).setOnClickListener(this);
//        findViewById(R.id.textView_id_forgotPass_logIn).setOnClickListener(this);
    }

//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

    @Override
    public void onClick(View v) {

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
//                case R.id.googleSignInButton:
////                    signIn();
//                    break;
//            }
    }
//
//
//    /**
//     * In this method we setup the Grid View base on an Object Map of the Post entity for our database
//     * <p>
//     * It creates a list of likes, which is later on used in the View Post Fragment
//     * <p>
//     * Based on the number of posts it will display the post, but it will be limited to 3 per Row.
//     */
//    private void setupGridView() {
//        Log.d(TAG, "setupGridView: Setting up GridView");
//
//        try {
//            final ArrayList<Post> posts = new ArrayList<>();
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
//                        Post post = new Post();
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
//
//                    //setup  our grid image
//                    int gridWidth = getResources().getDisplayMetrics().widthPixels;
//                    int imageWidth = gridWidth / NUM_GRID_COLUMNS;
//
//                    gridView.setColumnWidth(imageWidth);
//
//                    ArrayList<String> imgURLs = new ArrayList<>();
//
//                    for (int i = 0; i < posts.size(); i++) {
//                        imgURLs.add(posts.get(i).getmFoodImgUrl());
//                    }
//
//                    GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview,
//                            "", imgURLs);
//
//                    gridView.setAdapter(adapter);
//                    gridView.setOnItemClickListener((parent, view, position, id) -> onGridImageSelectedListener.onGridImageSelected(posts.get(position), ACTIVITY_NUM));
//
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
//    }


    /**
     * Bottom Navigation View setup
     */
    public void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigationBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(getApplicationContext(), bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }

}
