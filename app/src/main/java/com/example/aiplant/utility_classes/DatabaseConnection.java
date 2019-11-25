package com.example.aiplant.utility_classes;

import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;

import org.bson.Document;

import java.util.Objects;


public class DatabaseConnection {

    private static final StitchAppClient client = Stitch.initializeDefaultAppClient("eye-plant-tilrj");
    private static final RemoteMongoClient mongoClient =
            client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

    public void connectToMongo() {
        client.getAuth().loginWithCredential(new AnonymousCredential());
    }

    public static void addToCollection(Document document, String collectionName) {
        document.put("user_id", Objects.requireNonNull(client.getAuth().getUser()).getId());

        final RemoteMongoCollection<Document> coll =
                mongoClient.getDatabase("test").getCollection(collectionName);

        if (coll.find(document) != null) {
            coll.updateOne(null, document);
        } else {
            coll.insertOne(document);
        }

    }
}
