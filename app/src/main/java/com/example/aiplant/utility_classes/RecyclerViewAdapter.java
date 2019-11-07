package com.example.aiplant.utility_classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiplant.R;
import com.example.aiplant.model.RecyclerViewPlantItem;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CardViewHolder> {

    private ArrayList<RecyclerViewPlantItem> mPlantList;

    public RecyclerViewAdapter(ArrayList<RecyclerViewPlantItem> plantList){
        mPlantList = plantList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant_card_from_database, parent,false);
        CardViewHolder cardViewHolder = new CardViewHolder(view);
        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        RecyclerViewPlantItem currentItem = mPlantList.get(position);

        holder.mPlantProfilePicture.setImageResource(currentItem.getmPlantProfilePictureRes());
        holder.mPlantName.setText(currentItem.getmPlantName());
        holder.mPlantDescription.setText(currentItem.getmPlantDescription());
    }

    @Override
    public int getItemCount() {
        return mPlantList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public ImageView mPlantProfilePicture;
        public TextView mPlantName;
        public TextView mPlantDescription;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            mPlantProfilePicture = itemView.findViewById(R.id.plant_profile_image);
            mPlantName = itemView.findViewById(R.id.plant_name);
            mPlantDescription = itemView.findViewById(R.id.plant_description);
        }
    }


}
