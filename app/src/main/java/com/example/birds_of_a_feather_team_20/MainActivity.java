package com.example.birds_of_a_feather_team_20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class MainActivity extends AppCompatActivity {

    private Message profileMessage;

    private MessageListener profileMessageListener;

    private static final boolean showDebugToast = true;

    /**
     * Print a Log message and also display a Toast notification
     * @param str
     */
    private void toastLog(String str) {
        Log.d("MainActivity", str);
        if (!showDebugToast) return;
        runOnUiThread(() -> {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        });
    }

    private void testOnCreate() {
        profileMessage = new Message(MyProfile.singleton(this).serialize().getBytes(StandardCharsets.UTF_8));

        profileMessageListener = new MessageListener() {
            @Override
            public void onFound(final Message message) {
                String msgBody = new String(message.getContent());
                toastLog("Found profile: " + msgBody);
                onFoundProfile(msgBody);
            }

            @Override
            public void onLost(final Message message) {
                String msgBody = new String(message.getContent());
                toastLog("Lost profile: " + msgBody);
            }
        };
    }

    private void subscribe() {
        toastLog("Subscribing to nearby profiles...");
        Nearby.getMessagesClient(this).subscribe(profileMessageListener);
    }
    private void unsubscribe() {
        toastLog("Unsubscribing...");
        Nearby.getMessagesClient(this).unsubscribe(profileMessageListener);
    }
    private void publish() {
        toastLog("Publishing my profile...");
        Nearby.getMessagesClient(this).publish(profileMessage).addOnFailureListener(e ->
                toastLog("API Error!"));
    }
    private void unpublish() {
        toastLog("Unpublishing...");
        Nearby.getMessagesClient(this).unpublish(profileMessage);
    }
    private void onFoundProfile(String profileData) {
        // A profile was received, now process it
    }



    //////// END REFACTORING

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Find Friends");
        MyProfile.singleton(getApplicationContext());
//        refreshProfileListRoutine();
        testOnCreate();
    }

    private void refreshProfileListRoutine() {
        ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

        // Refresh on background thread
        Future<Void> future = backgroundThreadExecutor.submit(() -> {
            basicRecycler = findViewById(R.id.profile_list);
            layoutManager = new LinearLayoutManager(this);
            basicRecycler.setLayoutManager(layoutManager);
            adapter = new ProfilesViewAdapter(NearbyManager.nearbyProfiles);
            basicRecycler.setAdapter(adapter);

//            sendFakeMessage("{\"user_id\":\"fakeid\",\"name\":\"John F. Kennedy\",\"photo_url\":\"https://upload.wikimedia.org/wikipedia/commons/c/c3/John_F._Kennedy,_White_House_color_photo_portrait.jpg\"}");


            while (true) {
                // Keep this loop
                updateThumbnailsBackground();
                runOnUiThread(this::updateList);
                Thread.sleep(3000);

                // JUST FOR TESTING: Send some fake messages for UI testing
//                sendFakeMessage("{\"user_id\":\"fakeid\",\"name\":\"John F. Kennedy\",\"photo_url\":\"https://upload.wikimedia.org/wikipedia/commons/c/c3/John_F._Kennedy,_White_House_color_photo_portrait.jpg\"}");
//                updateThumbnailsBackground();
//                runOnUiThread(this::updateList);
//                Thread.sleep(3000);

//                sendFakeMessage("{\"user_id\":\"fakeid1\",\"name\":\"Barack Obama\",\"photo_url\":\"https://upload.wikimedia.org/wikipedia/commons/8/8d/President_Barack_Obama.jpg\"}");
//                updateThumbnailsBackground();
//                runOnUiThread(this::updateList);
//                Thread.sleep(3000);
//
//                sendFakeMessage("{\"user_id\":\"fakeid2\",\"name\":\"Richard Nixon\",\"photo_url\":\"https://upload.wikimedia.org/wikipedia/commons/2/2c/Richard_Nixon_presidential_portrait_(1).jpg\"}");
            }
        });
    }

    private void sendFakeMessage(String messageStr) {
        Message message = new Message(messageStr.getBytes(StandardCharsets.UTF_8));
        NearbyManager.getMessageListener().onFound(message);
        NearbyManager.getMessageListener().onLost(message);
    }

    public void updateList() {
//        basicRecycler = findViewById(R.id.profile_list);
//        layoutManager = new LinearLayoutManager(this);
//        basicRecycler.setLayoutManager(layoutManager);
        ProfilesViewAdapter.update(this);
        while(!NearbyManager.modifications.isEmpty()) {
            Integer i = NearbyManager.modifications.pop();
            if (i != null)
                adapter.notifyItemChanged(i);
        }
        while(!NearbyManager.additions.isEmpty()) {
            Integer i = NearbyManager.additions.pop();
            if (i != null)
                adapter.notifyItemInserted(i);
        }
//        adapter.notifyDataSetChanged();
//        adapter = new ProfilesViewAdapter(NearbyManager.nearbyProfiles);
//        basicRecycler.setAdapter(adapter);
    }

    void updateThumbnailsBackground() {
        for(Integer i : NearbyManager.modifications) {
            if (i != null)
                NearbyManager.nearbyProfiles.get(i).getThumbnail();
        }
        for(Integer i : NearbyManager.additions) {
            if (i != null)
                NearbyManager.nearbyProfiles.get(i).getThumbnail();
        }
    }


    RecyclerView basicRecycler;
    RecyclerView.LayoutManager layoutManager;

    ProfilesViewAdapter adapter;

    public void onLaunchProfileClicked(View view) {
        Intent intent = new Intent(this, EditProfile.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        Log.i("START", "onStart");
        super.onStart();
//        NearbyManager.startNearby(this);

        // FIXME TESTING
        subscribe();
        publish();
        // END FIXME
//        sendFakeMessage("{\"user_id\":\"fakeid\",\"name\":\"John F. Kennedy\",\"photo_url\":\"https://upload.wikimedia.org/wikipedia/commons/c/c3/John_F._Kennedy,_White_House_color_photo_portrait.jpg\"}");

    }

    @Override
    protected void onStop() {
        Log.i("STOP", "onStop");
//        NearbyManager.stopNearby(this);

        // FIXME TESTING
        unsubscribe();
        unpublish();
        // END FIXME
        super.onStop();
    }
}