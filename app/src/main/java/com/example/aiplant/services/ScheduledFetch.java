package com.example.aiplant.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.aiplant.R;
import com.example.aiplant.home.HomeActivity;
import com.example.aiplant.utility_classes.MongoDbSetup;
import com.mongodb.stitch.android.core.auth.StitchAuth;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;

import org.bson.Document;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.mongodb.client.model.Updates.set;

public class ScheduledFetch extends Service {

    private static final String TAG = "ScheduledFetch";
    public static final int notify = 5000;  //interval between two services(Here Service run every 5 Minute=300000)
    private Timer mTimer = null;    //timer handling
    private Handler mHandler = new Handler();//
    private Context mContext;
    private MongoDbSetup mMongoDbSetup;
    private StitchUser mStitchUser;
    private StitchAuth mStitchAuth;

    public ScheduledFetch(StitchAuth auth, StitchUser user) {
        this.mStitchAuth = auth;
        this.mStitchUser = user;
    }

    public ScheduledFetch() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        startService(intent);
        Log.d(TAG, "onBind: intent: " + intent.getAction());
        Log.d(TAG, "onBind: intent: " + intent.getDataString());
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        startService(intent);
        Log.d(TAG, "onRebind: ");
        super.onRebind(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.purge();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        mTimer.purge();
    }

    @Override
    public void onCreate() {
        if (mTimer != null) {
            mTimer.purge();
        } else
            mTimer = new Timer();   //recreate new
        mTimer.scheduleAtFixedRate(new ClientService(), 0, notify);   //Schedule task
        mMongoDbSetup = HomeActivity.getMongoDbSetup();
    }

    //class ClientService for handling task
    class ClientService extends TimerTask {
        private static final String TAG = "ClientService";

        private Document mDocument;

        public Document getDocument() {
            return mDocument;
        }

        public void setDocument(Document document) {
            mDocument = document;
        }

        public ClientService() {
        }

        @Override
        public void run() {

            Log.d(TAG, "run: is running");
            try {

                String user_id = mMongoDbSetup.getStitchUser().getId();
                RemoteMongoCollection plantProfile_coll = mMongoDbSetup.getCollectionByName(getResources().getString(R.string.eye_plant_plant_profiles));

                plantProfile_coll.findOne(new Document("user_id", user_id)).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Document document = (Document) task.getResult();
                        setDocument(document);
                        return;

                    }
                }).addOnFailureListener(e ->
                        Log.d(TAG, "fetchPlantData error: " + e.getMessage()));

                if (getDocument().size() != 0) {
                    Document doc = getDocument();
                    Log.d(TAG, "run: doc.size " + doc.size());
                    final int serverPort = 2390;
                    final String ip = "172.20.10.13";
                    InetAddress serverIp = InetAddress.getByName(ip);
                    DatagramSocket socket = new DatagramSocket();
                    socket.setReuseAddress(true);
                    socket.setBroadcast(true);
                    socket.connect(serverIp, serverPort);

                    ArrayList<Integer> hum = doc.get("humidity", ArrayList.class);
                    ArrayList<Integer> temp = doc.get("temperature", ArrayList.class);
                    ArrayList<Integer> light = doc.get("sunlight", ArrayList.class);
                    byte[] dataTosend;
                    byte[] receivedData = new byte[512];
                    int hum_min = hum.get(0);
                    int hum_max = hum.get(1);

                    int tem_min = temp.get(0);
                    int tem_max = temp.get(1);
                    int light_min = light.get(0);
                    int light_max = light.get(1);

                    byte hum_mi = (byte) hum_min;
                    byte hum_ma = (byte) hum_max;
                    byte tem_mi = (byte) tem_min;
                    byte tem_ma = (byte) tem_max;
                    byte light_mi = (byte) light_min;
                    byte light_ma = (byte) light_max;
                    dataTosend = new byte[]{hum_mi, hum_ma, tem_mi, tem_ma, light_mi, light_ma};

                    DatagramPacket send_packet = new DatagramPacket(dataTosend, dataTosend.length);//sending
                    DatagramPacket recieved_packet = new DatagramPacket(receivedData, receivedData.length);//receiving
                    socket.send(send_packet);
                    socket.receive(recieved_packet);
                    byte[] bytes = recieved_packet.getData();

                    int mea_hum = bytes[0];
                    int mea_light = bytes[1];
                    int mea_tem = bytes[2];

                    plantProfile_coll.updateOne(null, set("measured_humidity", mea_hum), new RemoteUpdateOptions());
                    plantProfile_coll.updateOne(null, set("measured_temperature", mea_tem), new RemoteUpdateOptions());
                    plantProfile_coll.updateOne(null, set("measured_sunlight", mea_light), new RemoteUpdateOptions());
                    mTimer.purge();//purge it to start over again
                }

            } catch (Exception e) {
                Log.d(TAG, "fetchPlantData error: " + e.getMessage());
            }


        }
    }
}
