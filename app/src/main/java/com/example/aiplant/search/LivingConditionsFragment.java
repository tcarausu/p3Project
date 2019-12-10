package com.example.aiplant.search;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.aiplant.R;
import com.example.aiplant.create_profile.PlantProfileFragment;


public class LivingConditionsFragment extends Fragment {

    private ImageView mPlantProfilePic;
    private TextView mPlantName;
    private TextView mPlantDescription;
    private ScrollView mPlantDescriptionScrollable;

    private TextView mMinSun;
    private TextView mMaxSun;
    private TextView mMinTemp;
    private TextView mMaxTemp;
    private TextView mMinHumidity;
    private TextView mMaxHumidity;

    private Button mCloseCardButton;
    private Button mAddPlantButton;


    private static final String TAG = "LivingConditionsFragm";
    public LivingConditionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_plant_living_conditions, container, false);
        findWidgets(v);
        Bundle bundle = this.getArguments();
        if (bundle != null){

            Glide.with(getActivity().getApplicationContext()).load(bundle.getString("profilePictureUrl")).into(mPlantProfilePic);
            mPlantName.setText(bundle.getString("name"));
            mPlantDescription.setText(bundle.getString("description"));

            mMinSun.setText(bundle.getString("minSun"));

//            Log.d(TAG, "onCreateView12345: " + getString(mMinSun));
            mMaxSun.setText(bundle.getString("maxSun"));
            mMinTemp.setText(bundle.getString("minTemp"));
            mMaxTemp.setText(bundle.getString("maxTemp"));
            mMinHumidity.setText(bundle.getString("minHumidity"));
            mMaxHumidity.setText(bundle.getString("maxHumidity"));
//
//            mPlantDescriptionScrollable.addView(mPlantDescription);


        }
        return v;

    }

    private void findWidgets(View v) {
        mPlantProfilePic = v.findViewById(R.id.plant_profile_image);
        mPlantName = v.findViewById(R.id.plant_name);
        mPlantDescriptionScrollable = v.findViewById(R.id.plant_description_view);
        mPlantDescription = v.findViewById(R.id.plant_description_text);

        mMinSun = v.findViewById(R.id.min_sun);
        mMaxSun = v.findViewById(R.id.max_sun);
        mMinHumidity = v.findViewById(R.id.min_humidity);
        mMaxHumidity = v.findViewById(R.id.max_humidity);
        mMinTemp = v.findViewById(R.id.min_temp);
        mMaxTemp = v.findViewById(R.id.max_temp);

        mCloseCardButton = v.findViewById(R.id.close_button);

        mCloseCardButton.setOnClickListener(view -> {

            Log.d(TAG, "findWidgets: ");

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment searchListFragment = fragmentManager.findFragmentById(R.id.plant_list_fragment);
//            if (searchListFragment == null) {
                searchListFragment = new SearchListFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.plant_list_fragment, searchListFragment).commit();
//            }
        });

        mAddPlantButton = v.findViewById(R.id.add_plant_button);

        mAddPlantButton.setOnClickListener(view -> {

            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
            Fragment plantProfilePragment = fragmentManager1.findFragmentById(R.id.plant_list_fragment);

            plantProfilePragment = new PlantProfileFragment();
            bundleFunctionality(plantProfilePragment);
//                     if (livingCondFragment == null) {
            FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
            fragmentTransaction1.addToBackStack(null);
            fragmentTransaction1.replace(R.id.plant_list_fragment, plantProfilePragment).commit();
        });

    }

    private void bundleFunctionality(Fragment plantProfilePragment) {
        Bundle bundle = new Bundle();

        bundle.putString("minSun",mMinSun.getText().toString());
        bundle.putString("maxSun",mMaxSun.getText().toString());
        bundle.putString("minHumidity",mMinHumidity.getText().toString());
        bundle.putString("maxHumidity",mMaxHumidity.getText().toString());
        bundle.putString("minTemp",mMinTemp.getText().toString());
        bundle.putString("maxTemp",mMaxTemp.getText().toString());

        plantProfilePragment.setArguments(bundle);
    }


}
