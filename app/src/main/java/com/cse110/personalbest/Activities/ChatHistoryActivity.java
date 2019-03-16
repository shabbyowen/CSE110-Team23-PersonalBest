package com.cse110.personalbest.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private static final long UPDATE_DELAY = 5000;

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
    private Handler handler = new Handler();
    private boolean activityInitialized;

    // service connection for friend service
    private ServiceConnection friendServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (name != null && service != null) {
                MyBinder binder = (MyBinder) service;
                friendService = (FriendService) binder.getService();

                friendService.retrieveMessage(friendEmail, new FriendServiceCallback() {
                    @Override
                    public void onRetrieveMessageResult(List<ChatMessage> result) {
                        updateChatMessages(result);
                    }
                });
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            friendService = null;
        }
    };

    private Runnable messageUpdateTask = new Runnable() {
        @Override
        public void run() {
            if (friendService == null) return;
            friendService.retrieveMessage(friendEmail, new FriendServiceCallback() {
                @Override
                public void onRetrieveMessageResult(List<ChatMessage> result) {
                    updateChatMessages(result);
                }
            });
            handler.postDelayed(messageUpdateTask, UPDATE_DELAY);
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Bind friend service
        bindService(getFriendServiceIntent(), friendServiceConnection, Context.BIND_AUTO_CREATE);

        // Setup UI elements
        titleTextView = findViewById(R.id.tv_chat_history_toolbar_title);
        sendMessageEditText = findViewById(R.id.et_send_message);
        sendMessageBtn = findViewById(R.id.btn_send_message);
        titleTextView.setText(friendEmail);

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
                sendMessage(friendEmail, message);
            }
        });

        // Setup Recycler view
        chatMessagesListView = findViewById(R.id.lv_chat_messages);
        chatMessagesAdapter = new ChatMessagesListAdapter(this, new ArrayList<ChatMessage>());
        chatMessagesLayoutManager = new LinearLayoutManager(this);
        chatMessagesListView.setAdapter(chatMessagesAdapter);
        chatMessagesLayoutManager.setStackFromEnd(true);
        chatMessagesListView.setLayoutManager(chatMessagesLayoutManager);

        // Setup Handler
        handler.removeCallbacks(messageUpdateTask);

        activityInitialized = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!activityInitialized) {
            return;
        }

        // bind to the service
        handler.post(messageUpdateTask);
        bindService(getFriendServiceIntent(), friendServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "Bind to friend service");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // unbind the service
        handler.removeCallbacks(messageUpdateTask);
        unbindService(friendServiceConnection);
        Log.d(TAG, "ChatHistoryActivity onPause called");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public void onBackPressed() {
//        Log.d(TAG, "back button pressed");
//        Intent intent = new Intent(ChatHistoryActivity.this, MonthlyHistoryActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//    }

    private Intent getFriendServiceIntent() {
        ServiceSelector serviceSelector = new FriendServiceSelector();
        Intent intent = new Intent(this, serviceSelector.retrieveServiceClass(friendServiceKey));
        intent.putExtra(FriendService.STORAGE_SOLUTION_KEY_EXTRA, StorageSolutionFactory.SHARED_PREF_KEY);
        return intent;
    }

    private void updateChatMessages(List<ChatMessage> result) {
        List<ChatMessage> currentMessages = chatMessagesAdapter.getMessages();
        result.removeAll(currentMessages);
        if (result.isEmpty()) {
            return;
        } else {
            for (ChatMessage msg : result)
                chatMessagesAdapter.addMessage(msg);
            chatMessagesListView.scrollToPosition(chatMessagesAdapter.getItemCount() - 1);
        }
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
                    Toast.makeText(ChatHistoryActivity.this, R.string.send_message_success, Toast.LENGTH_LONG).show();
                    friendService.retrieveMessage(receiver, new FriendServiceCallback() {
                        @Override
                        public void onRetrieveMessageResult(List<ChatMessage> messages) {
                            updateChatMessages(messages);
                        }
                    });
                } else {
                    sendMessageBtn.setEnabled(true);
                    Toast.makeText(ChatHistoryActivity.this, R.string.send_message_fail, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
