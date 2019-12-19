package com.example.aiplant.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.aiplant.R;
import com.example.aiplant.home.HomeActivity;
import com.example.aiplant.utility_classes.MongoDbSetup;
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
    public static final int notify = 10000 ;  //interval between two services 180 minutes =  60000 * 180
    private Timer mTimer = null;    //timer handling
    private Handler mHandler = new Handler();//
    private MongoDbSetup mMongoDbSetup;

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

    class ClientService extends TimerTask {  //class ClientService for handling task
        private static final String TAG = "ClientService";
        private Document mDocument;
        private Document getDocument() {
            return mDocument;
        }
        private void setDocument(Document document) {
            mDocument = document;
        }
        @Override
        public void run() {
            Log.d(TAG, "run: is running");
            try {
                String user_id = mMongoDbSetup.getStitchUser().getId();
                RemoteMongoCollection plantProfile_coll = mMongoDbSetup.getCollection(getResources().getString(R.string.eye_plant_plant_profiles));//<-Fetching data from database->
                plantProfile_coll.findOne(new Document("user_id", user_id)).continueWithTask(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {  //if there is any corresponding document, we fetched it
                                Document document = (Document) task.getResult();
                                setDocument(document);
                                return task.getResult(); // we return to stop the task.
                            } else
                                return task.getException(); // if no match we just return the collection back with the task.
                        }).addOnFailureListener(e ->
                                Log.d(TAG, "fetchPlantData error: " + e.getMessage()));//<-catching the  database error to prevent app crush->
                if (getDocument().size() > 0) {
                    Document doc = getDocument();
                    Log.d(TAG, "run: doc.size " + doc.size());
                    final int serverPort = 2390;
                    final String ip = "172.20.10.13";
                    InetAddress serverIp = InetAddress.getByName(ip);//<- Client-Server Communication setup->
                    DatagramSocket socket = new DatagramSocket();
                    socket.setReuseAddress(true); // we set reuse to true to avoid the AddressAlreadyInUse exception and keep the connection steady
                    socket.setBroadcast(true);
                    socket.connect(serverIp, serverPort);//<-connection request to server->
                    ArrayList<Integer> hum = doc.get("humidity", ArrayList.class);//<-Beginning of extracting data from fetched document from database->
                    ArrayList<Integer> temp = doc.get("temperature", ArrayList.class);
                    ArrayList<Integer> light = doc.get("sunlight", ArrayList.class);
                    byte[] dataTosend, receivedData = new byte[512]; //
                    int hum_min = hum.get(0), hum_max = hum.get(1), tem_min = temp.get(0);
                    int tem_max = temp.get(1), light_min = light.get(0), light_max = light.get(1);
                    byte hum_mi = (byte) hum_min, hum_ma = (byte) hum_max, tem_mi = (byte) tem_min, tem_ma = (byte) tem_max, light_mi = (byte) light_min, light_ma = (byte) light_max;
                    ;//<-End of extracting data from fetched document from database->
                    dataTosend = new byte[]{hum_mi, hum_ma, tem_mi, tem_ma, light_mi, light_ma};// <--prepare packet to send--->
                    DatagramPacket send_packet = new DatagramPacket(dataTosend, dataTosend.length);//sending
                    DatagramPacket recieved_packet = new DatagramPacket(receivedData, receivedData.length);//receiving
                    socket.send(send_packet);// <--send--->
                    socket.receive(recieved_packet);// <---receive-->

                    if (recieved_packet.getLength() > 0) {
                        byte[] bytes = recieved_packet.getData();
                        int mea_hum = bytes[0], mea_light = bytes[1], mea_tem = bytes[2];

                        plantProfile_coll.updateOne(null, set("measured_humidity", mea_hum), new RemoteUpdateOptions());// <--update database with the received data--->
                        plantProfile_coll.updateOne(null, set("measured_temperature", mea_tem), new RemoteUpdateOptions());//<--update database with the received data--->
                        plantProfile_coll.updateOne(null, set("measured_sunlight", mea_light), new RemoteUpdateOptions());//<--update database with the received data--->
                        mTimer.purge();//purge it to start over again
                    }
                    else Log.d(TAG, "run: NoTHING RECEIVED????");
                }

            } catch (Exception e) {
                Log.d(TAG, "fetchPlantData error: " + e.getMessage());//<--Catch exception to prevent app crush--->
            }
        }
    }
}
