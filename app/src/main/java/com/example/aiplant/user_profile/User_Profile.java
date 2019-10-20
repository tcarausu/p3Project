package com.example.aiplant.user_profile;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.aiplant.R;
import com.example.aiplant.utility_classes.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

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
