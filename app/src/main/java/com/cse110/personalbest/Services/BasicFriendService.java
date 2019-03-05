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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
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
    private String userEmail;

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
        userEmail = storageSolution.get(CURRENT_USER_KEY, "");

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

    @Override
    public void addFriend(Friend friend, FriendServiceCallback callback) {
        DocumentReference userRef = storage.collection(COLLECTION_KEY).document(userEmail);
        DocumentReference friendRef = storage.collection(COLLECTION_KEY).document(friend.getEmail());

        storage.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot userSnapshot = transaction.get(userRef);
                DocumentSnapshot requestFriendSnapshot = transaction.get(friendRef);

                // Remove friend from pending lists
                List<String> pendingEmails = (List<String>) userSnapshot.get(PENDING_REQUESTS_KEY);
                pendingEmails.remove(friend.getEmail());
                transaction.update(userRef, PENDING_REQUESTS_KEY, pendingEmails);

                // Add this friend to friend lists
                List<String> userFriends = (List<String>) userSnapshot.get(FRIENDS_KEY);
                userFriends.add(friend.getEmail());
                transaction.update(userRef, FRIENDS_KEY, userFriends);

                // Add current user as a friend of the person who sends the request
                List<String> requestFriends = (List<String>) requestFriendSnapshot.get(FRIENDS_KEY);
                requestFriends.add(userEmail);
                transaction.update(friendRef, FRIENDS_KEY, requestFriends);

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onAcceptFriendResult(true);
                Log.d(TAG, "Add Friend Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onAcceptFriendResult(false);
                Log.w(TAG, "Add Friend Transaction failure.", e);
            }
        });
    }

    @Override
    public void rejectFriend(Friend rejectedFriend, FriendServiceCallback callback) {
        DocumentReference userRef = storage.collection(COLLECTION_KEY).document(userEmail);

        storage.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot userSnapshot = transaction.get(userRef);

                // Remove friend from pending lists
                List<String> pendingEmails = (List<String>) userSnapshot.get(PENDING_REQUESTS_KEY);
                pendingEmails.remove(rejectedFriend.getEmail());
                transaction.update(userRef, PENDING_REQUESTS_KEY, pendingEmails);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onRejectFriendResult(true);
                Log.d(TAG, "Remove pending request Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onRejectFriendResult(false);
                Log.w(TAG, "Remove pending request Transaction failure.", e);
            }
        });
    }

    @Override
    public void removeFriend(Friend removedfriend, FriendServiceCallback callback) {
        DocumentReference userRef = storage.collection(COLLECTION_KEY).document(userEmail);
        DocumentReference removedFriendRef = storage.collection(COLLECTION_KEY).document(removedfriend.getEmail());

        storage.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot userSnapshot = transaction.get(userRef);
                DocumentSnapshot toRemoveFriendSnapshot = transaction.get(removedFriendRef);

                // Remove friend from friend list
                List<String> friends = (List<String>) userSnapshot.get(FRIENDS_KEY);
                friends.remove(removedfriend.getEmail());
                transaction.update(userRef, FRIENDS_KEY, friends);

                // Remove user from removed friend's list
                List<String> toRemoveFriends = (List<String>) toRemoveFriendSnapshot.get(FRIENDS_KEY);
                toRemoveFriends.remove(userEmail);
                transaction.update(removedFriendRef, FRIENDS_KEY, toRemoveFriends);

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onRemoveFriendResult(true);
                Log.d(TAG, "Remove friend Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onRemoveFriendResult(false);
                Log.w(TAG, "Remove friend Transaction failure.", e);
            }
        });
    }
}
