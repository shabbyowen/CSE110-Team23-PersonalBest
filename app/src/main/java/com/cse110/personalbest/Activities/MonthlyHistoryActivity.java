package com.cse110.personalbest.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.cse110.personalbest.Events.FriendServiceCallback;
import com.cse110.personalbest.Events.MonthlyProgressFragmentInfo;
import com.cse110.personalbest.Events.MyBinder;
import com.cse110.personalbest.Events.WeeklyProgressFragmentInfo;
import com.cse110.personalbest.Factories.FriendServiceSelector;
import com.cse110.personalbest.Factories.MonthlyProgressFragmentFactory;
import com.cse110.personalbest.Factories.ServiceSelector;
import com.cse110.personalbest.Factories.StorageSolutionFactory;
import com.cse110.personalbest.Fragments.MonthlyProgressFragment;
import com.cse110.personalbest.R;
import com.cse110.personalbest.Services.FriendService;

import java.util.Arrays;

public class MonthlyHistoryActivity extends AppCompatActivity {
    public static final String FRIEND_SERVICE_KEY_EXTRA = "friend_service_key_extra";
    private static final String SENDER = "sender";
    private static final String RECEIVER = "receiver";
    private static final String TAG = "MonthlyHistoryActivity";

    MonthlyProgressFragment monthlyProgressFragment;

    private TextView titleTextView;
    private Button sendMessageBtn;
    private EditText sendMessageEditText;
    private String sender;
    private String receiver;

    private String friendServiceKey = FriendServiceSelector.BASIC_FRIEND_SERVICE_KEY;
    private FriendService friendService;
    private boolean activityInitialized;

    // service connection for friend service
    private ServiceConnection friendServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder binder = (MyBinder) service;
            friendService = (FriendService) binder.getService();

            // update friend's monthly activity
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            friendService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_history);

        // Get extra info
        // retrieve the step service key
        Intent intent = getIntent();
        String key1 = intent.getStringExtra(FRIEND_SERVICE_KEY_EXTRA);
        if (key1 != null) {
            friendServiceKey = key1;
        }
        sender = intent.getStringExtra(SENDER);
        receiver = intent.getStringExtra(RECEIVER);

        // Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Bind friend service
        bindService(getFriendServiceIntent(), friendServiceConnection, Context.BIND_AUTO_CREATE);

        // Setup UI elements
        titleTextView = findViewById(R.id.tv_monthly_history);
        sendMessageEditText = findViewById(R.id.et_send_message);
        sendMessageBtn = findViewById(R.id.btn_send_message);
        titleTextView.setText("Monthly Progress for " + receiver);

        // Setup Listeners
        sendMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    sendMessageBtn.setEnabled(false);
                } else {
                    sendMessageBtn.setEnabled(true);
                }
            }
        });
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageBtn.setEnabled(false);
                String message = sendMessageEditText.getText().toString();
                sendMessage(receiver, message);
            }
        });

        monthlyProgressFragment = (MonthlyProgressFragment) new MonthlyProgressFragmentFactory()
                .create(MonthlyProgressFragmentFactory.BASIC_WEEKLY_PROGRESS_FRAGMENT_KEY);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
//        ft.add(R.id.monthly_history_container, monthlyProgressFragment);
        ft.commit();

        //updateMonthlyProgressFragment();
        activityInitialized = true;
    }

    private Intent getFriendServiceIntent() {
        ServiceSelector serviceSelector = new FriendServiceSelector();
        Intent intent = new Intent(this, serviceSelector.retrieveServiceClass(friendServiceKey));
        intent.putExtra(FriendService.STORAGE_SOLUTION_KEY_EXTRA, StorageSolutionFactory.SHARED_PREF_KEY);
        return intent;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!activityInitialized) {
            return;
        }

        // bind to the service
        bindService(getFriendServiceIntent(), friendServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "Bind to friend service");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // unbind the service
        //unbindService(friendServiceConnection);
        Log.d(TAG, "Unbind friend service");
    }

    public void updateMonthlyProgressFragment() {
        MonthlyProgressFragmentInfo info = new MonthlyProgressFragmentInfo();

        info.week1Info = new WeeklyProgressFragmentInfo();
        info.week1Info.intentionalSteps = Arrays.asList(3000, 3000, 5000, 8000, 1000, 2000, 5000);
        info.week1Info.unintentionalSteps = Arrays.asList(0, 2000, 1000, 200, 1000, 5000, 1000);
        info.week1Info.weekGoal = Arrays.asList(3000, 3500, 4000, 4500, 5000, 5500, 6000);
        info.week1Info.weekSpeed = Arrays.asList(14, 13, 12, 14, 13, 12, 14);

        info.week2Info = info.week1Info;
        info.week3Info = info.week1Info;
        info.week4Info = info.week1Info;

        /*
        info.week2Info = new WeeklyProgressFragmentInfo();
        info.week2Info.intentionalSteps = Arrays.asList();
        info.week2Info.unintentionalSteps = Arrays.asList();
        info.week2Info.weekGoal = Arrays.asList();
        info.week2Info.weekSpeed = Arrays.asList();

        info.week3Info = new WeeklyProgressFragmentInfo();
        info.week3Info.intentionalSteps = Arrays.asList();
        info.week3Info.unintentionalSteps = Arrays.asList();
        info.week3Info.weekGoal = Arrays.asList();
        info.week3Info.weekSpeed = Arrays.asList();

        info.week4Info = new WeeklyProgressFragmentInfo();
        info.week4Info.intentionalSteps = Arrays.asList();
        info.week4Info.unintentionalSteps = Arrays.asList();
        info.week4Info.weekGoal = Arrays.asList();
        info.week4Info.weekSpeed = Arrays.asList();
        */
    }

    @Override
    public boolean onSupportNavigateUp() {
        unbindService(friendServiceConnection);
        finish();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            unbindService(friendServiceConnection);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void sendMessage(String receiver, String message) {
        if (friendService == null) {
            Log.d(TAG, "Send Message to a friend failed: friendService is null");
            return;
        }
        friendService.sendMessage(receiver, message, new FriendServiceCallback() {
            @Override
            public void onSendMessageResult(boolean hasSendMessageSuccess){
                if (hasSendMessageSuccess) {
                    sendMessageEditText.setText("");
                    Toast.makeText(MonthlyHistoryActivity.this, R.string.send_message_success, Toast.LENGTH_LONG).show();
                } else {
                    sendMessageBtn.setEnabled(true);
                    Toast.makeText(MonthlyHistoryActivity.this, R.string.send_message_fail, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "back button pressed");
        Intent intent = new Intent(MonthlyHistoryActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
