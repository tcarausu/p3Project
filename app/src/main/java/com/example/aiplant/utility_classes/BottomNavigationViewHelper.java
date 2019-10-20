package com.example.aiplant.utility_classes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.aiplant.R;
import com.example.aiplant.home.HomeActivity;
import com.example.aiplant.user_profile.User_Profile;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * File created by tcarau18
 **/
public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";


    /**
     * Bottom Navigation View settup
     */
    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {
        Log.d(TAG, "setupBottomNavigationView: Setting up  BottomNavigationViewEx ");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(0, false);
        bottomNavigationViewEx.setItemHorizontalTranslationEnabled(false);
        bottomNavigationViewEx.setTextVisibility(false);

    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx viewEx) {
        viewEx.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.ic_home:
                    Intent homeIntent = new Intent(context, HomeActivity.class); //ACTIVITY_NUM = 0
                    context.startActivity(homeIntent
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    break;

                    case R.id.ic_user_profile:
                    Intent user_profile = new Intent(context, User_Profile.class); //ACTIVITY_NUM = 2
                    context.startActivity(user_profile
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    break;
//                case R.id.ic_search:
//                    Intent searchIntent = new Intent(context, SearchActivity.class);//ACTIVITY_NUM = 1
//                    context.startActivity(searchIntent
//                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                    break;
//
//                case R.id.ic_add_post:
//                    Intent addPostIntent = new Intent(context, AddPostActivity.class);//ACTIVITY_NUM = 2
//                    context.startActivity(addPostIntent
//                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                    break;
//                case R.id.ic_like_posts:
//                    Intent likePost = new Intent(context, HistoryLogActivity.class);//ACTIVITY_NUM = 3
//                    context.startActivity(likePost
//                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                    break;
//                case R.id.ic_user_profile:
//                    Intent userProfileIntent = new Intent(context, UserProfileActivity.class);//ACTIVITY_NUM = 4
//                    context.startActivity(userProfileIntent
//                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//
//                    break;
            }
            return false;
        });

    }
}