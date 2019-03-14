package com.cse110.personalbest.Activities;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.cse110.personalbest.Events.FriendServiceCallback;
import com.cse110.personalbest.Events.MyBinder;
import com.cse110.personalbest.Events.WeeklyProgressFragmentInfo;
import com.cse110.personalbest.Factories.FriendServiceSelector;
import com.cse110.personalbest.Fragments.MonthlyProgressFragment;
import com.cse110.personalbest.R;
import com.cse110.personalbest.Services.FriendService;

public class ChatHistoryActivity extends AppCompatActivity {
    public static final String FRIEND_SERVICE_KEY_EXTRA = "friend_service_key_extra";
    private static final String CHAT_FRIEND_EMAIL = "chat_friend_email";
    private static final String RECEIVER = "receiver";
    private static final String TAG = "MonthlyHistoryActivity";

    private MonthlyProgressFragment monthlyProgressFragment;
    private WeeklyProgressFragmentInfo friendInfo;

    private TextView titleTextView;
    private Button sendMessageBtn;
    private Button toChatBtn;
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
            friendService.retrieveProgress(receiver, new FriendServiceCallback() {
                @Override
                public void onRetrieveProgressResult(WeeklyProgressFragmentInfo info) {
                    friendInfo = info;
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
    }
}
