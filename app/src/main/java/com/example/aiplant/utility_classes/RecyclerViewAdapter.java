package com.example.aiplant.utility_classes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aiplant.R;
import com.example.aiplant.create_profile.PlantProfileFragment;
import com.example.aiplant.model.RecyclerViewPlantItem;
import com.example.aiplant.search.LivingConditionsFragment;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.database.DatabaseReference;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchAuth;
import com.mongodb.stitch.android.core.auth.StitchUser;

import org.bson.Document;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CardViewHolder> {
    //database
    private static MongoDbSetup mongoDbSetup;
    private DatabaseReference user_ref;
    private DatabaseReference myRef;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private StitchAuth mStitchAuth;
    private StitchUser mStitchUser;
    private Document updateDoc, fetchedDOc;
    private Context mContext;
    private StitchAppClient appClient;

    private ArrayList<RecyclerViewPlantItem> mPlantList;


    public RecyclerViewAdapter(ArrayList<RecyclerViewPlantItem> plantList, Context auxContext) {
        mPlantList = plantList;
        mContext = auxContext;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant_card_from_database, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

        RecyclerViewPlantItem currentItem = mPlantList.get(position);

        holder.setProfilePictureUrl(currentItem.getPlantProfileUrl());
        holder.setMinSun(currentItem.getMinSun());
        holder.setMaxSun(currentItem.getMaxSun());
        holder.setMinTemp(currentItem.getMinTemp());
        holder.setMaxTemp(currentItem.getMaxTemp());
        holder.setMinHumidity(currentItem.getMinHumidity());
        holder.setMaxHumidity(currentItem.getMaxHumidity());

        Glide.with(mContext).load(currentItem.getPlantProfileUrl()).fitCenter().into(holder.mPlantProfilePicture);

        holder.mPlantName.setText(currentItem.getmPlantName());
        holder.mPlantDescription.setText(currentItem.getmPlantDescription());


    }

    @Override
    public int getItemCount() {
        return mPlantList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "CardViewHolder";
        ImageView mPlantProfilePicture;
        TextView mPlantName;
        TextView mPlantDescription;
        Button mAddButton;
        Button mCloseButton;

        public void setProfilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
        }

        String profilePictureUrl;
        String minSun;
        String maxSun;
        String minTemp;
        String maxTemp;
        String minHumidity;
        String maxHumidity;

        public void setMinSun(String minSun) {
            this.minSun = minSun;
        }

        public void setMaxSun(String maxSun) {
            this.maxSun = maxSun;
        }

        public void setMinTemp(String minTemp) {
            this.minTemp = minTemp;
        }

        public void setMaxTemp(String maxTemp) {
            this.maxTemp = maxTemp;
        }

        public void setMinHumidity(String minHumidity) {
            this.minHumidity = minHumidity;
        }

        public void setMaxHumidity(String maxHumidity) {
            this.maxHumidity = maxHumidity;
        }

        CardViewHolder(@NonNull View itemView) {
            super(itemView);

            mPlantProfilePicture = itemView.findViewById(R.id.plant_profile_image);
            mPlantName = itemView.findViewById(R.id.plant_name);
            mPlantDescription = itemView.findViewById(R.id.plant_description);
            mAddButton = itemView.findViewById(R.id.add_plant_button_card);
            mCloseButton = itemView.findViewById(R.id.close_button);

            setButtonListeners(itemView);


        }

        private void setButtonListeners(View v) {
            v.findViewById(R.id.click_for_living_conditions).setOnClickListener(this);
            v.findViewById(R.id.add_plant_button_card).setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.close_button:

                    break;

                case R.id.add_plant_button_card:

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
                    Fragment plantProfilePragment = fragmentManager1.findFragmentById(R.id.plant_list_fragment);

                    plantProfilePragment = new PlantProfileFragment();
                    bundleFunctionality(plantProfilePragment);

                    FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
                    fragmentTransaction1.addToBackStack(null);
                    fragmentTransaction1.replace(R.id.plant_list_fragment, plantProfilePragment).commit();

                    break;
                case R.id.click_for_living_conditions:

                    AppCompatActivity activity1 = (AppCompatActivity) view.getContext();
                    FragmentManager fragmentManager = activity1.getSupportFragmentManager();
                    Fragment livingCondFragment = new LivingConditionsFragment();

//                    livingCondFragment = new LivingConditionsFragment();
                    bundleFunctionality(livingCondFragment);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.plant_list_fragment, livingCondFragment).commit();
                    break;

            }
        }

        private void bundleFunctionality(Fragment livingConditions) {
            Bundle bundle = new Bundle();
            String plant_name = mPlantName.getText().toString();
            String plant_description = mPlantDescription.getText().toString();
            String plant_profile_pic = profilePictureUrl;

            bundle.putString("name", plant_name);
            bundle.putString("description", plant_description);
            bundle.putString("profilePictureUrl", plant_profile_pic);
            bundle.putString("minSun", minSun);
            bundle.putString("maxSun", maxSun);
            bundle.putString("minTemp", minTemp);
            bundle.putString("maxTemp", maxTemp);
            bundle.putString("minHumidity", minHumidity);
            bundle.putString("maxHumidity", maxHumidity);

            livingConditions.setArguments(bundle);

        }

    }
}
