package com.example.aiplant.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiplant.R;
import com.example.aiplant.model.RecyclerViewPlantItem;
import com.example.aiplant.utility_classes.MongoDbSetup;
import com.example.aiplant.utility_classes.RecyclerViewAdapter;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoIterable;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SearchListFragment extends Fragment implements View.OnClickListener {

    private String TAG = "SearchListFragment";

    //recycler view
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ImageButton searchButton;
    private EditText search_bar;

    private MongoDbSetup mongoDbSetup;

    private ArrayList<RecyclerViewPlantItem> listOfPlants = new ArrayList<>();

    private List<Document> docsToUse = new ArrayList<>();

    private String plant_name;
    private String picture_url;
    private String description;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_plant_from_database, container, false);

        mongoDbSetup =  MongoDbSetup.getInstance(getActivity());

        findPlantsList();

        findWidgets(v);

        return v;

    }


    private void findWidgets(View v) {
        mRecyclerView = v.findViewById(R.id.recyclerView);
        searchButton = v.findViewById(R.id.search_button_id);
        search_bar = v.findViewById(R.id.search_bar_id);

        searchButton.setOnClickListener(this);
    }

    private void findPlantsList() {
        if (!mongoDbSetup.checkInternetConnection(Objects.requireNonNull(getActivity()))) {
            Toast.makeText(getActivity(), getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
        } else {
            try {

                RemoteMongoCollection<Document> plants = mongoDbSetup.getCollectionByName(getString(R.string.eye_plant_plants));
                RemoteMongoIterable<Document> plantIterator = plants.find();

                final ArrayList<Document> docsToUser = new ArrayList<>();

                plantIterator
                        .forEach(document -> {
                            docsToUser.add(document);
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
    }

    private void searchList() {
        String keyword = search_bar.getText().toString();
        if (!mongoDbSetup.checkInternetConnection(Objects.requireNonNull(getActivity()))) {
            Toast.makeText(getActivity(), getString(R.string.check_internet_connection_search_list), Toast.LENGTH_LONG).show();
        } else {

            try {
                if (!TextUtils.isEmpty(keyword)) {
                    RemoteMongoCollection<Document> plants = mongoDbSetup.getCollectionByName("plants");
                    RemoteMongoIterable<Document> plantIterator = plants.find();

                    docsToUse.clear();
                    listOfPlants.clear();
                    mRecyclerView.removeAllViews();

                    final ArrayList<Document> docs = new ArrayList<>();

                    plantIterator
                            .forEach(document -> {
                                plant_name = document.getString("plant_name");
                                picture_url = document.getString("picture_url");
                                description = document.getString("description");

                                if (plant_name.toLowerCase().contains(keyword.toLowerCase())) {

                                    docs.add(document);
                                    setPlantList(docs);
                                    listOfPlants.add(new RecyclerViewPlantItem(picture_url, plant_name, description));

                                }
                            })

                            .addOnCompleteListener(task -> {
                                if (listOfPlants.size() == 0) {
                                    search_bar.requestFocus();
                                    search_bar.setError("No match found");

                                    listOfPlants.clear();
                                }
                                mRecyclerView.setHasFixedSize(true);
                                mLayoutManager = new LinearLayoutManager(getActivity());
                                mAdapter = new RecyclerViewAdapter(listOfPlants, getActivity());
                                mAdapter.notifyDataSetChanged();

                                mRecyclerView.setLayoutManager(mLayoutManager);
                                mRecyclerView.setAdapter(mAdapter);
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "error " + e.getMessage()));

                } else if (searchButton.isPressed() && TextUtils.isEmpty(keyword)) {
                    search_bar.setError("Please type a keyword");
                    listOfPlants.clear();
                    findPlantsList();
                }
            } catch (Throwable e) {
                Log.e(TAG, "NullPointerException: " + e.getMessage());
            }
        }

    }

    private void setUpRecyclerView() {

        if (listOfPlants.size() == 0) {
            populatePlantList();
        }

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new RecyclerViewAdapter(listOfPlants, getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void populatePlantList() {
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
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.search_button_id) {
            searchList();
        }
    }

    private ArrayList<Document> getPlantList() {
        return plantList;
    }

    private void setPlantList(ArrayList<Document> plantList) {
        this.plantList = plantList;
    }

    public void setPlantList(Document document) {
        this.plantList.add(document);
    }

    private ArrayList<Document> plantList = new ArrayList<>();

    public List<Document> getDocsToUse() {
        return docsToUse;
    }

    public void setDocsToUse(List<Document> docsToUse) {
        this.docsToUse = docsToUse;
    }

}
