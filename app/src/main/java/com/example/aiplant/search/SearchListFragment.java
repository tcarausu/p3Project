package com.example.aiplant.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiplant.R;
import com.example.aiplant.model.RecyclerViewPlantItem;
import com.example.aiplant.utility_classes.MongoDbSetup;
import com.example.aiplant.utility_classes.RecyclerViewAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoIterable;

import org.bson.Document;

import java.util.ArrayList;


public class SearchListFragment extends Fragment implements View.OnClickListener {

    private String TAG = "SearchListFragment";

    //recycler view
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private MongoDbSetup mongoDbSetup;
    private StitchAppClient appClient;
    private GoogleSignInClient mGoogleSignInClient;

    public ArrayList<Document> getPlantList() {
        return plantList;
    }

    public void setPlantList(ArrayList<Document> plantList) {
        this.plantList = plantList;
    }

    public void setPlantList(Document document) {
        this.plantList.add(document);
    }

    private ArrayList<Document> plantList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_plant_from_database, container, false);

        mongoDbSetup = ((SearchActivity) getActivity()).getMongoDbForLaterUse();


        findPlantsList();

        findWidgets(v);

        return v;

    }


    private void findWidgets(View v) {
        mRecyclerView = v.findViewById(R.id.recyclerView);

    }


    @Override
    public void onClick(View view) {

    }

    public void findPlantsList() {
        try {

            RemoteMongoCollection<Document> plants = mongoDbSetup.getCollectionByName(getString(R.string.eye_plant_plants));
            RemoteMongoIterable<Document> plantIterator = plants.find();

            ArrayList<Document> docsToUser = new ArrayList<>();

            plantIterator
                    .forEach(document -> {
                        docsToUser.add(document);
                        Log.d(TAG, "doc" + docsToUser.toString());
                        Log.d(TAG, "doc" + docsToUser.size());
                        setPlantList(docsToUser);
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error" + e.getCause()))

                    .addOnCompleteListener(task -> {

                        if (getPlantList().size() == docsToUser.size() && getPlantList().size() != 0) {
                            setUpRecyclerView();
                        }
                    });
        } catch (Throwable e) {
            Log.e(TAG, "NullPointerException: " + e.getMessage());
        }
    }

    private void setUpRecyclerView() {

        ArrayList<RecyclerViewPlantItem> listOfPlants = new ArrayList<>();

        for (Document doc : getPlantList()) {
            String picture = doc.getString("picture_url");
            String name = doc.getString("plant_name");
            String description = doc.getString("description");
            ArrayList sunlightArray = doc.get("sunlight", ArrayList.class);
            String min_sun = sunlightArray.get(0).toString();
            String max_sun = sunlightArray.get(1).toString();
            ArrayList temperatureArray = doc.get("temperature", ArrayList.class);
            String min_temp = temperatureArray.get(0).toString();
            String max_temp = temperatureArray.get(1).toString();
            ArrayList humidityArray = doc.get("humidity", ArrayList.class);
            String min_humidity = humidityArray.get(0).toString();
            String max_humidity = humidityArray.get(1).toString();


            listOfPlants.add(new RecyclerViewPlantItem(picture, name, description, min_sun, max_sun, min_temp, max_temp, min_humidity, max_humidity));

        }

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new RecyclerViewAdapter(listOfPlants, getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


}
