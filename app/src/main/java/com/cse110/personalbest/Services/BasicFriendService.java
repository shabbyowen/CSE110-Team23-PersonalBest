package com.cse110.personalbest.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import com.cse110.personalbest.Events.FriendServiceCallback;
import com.cse110.personalbest.Events.MyBinder;
import com.cse110.personalbest.Factories.StorageSolutionFactory;
import com.cse110.personalbest.Friend;
import com.cse110.personalbest.Utilities.StorageSolution;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class BasicFriendService extends FriendService {
    private static final String TAG = "FRIEND_LOG";
    public static final String CURRENT_USER_KEY = "user_email";
    private static final String COLLECTION_KEY = "users";
    private static final String FRIENDS_KEY = "friends";
    private static final String PENDING_REQUESTS_KEY = "pending_requests";

    private StorageSolution storageSolution;
    private String storageSolutionKey;

    private IBinder binder = new MyBinder() {
        @Override
        public Service getService() {
            return BasicFriendService.this;
        }
    };

    private FirebaseFirestore storage;

    public BasicFriendService() {
        storage = FirebaseFirestore.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // use the correct storage solution base on key
        String key = intent.getStringExtra(STORAGE_SOLUTION_KEY_EXTRA);
        if (key != null) {
            storageSolutionKey = key;
        }
        storageSolution = StorageSolutionFactory.create(storageSolutionKey, this);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void getPendingRequests(FriendServiceCallback callback) {
        String userEmail= storageSolution.get(CURRENT_USER_KEY, "");
        DocumentReference userRef = storage.collection(COLLECTION_KEY).document(userEmail);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    List<String> pendingEmails = (List<String>) document.get(PENDING_REQUESTS_KEY);
                    List<Friend> friends = new ArrayList<>();
                    for (String email : pendingEmails) {
                        friends.add(new Friend(email));
                    }
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                    callback.onPendingRequestsResult(friends);
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void getFriendList(FriendServiceCallback callback) {
        String userEmail= storageSolution.get(CURRENT_USER_KEY, "");
        DocumentReference userRef = storage.collection(COLLECTION_KEY).document(userEmail);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    List<String> pendingEmails = (List<String>) document.get(FRIENDS_KEY);
                    List<Friend> friends = new ArrayList<>();
                    for (String email : pendingEmails) {
                        friends.add(new Friend(email));
                    }
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                    callback.onFriendsListResult(friends);
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
