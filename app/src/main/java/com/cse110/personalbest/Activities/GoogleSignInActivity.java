package com.cse110.personalbest.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.cse110.personalbest.Factories.StorageSolutionFactory;
import com.cse110.personalbest.Utilities.StorageSolution;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GoogleSignInActivity extends AppCompatActivity {
    private static final String TAG = "GoogleSigninActivity";

    // Storage Key
    private static final String USER_EMAIL = "user_email";
    private static final String USER_DISPLAY_NAME = "user_name";
    private static final String COLLECTION_KEY = "users";
    private static final String FRIENDS_KEY = "friends";
    private static final String PENDING_REQUESTS_KEY = "pending_requests";

    // Request Code for sign in action
    private static final int RC_SIGN_IN = 12345;

    private String storageSolutionKey = StorageSolutionFactory.SHARED_PREF_KEY;
    private StorageSolution storageSolution;

    private GoogleSignInAccount account;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the storage solution
        storageSolution = StorageSolutionFactory.create(storageSolutionKey, this);

        /* ----------------------------------------Google Sign in------------------------------------ */
        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestId()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            startHomeScreenActivity();
        }
    }

    private void startHomeScreenActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount googleAccount = completedTask.getResult(ApiException.class);
            storageSolution.put(USER_EMAIL, googleAccount.getEmail());
            storageSolution.put(USER_DISPLAY_NAME, googleAccount.getDisplayName());

            initializeFirebaseStorageForNewUser(googleAccount.getEmail());
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void initializeFirebaseStorageForNewUser(String userEmail) {
        FirebaseFirestore storage = FirebaseFirestore.getInstance();
        DocumentReference userRef = storage.collection(COLLECTION_KEY).document(userEmail);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    // If user does not exist in Firestore, create new skeleton data structure for the user
                    if (!document.exists()) {
                        Log.d(TAG, "Document does not exist!");
                        // Initialize Firestore document for current user
                        Map<String, Object> data = new HashMap<>();
                        data.put(FRIENDS_KEY, new ArrayList<String>());
                        data.put(PENDING_REQUESTS_KEY, new ArrayList<String>());
                        userRef.set(data);
                    }
                    startHomeScreenActivity();
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }
}
