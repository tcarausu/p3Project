package com.example.aiplant.utility_classes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.aiplant.R;
import com.example.aiplant.home.HomeActivity;
import com.example.aiplant.search.SearchActivity;
import com.example.aiplant.user_profile.User_Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";

    //default state HomeActivity
    private static int bottomNavigationState;

    public  void setBottomNavigationState(int bottomNavigationState) {
        BottomNavigationViewHelper.bottomNavigationState = bottomNavigationState;
    }

    public BottomNavigationViewHelper() {
    }

    /**
     * Bottom Navigation View settup
     */
    public static void setupBottomNavigationView(BottomNavigationView bottomNavigationView) {
        Log.d(TAG, "setupBottomNavigationView: Setting up  BottomNavigationViewEx ");
//        bottomNavigationView.enableAnimation(false);
//        bottomNavigationView.setAnimation(null);
//        bottomNavigationViewEx.enableShiftingMode(0, false);
//        bottomNavigationViewEx.setItemHorizontalTranslationEnabled(false);
//        bottomNavigationViewEx.setTextVisibility(false);

    }

    public void enableNavigation(final Context context, BottomNavigationView viewEx) {

        viewEx.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {

                case R.id.ic_home:
                    if (bottomNavigationState != 0) {
                        Intent homeIntent = new Intent(context, HomeActivity.class); //ACTIVITY_NUM = 0
                        context.startActivity(homeIntent
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        bottomNavigationState = 0;
                    }
                    break;

                case R.id.ic_search:
                    if (bottomNavigationState != 1) {
                        Intent searchIntent = new Intent(context, SearchActivity.class);//ACTIVITY_NUM = 1
                        context.startActivity(searchIntent
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        bottomNavigationState = 1;
                    }
                    break;
                case R.id.ic_user_profile:
                    if (bottomNavigationState != 2) {
                        Intent user_profile = new Intent(context, User_Profile.class); //ACTIVITY_NUM = 2
                        context.startActivity(user_profile
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        bottomNavigationState = 2;
                    }
                    break;

            }
            return false;
        });

    }
}