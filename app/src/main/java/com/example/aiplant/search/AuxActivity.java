package com.example.aiplant.search;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiplant.R;
import com.example.aiplant.model.RecyclerViewPlantItem;
import com.example.aiplant.utility_classes.RecyclerViewAdapter;

import java.util.ArrayList;

public class AuxActivity extends AppCompatActivity implements View.OnClickListener {

    //recycler view
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_plant_from_database);
        mRecyclerView = findViewById(R.id.recyclerView);
        setUpRecyclerView();


    }

    private void setUpRecyclerView() {


        ArrayList<RecyclerViewPlantItem> listOfPlants = new ArrayList<>();
        listOfPlants.add(new RecyclerViewPlantItem(R.drawable.peace_lily, "Peace Lily", "If you're guilty of overwatering, try Spathiphyllum. Peace lilies can \"almost grow in a fish tank,\" Fried says. Bonus: These powerful plants can also filter toxins from the air, according to NASA."));
        listOfPlants.add(new RecyclerViewPlantItem(R.drawable.money_tree, "Money Tree", "According to the Missouri Botanical Garden, the belief that this plant will bring good fortune comes from an old story that states that a poor man became wealthy when he found the tree and started selling its seeds, which are edible. The young, tender leaves and flowers are cooked and eaten like vegetables, but it is the seeds -- which taste a bit like peanuts -- that are so desirable. They can be eaten raw or cooked, and can also be ground into flour."));
        listOfPlants.add(new RecyclerViewPlantItem(R.drawable.yucca, "Yucca", "The recipe for a happy yucca is easy: sun, sun, and more sun. Water sparingly and plant in a deep container to prevent the top-heavy woody stems from toppling over."));
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RecyclerViewAdapter(listOfPlants);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {

    }
}
