package com.cse110.personalbest.Activities;

import android.content.Intent;
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
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.Task;

public class GoogleSignInActivity extends AppCompatActivity {
    private static final String TAG = "GoogleSigninActivity";

    // Storage Key
    private static final String USER_EMAIL = "user_email";
    private static final String USER_DISPLAY_NAME = "user_name";

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

            startHomeScreenActivity();
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }
}
