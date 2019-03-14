package com.cse110.personalbest.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.cse110.personalbest.*;
import com.cse110.personalbest.Events.FriendServiceCallback;
import com.cse110.personalbest.Events.MyBinder;
import com.cse110.personalbest.Events.WeeklyProgressFragmentInfo;
import com.cse110.personalbest.Factories.FriendServiceSelector;
import com.cse110.personalbest.Factories.ServiceSelector;
import com.cse110.personalbest.Factories.StorageSolutionFactory;
import com.cse110.personalbest.Fragments.MonthlyProgressFragment;
import com.cse110.personalbest.Services.FriendService;

import java.util.ArrayList;
import java.util.List;

public class ChatHistoryActivity extends AppCompatActivity {
    public static final String FRIEND_SERVICE_KEY_EXTRA = "friend_service_key_extra";
    private static final String CHAT_FRIEND_EMAIL = "chat_friend_email";
    private static final String MY_EMAIL = "my_email";
    private static final String TAG = "ChatHistoryActivity";

    private RecyclerView chatMessagesListView;
    private TextView titleTextView;
    private Button sendMessageBtn;
    private EditText sendMessageEditText;
    private String friendEmail;
    private String myEmail;

    private LinearLayoutManager chatMessagesLayoutManager;
    private ChatMessagesListAdapter chatMessagesAdapter;

    private String friendServiceKey = FriendServiceSelector.BASIC_FRIEND_SERVICE_KEY;
    private FriendService friendService;
    private boolean activityInitialized;

    // service connection for friend service
    private ServiceConnection friendServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder binder = (MyBinder) service;
            friendService = (FriendService) binder.getService();

            friendService.retrieveMessage(friendEmail, new FriendServiceCallback() {
                @Override
                public void onRetrieveMessageResult(List<ChatMessage> result) {
                    updateChatMessages(result);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            friendService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        Intent intent = getIntent();
        String key1 = intent.getStringExtra(FRIEND_SERVICE_KEY_EXTRA);
        if (key1 != null) {
            friendServiceKey = key1;
        }
        friendEmail = intent.getStringExtra(CHAT_FRIEND_EMAIL);
        myEmail = intent.getStringExtra(MY_EMAIL);

        // Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Bind friend service
        bindService(getFriendServiceIntent(), friendServiceConnection, Context.BIND_AUTO_CREATE);

        // Setup UI elements
        titleTextView = findViewById(R.id.tv_chat_history_toolbar_title);
        sendMessageEditText = findViewById(R.id.et_send_message);
        sendMessageBtn = findViewById(R.id.btn_send_message);
        titleTextView.setText(friendEmail);

        // Setup Recycler view
        chatMessagesListView = findViewById(R.id.lv_chat_messages);
        chatMessagesAdapter = new ChatMessagesListAdapter(this, new ArrayList<ChatMessage>());
        chatMessagesLayoutManager = new LinearLayoutManager(this);
        chatMessagesLayoutManager.setReverseLayout(true);
        chatMessagesListView.setLayoutManager(chatMessagesLayoutManager);
        chatMessagesListView.setAdapter(chatMessagesAdapter);

        activityInitialized = true;
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

    @Override
    public void onBackPressed() {
        Log.d(TAG, "back button pressed");
        Intent intent = new Intent(ChatHistoryActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private Intent getFriendServiceIntent() {
        ServiceSelector serviceSelector = new FriendServiceSelector();
        Intent intent = new Intent(this, serviceSelector.retrieveServiceClass(friendServiceKey));
        intent.putExtra(FriendService.STORAGE_SOLUTION_KEY_EXTRA, StorageSolutionFactory.SHARED_PREF_KEY);
        return intent;
    }

    private void updateChatMessages(List<ChatMessage> result) {
        chatMessagesAdapter = new ChatMessagesListAdapter(this, result);
        chatMessagesListView.setAdapter(chatMessagesAdapter);
    }
}
