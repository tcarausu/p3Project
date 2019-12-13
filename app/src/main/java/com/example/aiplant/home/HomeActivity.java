package com.example.aiplant.home;


import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.aiplant.R;
import com.example.aiplant.services.ScheduledFetch;
import com.example.aiplant.utility_classes.BottomNavigationViewHelper;
import com.example.aiplant.utility_classes.MongoDbSetup;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchAuth;
import com.mongodb.stitch.android.core.auth.StitchUser;

import static com.example.aiplant.R.id;
import static com.example.aiplant.R.layout;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;
    private FragmentManager fragmentManager;

    //database
    private static MongoDbSetup mongoDbSetup;
    private GoogleSignInClient mGoogleSignInClient;
    private StitchAuth mStitchAuth;
    private StitchUser mStitchUser;
    private Context mContext;
    private StitchAppClient appClient;

    private ScheduledFetch scheduledFetch;

    public static MongoDbSetup getMongoDbSetup() {
        return mongoDbSetup;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_home);
        fragmentManager = getSupportFragmentManager();
        mContext = this;

        connectMongoDb();

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.useThisFragmentID);

        if (currentFragment == null) {
            currentFragment = new HomeFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.add(R.id.useThisFragmentID, currentFragment).commit();
        }


        setupBottomNavigationView();
    }


    private void connectMongoDb() {
        mongoDbSetup = MongoDbSetup.getInstance(mContext);
        mGoogleSignInClient = mongoDbSetup.getGoogleSignInClient();

        appClient = mongoDbSetup.getAppClient();

        mStitchAuth = mongoDbSetup.getStitchAuth();
        mStitchUser = mongoDbSetup.getStitchUser();

    }

    public synchronized MongoDbSetup getMongoDbForLaterUse() {
        return mongoDbSetup;
    }

    /**
     * Bottom Navigation View setup
     */
    public void setupBottomNavigationView() {

        BottomNavigationViewHelper bnh = new BottomNavigationViewHelper();
        BottomNavigationView bottomNavigationViewEx = findViewById(id.bottomNavigationBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        bnh.enableNavigation(getApplicationContext(), bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mongoDbSetup.checkInternetConnection(mContext)) {
            Toast.makeText(getApplicationContext(), getString(R.string.check_internet_connection_display_profile), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mongoDbSetup.checkInternetConnection(mContext)) {
            Toast.makeText(getApplicationContext(), getString(R.string.check_internet_connection_display_profile), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
