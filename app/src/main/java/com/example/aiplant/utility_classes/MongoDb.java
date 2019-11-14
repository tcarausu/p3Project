package com.example.aiplant.utility_classes;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiplant.R;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

public class MongoDb extends AppCompatActivity {


    final StitchAppClient client =
            Stitch.initializeDefaultAppClient(getResources().getString(R.string.my_app_id));

    final RemoteMongoClient mongoClient =
            client.getServiceClient(RemoteMongoClient.factory, getResources().getString(R.string.service_name));


    final RemoteMongoCollection<Document> coll =
            mongoClient.getDatabase(getResources().getString(R.string.eye_plant))
                    .getCollection(getResources().getString(R.string.eye_plant_plants));



}
