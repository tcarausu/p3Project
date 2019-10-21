package com.example.aiplant.search;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiplant.R;
import com.example.aiplant.utility_classes.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "User_Profile";
    private static final int ACTIVITY_NUM = 1;
    //widgets
    private EditText mSearchParam;
    private ImageView backArrow;
    private ImageButton mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        initLayout();
        buttonListeners();

        setupBottomNavigationView();

    }


    public void initLayout() {
        mSearchParam = findViewById(R.id.search_bar_id);
        backArrow = findViewById(R.id.backArrow);
        mSearchButton = findViewById(R.id.search_button_id);

    }

    public void buttonListeners() {
        backArrow.setOnClickListener(this);
        mSearchParam.setOnClickListener(this);
        mSearchButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

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
