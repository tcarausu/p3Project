package com.example.aiplant.home;

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

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;

    // widgets
    private Button loginButton;
    private TextView flower_pic_name, flower_pic_time;
    private TextView temperature_text, humidity_text, sunlight_text;
    private SeekBar temperature_slider, humidity_slider, sunlight_slider;
    private ImageView flower_pic, mood_pic;
    private RelativeLayout home_Layout;
    private FragmentManager fragmentManager;
    private boolean isVerified;
    private Context mContext;
//    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initLayout();
        buttonListeners();

        setupBottomNavigationView();

    }


    public void initLayout() {
        home_Layout = findViewById(R.id.home_activity);
        flower_pic_name = findViewById(R.id.flower_pic_name);
        flower_pic_time = findViewById(R.id.flower_pic_time);

        //Conditions
        temperature_text = findViewById(R.id.temperature_text);
        humidity_text = findViewById(R.id.humidity_text);
        sunlight_text = findViewById(R.id.sunlight_text);

        temperature_slider = findViewById(R.id.temperature_slider);
        humidity_slider = findViewById(R.id.humidity_slider);
        sunlight_slider = findViewById(R.id.sunlight_slider);

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
