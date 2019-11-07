package com.example.aiplant.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiplant.R;
import com.example.aiplant.create_profile.PlantProfile;
import com.example.aiplant.model.RecyclerViewPlantItem;
import com.example.aiplant.utility_classes.BottomNavigationViewHelper;
import com.example.aiplant.utility_classes.RecyclerViewAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "User_Profile";
    private static final int ACTIVITY_NUM = 1;
    //widgets
    private EditText mSearchParam;
    private ImageView backArrow;
    private ImageButton mSearchButton;
    private ImageView mPlantListButton;
    private ImageView mCreatePlantProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plant_library_layout);
        initLayout();
        buttonListeners();
        setupBottomNavigationView();


    }




    public void initLayout() {
        mSearchParam = findViewById(R.id.search_bar_id);
        mSearchButton = findViewById(R.id.search_button_id);
        mCreatePlantProfile = findViewById(R.id.create_new_plant_button);
        mPlantListButton = findViewById(R.id.library_button);

    }

    public void buttonListeners() {
//        mSearchParam.setOnClickListener(this);
//        mSearchButton.setOnClickListener(this);
        mPlantListButton.setOnClickListener(this);
        mCreatePlantProfile.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.create_new_plant_button:
                startActivity(new Intent(this, PlantProfile.class));
                break;
            case R.id.library_button:
                startActivity(new Intent(this,AuxActivity.class));
                Log.d(TAG, "onClick: do something");
                break;
        }
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
