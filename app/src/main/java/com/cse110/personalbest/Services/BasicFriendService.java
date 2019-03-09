package com.cse110.personalbest.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.cse110.personalbest.Activities.HomeActivity;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicFriendService extends FriendService {
    private static final String TAG = "BasicFriendService";
    public static final String CURRENT_USER_KEY = "user_email";
    private static final String COLLECTION_USERS_KEY = "users";
    private static final String COLLECTION_CHATS_KEY = "chats";
    private static final String COLLECTION_MESSAGES_KEY = "messages";
    private static final String FRIENDS_KEY = "friends";
    private static final String PENDING_REQUESTS_KEY = "pending_requests";
    private static final String CHATS_KEY = "chats";
    private static final String CHATS_ID_KEY = "chats_id";
    private static final String MESSAGE_FROM_KEY = "from";
    private static final String MESSAGE_TO_KEY = "to";
    private static final String MESSAGE_TEXT_KEY = "text";

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
        DocumentReference userRef = storage.collection(COLLECTION_USERS_KEY).document(userEmail);
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
        DocumentReference userRef = storage.collection(COLLECTION_USERS_KEY).document(userEmail);
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
        DocumentReference userRef = storage.collection(COLLECTION_USERS_KEY).document(userEmail);
        DocumentReference friendRef = storage.collection(COLLECTION_USERS_KEY).document(friend.getEmail());

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
                if (!userFriends.contains(friend.getEmail())) {
                    userFriends.add(friend.getEmail());
                }
                transaction.update(userRef, FRIENDS_KEY, userFriends);

                // Add current user as a friend of the person who sends the request
                List<String> requestFriends = (List<String>) requestFriendSnapshot.get(FRIENDS_KEY);
                if (!requestFriends.contains(userEmail)) {
                    requestFriends.add(userEmail);
                }
                transaction.update(friendRef, FRIENDS_KEY, requestFriends);

                // remove current user from pending lists of target friend if exists
                List<String> targetPending = (List<String>) requestFriendSnapshot.get(PENDING_REQUESTS_KEY);
                targetPending.remove(userEmail);
                transaction.update(friendRef, PENDING_REQUESTS_KEY, targetPending);

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onAcceptFriendResult(true);
                Log.d(TAG, "Add Friend Transaction success!");
                subscribeToNotificationsTopic(friend.getEmail());
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
        DocumentReference userRef = storage.collection(COLLECTION_USERS_KEY).document(userEmail);

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
        DocumentReference userRef = storage.collection(COLLECTION_USERS_KEY).document(userEmail);
        DocumentReference removedFriendRef = storage.collection(COLLECTION_USERS_KEY).document(removedfriend.getEmail());

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

    @Override
    public void sendFriendRequest(String friendToAddEmail, FriendServiceCallback callback) {
        DocumentReference userRef = storage.collection(COLLECTION_USERS_KEY).document(userEmail);
        DocumentReference friendToAddRef = storage.collection(COLLECTION_USERS_KEY).document(friendToAddEmail);
        storage.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot toAddFriendSnapshot = transaction.get(friendToAddRef);

                if (!toAddFriendSnapshot.exists()) {
                    throw new FirebaseFirestoreException("User does not exist", FirebaseFirestoreException.Code.NOT_FOUND);
                } else if (((List<String>) toAddFriendSnapshot.get(PENDING_REQUESTS_KEY)).contains(userEmail)
                    || ((List<String>) toAddFriendSnapshot.get(FRIENDS_KEY)).contains(userEmail)) {
                    throw new FirebaseFirestoreException("Friend Exists", FirebaseFirestoreException.Code.ALREADY_EXISTS);
                } else {
                    // Add current user to target pending requests list
                    List<String> targetPendingList = (List<String>) toAddFriendSnapshot.get(PENDING_REQUESTS_KEY);
                    if (!targetPendingList.contains(userEmail)) {
                        targetPendingList.add(userEmail);
                    }
                    transaction.update(friendToAddRef, PENDING_REQUESTS_KEY, targetPendingList);
                }
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onSendFriendRequestResult(0);
                Log.d(TAG, "Friend Request Send Transaction success!");
                subscribeToNotificationsTopic(friendToAddEmail);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseFirestoreException ffe = (FirebaseFirestoreException) e;

                if (ffe.getCode() == FirebaseFirestoreException.Code.NOT_FOUND) {
                    callback.onSendFriendRequestResult(FriendServiceCallback.USER_DOES_NOT_EXIST);
                } else if (ffe.getCode() == FirebaseFirestoreException.Code.ALREADY_EXISTS) {
                    callback.onSendFriendRequestResult(FriendServiceCallback.USER_ALREADY_FRIEND);
                } else {
                    callback.onSendFriendRequestResult(FriendServiceCallback.NO_INTERNET_CONNECTION);
                }
                Log.w(TAG, "Friend Request Send Transaction failure.", e);
            }
        });
    }

    @Override
    public void sendMessage(String friendEmail, String message, FriendServiceCallback callback) {
        CollectionReference userChatRef = storage.collection(COLLECTION_USERS_KEY).document(userEmail).collection(CHATS_KEY);
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("to", friendEmail);
        chatData.put("content", message);
        userChatRef.add(chatData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                callback.onSendMessageResult(true);
                Log.d(TAG, "Friend Message Send Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onSendMessageResult(false);
                Log.w(TAG, "Friend Message Send Transaction failure.", e);
            }
        });

//        storage.runTransaction(new Transaction.Function<Void>() {
//            @Override
//            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
//
//                DocumentSnapshot userChatIdSnapshot = transaction.get(userChatRef);
//                DocumentSnapshot friendChatIdSnapshot = transaction.get(friendChatRef);
//
//                if (!userChatIdSnapshot.exists() && !friendChatIdSnapshot.exists()) {
//                    transaction.update(userChatIdSnapshot, CHATS_ID_KEY, );
//                } else {
//                    // Add current user to target pending requests list
//                }
//                return null;
//            }
//        });
    }

    public void retrieveMessage(String friendEmail, FriendServiceCallback callback) {
        CollectionReference userChatRef = storage.collection(COLLECTION_USERS_KEY).document(userEmail).collection(CHATS_KEY);
        CollectionReference friendChatRef = storage.collection(COLLECTION_USERS_KEY).document(friendEmail).collection(CHATS_KEY);

        userChatRef.whereEqualTo("to", friendEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> userChatTask) {
                QuerySnapshot userChatQuerySnapshot = userChatTask.getResult();
                List<DocumentSnapshot> userChatDocuments = userChatQuerySnapshot.getDocuments();
                Log.d(TAG, "user chat documents" + userChatDocuments.toString());

                friendChatRef.whereEqualTo("to", userEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> friendChatTask) {
                        QuerySnapshot friendChatQuerySnapshot = friendChatTask.getResult();
                        List<DocumentSnapshot> friendChatDocuments = friendChatQuerySnapshot.getDocuments();
                        Log.d(TAG, "friend chat documents" + friendChatDocuments.toString());



                        // TODO: fix this callback to let it actually return things
                        callback.onRetrieveMessageResult();
                    }
                });
            }
        });
//        friendChatRef.whereEqualTo("to", userEmail).get();
    }


    private void subscribeToNotificationsTopic(String DOCUMENT_KEY) {
        FirebaseMessaging.getInstance().subscribeToTopic(DOCUMENT_KEY)
                .addOnCompleteListener(task -> {
                            String msg = "Subscribed to notifications";
                            if (!task.isSuccessful()) {
                                msg = "Subscribe to notifications failed";
                            }
                            Log.d(TAG, msg);
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                        }
                );
    }
}
