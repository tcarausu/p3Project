package com.example.aiplant.interfcaes;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoDatabase;

import org.bson.Document;

/**
 * Created by Mo.Msaad
 * @implements RemoteMongoDatabase
 * @use we inherit the MongoDB database methods and others without recreating the wheel
 **/

public interface Navigator extends RemoteMongoDatabase, RemoteMongoClient {

    void intentWithFlag(Context sourceContext, Context current_Activity_Context, Class<? extends Activity> destinationClass);

    RemoteMongoClient getRemoteMongoDbClient();

    boolean checkInternetConnection(@NonNull Context context);

    void createPlantProfileDocument(String collectionName, String profileId, String plantName, String birthday,
                                    int minHumidity, int maxHumidity, int minTemperature,
                                    int maxTemperature, int minSun,
                                    int maxSun, String url, byte[] picture);

    void checkIfExists(RemoteMongoCollection coll, Document documentToCheck);

    GoogleSignInClient googleClient();

    @Override
    String getName();

    @Override
    RemoteMongoCollection<Document> getCollection(String collectionName);

    @Override
    <DocumentT> RemoteMongoCollection<DocumentT> getCollection(String collectionName, Class<DocumentT> documentClass);

    @Override
    RemoteMongoDatabase getDatabase(String databaseName);


}
