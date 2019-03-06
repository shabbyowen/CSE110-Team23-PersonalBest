package com.cse110.personalbest.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.cse110.personalbest.Events.*;
import com.cse110.personalbest.Factories.*;
import com.cse110.personalbest.Fragments.DailyGoalFragment;
import com.cse110.personalbest.Fragments.FriendsListFragment;
import com.cse110.personalbest.Fragments.InputDialogFragment;
import com.cse110.personalbest.Fragments.WeeklyProgressFragment;
import com.cse110.personalbest.Friend;
import com.cse110.personalbest.R;
import com.cse110.personalbest.Services.FriendService;
import com.cse110.personalbest.Services.SessionService;
import com.cse110.personalbest.Services.StepService;
import com.cse110.personalbest.Utilities.SpeedCalculator;
import com.cse110.personalbest.Utilities.StorageSolution;
import com.cse110.personalbest.Utilities.TimeMachine;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements
    StepServiceListener, SessionServiceListener, DailyGoalFragmentListener, FriendsListFragmentListener,
    InputDialogFragmentListener {

    private static final String TAG = "HomeActivity";

    // storage key
    private static final String USER_HEIGHT = "user_height";

    // extra string keys
    public static final String STEP_SERVICE_KEY_EXTRA = "step_service_key_extra";
    public static final String SESSION_SERVICE_KEY_EXTRA = "session_service_key_extra";
    public static final String STORAGE_SOLUTION_KEY_EXTRA = "storage_solution_key_extra";

    // factory keys
    private String stepServiceKey = StepServiceSelector.GOOGLE_STEP_SERVICE_KEY;
    private String sessionServiceKey = SessionServiceSelector.BASIC_SESSION_SERVICE_KEY;
    private String friendServiceKey = FriendServiceSelector.BASIC_FRIEND_SERVICE_KEY;
    private String storageSolutionKey = StorageSolutionFactory.SHARED_PREF_KEY;

    private StepService stepService;
    private SessionService sessionService;
    private FriendService friendService;
    private StorageSolution storageSolution;

    private DailyGoalFragment dailyGoalFragment;
    private FriendsListFragment friendsListFragment;
    private WeeklyProgressFragment weeklyProgressFragment;

    private FragmentManager fragmentManager;
    private Fragment currentFragment;

    private FragmentFactory inputDialogFragmentFactory;

    private MenuItem addFriendMenuItem;

    private boolean activityInitialized = false;

    private int height = 70;

    // service connection for step service
    private ServiceConnection stepServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder binder = (MyBinder) service;
            stepService = (StepService) binder.getService();
            stepService.setup(HomeActivity.this);
            stepService.registerListener(HomeActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            // unregister self
            stepService.unregisterListener(HomeActivity.this);
            stepService = null;
        }
    };

    // service connection for session service
    private ServiceConnection sessionServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder binder = (MyBinder) service;
            sessionService = (SessionService) binder.getService();
            sessionService.registerListener(HomeActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sessionService.unregisterListener(HomeActivity.this);
            sessionService = null;
        }
    };

    // service connection for friend service
    private ServiceConnection friendServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder binder = (MyBinder) service;
            friendService = (FriendService) binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            friendService = null;
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
        = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    addFriendMenuItem.setVisible(false);
                    display(dailyGoalFragment);
                    return true;
                case R.id.navigation_friend:
                    addFriendMenuItem.setVisible(true);
                    updateFriendsListFragment();
                    display(friendsListFragment);
                    return true;
                case R.id.navigation_stats:
                    addFriendMenuItem.setVisible(false);
                    updateWeeklyProgressFragment();
                    display(weeklyProgressFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initHomeScreenActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        addFriendMenuItem = menu.findItem(R.id.action_add_friend);
        addFriendMenuItem.setVisible(false);
        return true;
    }

    private void initHomeScreenActivity() {
        setContentView(R.layout.activity_home);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(myToolbar);

        // get the storage solution
        storageSolution = StorageSolutionFactory.create(storageSolutionKey, this);

        // retrieve the step service key
        Intent intent = getIntent();
        String key1 = intent.getStringExtra(STEP_SERVICE_KEY_EXTRA);
        if (key1 != null) {
            stepServiceKey = key1;
        }

        // retrieve the storage solution key
        String key2 = intent.getStringExtra(STORAGE_SOLUTION_KEY_EXTRA);
        if (key2 != null) {
            storageSolutionKey = key2;
        }

        // retrieve the session service key
        String key3 = intent.getStringExtra(SESSION_SERVICE_KEY_EXTRA);
        if (key3 != null) {
            sessionServiceKey = key3;
        }

        // start the services
        startService(getStepServiceIntent());
        startService(getSessionServiceIntent());
        startService(getFriendServiceIntent());


        // ui init
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // creating daily goal fragment
        dailyGoalFragment = (DailyGoalFragment) new DailyGoalFragmentFactory()
            .create(DailyGoalFragmentFactory.BASIC_DAILY_GOAL_FRAGMENT_KEY);
        dailyGoalFragment.setListener(this);

        // creating friends list fragment
        friendsListFragment = (FriendsListFragment) new FriendsListFragmentFactory()
            .create(FriendsListFragmentFactory.BASIC_FRIENDS_LIST_FRAGMENT_KEY);
        friendsListFragment.setListener(this);

        // creating weekly progress fragment
        weeklyProgressFragment = (WeeklyProgressFragment) new WeeklyProgressFragmentFactory()
            .create(WeeklyProgressFragmentFactory.BASIC_WEEKLY_PROGRESS_FRAGMENT_KEY);

        // get fragment manager and add fragment
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        currentFragment = dailyGoalFragment;
        ft.add(R.id.home_screen_container, dailyGoalFragment);
        ft.add(R.id.home_screen_container, weeklyProgressFragment).hide(weeklyProgressFragment);
        ft.add(R.id.home_screen_container, friendsListFragment).hide(friendsListFragment);
        ft.commit();

        // set up factory
        inputDialogFragmentFactory = new InputDialogFragmentFactory();

        // check if we have height stored
        height = storageSolution.get(USER_HEIGHT, -1);
        if (height < 0) {
            height = 70;
            showInputDialog(
                InputDialogFragmentFactory.HEIGHT_INPUT_DIALOG_FRAGMENT_KEY,
                "height_input_dialog",
                null);
        }
        activityInitialized = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!activityInitialized) {
            return;
        }

        // bind to the service
        bindService(getStepServiceIntent(), stepServiceConnection, Context.BIND_AUTO_CREATE);
        bindService(getSessionServiceIntent(), sessionServiceConnection, Context.BIND_AUTO_CREATE);
        bindService(getFriendServiceIntent(), friendServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "Bind to step service");
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!activityInitialized) {
            return;
        }

        // unbind the service
        sessionService.saveNow();
        unbindService(stepServiceConnection);
        unbindService(sessionServiceConnection);
        unbindService(friendServiceConnection);
        Log.d(TAG, "Unbind step service");
    }

    private Intent getStepServiceIntent() {
        ServiceSelector serviceSelector = new StepServiceSelector();
        Intent intent = new Intent(this, serviceSelector.retrieveServiceClass(stepServiceKey));
        intent.putExtra(StepService.STORAGE_SOLUTION_KEY_EXTRA, StorageSolutionFactory.SHARED_PREF_KEY);
        return intent;
    }

    private Intent getSessionServiceIntent() {
        ServiceSelector serviceSelector = new SessionServiceSelector();
        Intent intent = new Intent(this, serviceSelector.retrieveServiceClass(sessionServiceKey));
        intent.putExtra(SessionService.STORAGE_SOLUTION_KEY_EXTRA, StorageSolutionFactory.SHARED_PREF_KEY);
        return intent;
    }

    private Intent getFriendServiceIntent() {
        ServiceSelector serviceSelector = new FriendServiceSelector();
        Intent intent = new Intent(this, serviceSelector.retrieveServiceClass(friendServiceKey));
        intent.putExtra(FriendService.STORAGE_SOLUTION_KEY_EXTRA, StorageSolutionFactory.SHARED_PREF_KEY);
        return intent;
    }

    private void display(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // dont switch if the new fragment is the same
        if (currentFragment == fragment) {
            return;
        }

        // hide old fragment
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        // add the new fragment and commit
        fragmentTransaction.show(fragment);
        currentFragment = fragment;
        fragmentTransaction.commit();
    }

    @Override
    public void onStepChanged(int step) {
        if (dailyGoalFragment != null) {
            DailyGoalFragmentInfo info = new DailyGoalFragmentInfo();
            double dist = SpeedCalculator.stepToMiles(step, height);
            info.step = step;
            info.currentDist = dist;
            dailyGoalFragment.updateView(info);
        }
    }

    @Override
    public void onGoalChanged(int goal) {
        if (dailyGoalFragment != null) {
            DailyGoalFragmentInfo info = new DailyGoalFragmentInfo();
            double dist = SpeedCalculator.stepToMiles(goal, height);
            info.goal = goal;
            info.goalDist = dist;
            dailyGoalFragment.updateView(info);
        }
    }

    @Override
    public void onGoalMet() {
        showInputDialog(
            InputDialogFragmentFactory.GOAL_MET_INPUT_DIALOG_FRAGMENT_KEY,
            "goal_met_input_dialog",
            null);
    }

    @Override
    public void onEncouragement() {

    }

    @Override
    public void onRecordBtnClicked() {
        if (sessionService != null) {
            DailyGoalFragmentInfo info = new DailyGoalFragmentInfo();
            if (sessionService.isWorkingOut()) {
                info.isWorkingOut = false;
                sessionService.endSession();
            } else {
                info.isWorkingOut = true;
                sessionService.startSession();
            }
            dailyGoalFragment.updateView(info);
        }
    }

    @Override
    public void onChangeGoalBtnClicked() {
        showInputDialog(
            InputDialogFragmentFactory.GOAL_INPUT_DIALOG_FRAGMENT_KEY,
            "goal_input_dialog",
            null);
    }

    @Override
    public void onCurrentSessionUpdate(Session session) {
        // session can be null, which means that no session is active
        if (dailyGoalFragment != null) {
            DailyGoalFragmentInfo info = new DailyGoalFragmentInfo();
            if (session != null) {
                info.sessionTime = TimeMachine.formatTime((int) session.deltaTime / 1000);
                info.sessionStep = session.deltaStep;
                info.sessionSpeed = SpeedCalculator.calculateSpeed(session.deltaStep, (int) session.deltaTime, height);
                info.isWorkingOut = true;
            } else {
                info.isWorkingOut = false;
            }
            dailyGoalFragment.updateView(info);
        }
    }

    @Override
    public boolean onInputDialogResult(Object result, InputDialogFragment dialog) {
        if (result instanceof GoalInputDialogResult) {

            // change the goal
            int goal = ((GoalInputDialogResult) result).goal;
            if (goal <= 0) {
                dialog.setPrompt(getString(R.string.change_goal_instruction_failed));
                return false;
            } else {
                stepService.setGoal(goal);
                return true;
            }
        } else if (result instanceof HeightInputDialogResult) {

            // save the height
            HeightInputDialogResult heightResult = ((HeightInputDialogResult) result);
            int height = heightResult.foot * 12 + heightResult.inch;
            if (height < 0) {
                dialog.setPrompt(getString(R.string.height_instruction_failed));
                return false;
            } else {
                storageSolution.put(USER_HEIGHT, height);
                this.height = height;
                Toast.makeText(this, R.string.saved, Toast.LENGTH_LONG).show();
                return true;
            }
        } else if (result instanceof GoalMetInputDialogResult) {

            // check should update the goal
            GoalMetInputDialogResult goalMetResult = ((GoalMetInputDialogResult) result);
            if (goalMetResult.good) {
                stepService.getTodayGoal(new StepServiceCallback() {
                    @Override
                    public void onGoalResult(List<Integer> result) {
                        int goal = result.get(0);
                        stepService.setGoal(goal + 500);
                    }
                });
            }
            return true;
        } else if (result instanceof AddFriendInputDialogResult) {
           AddFriendInputDialogResult addFriendInputDialogResult = ((AddFriendInputDialogResult) result);
           friendService.sendFriendRequest(addFriendInputDialogResult.userEmail, new FriendServiceCallback(){
               @Override
               public void onSendFriendRequestResult(int statusCode) {
                   switch (statusCode) {
                       case FriendServiceCallback.OPERATION_SUCCESS:
                           Toast.makeText(HomeActivity.this, R.string.friend_request_send_success,Toast.LENGTH_LONG).show();
                           break;
                       case FriendServiceCallback.USER_DOES_NOT_EXIST:
                           Toast.makeText(HomeActivity.this, R.string.friend_request_user_nonexist,Toast.LENGTH_LONG).show();
                           break;
                       case FriendServiceCallback.NO_INTERNET_CONNECTION:
                           Toast.makeText(HomeActivity.this, R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                           break;
                       case FriendServiceCallback.USER_ALREADY_FRIEND:
                           Toast.makeText(HomeActivity.this, R.string.friend_already_exists,Toast.LENGTH_LONG).show();
                           break;
                   }
               }
           });
           return true;
        } else {
            Log.w(TAG, "Unhandled input dialog result");
            return true;
        }
    }

    public void showInputDialog(String key, String tag, String prompt) {
        InputDialogFragment dialog = (InputDialogFragment) inputDialogFragmentFactory.create(key);
        dialog.registerListener(this);
        if (prompt != null) {
            dialog.setPrompt(prompt);
        }
        dialog.show(fragmentManager, tag);
    }

    public void updateWeeklyProgressFragment() {
        if (weeklyProgressFragment == null) {
            return;
        }

        sessionService.getWeekSession(new SessionServiceCallback() {
            @Override
            public void onSessionResult(List<Session> result) {

                final List<Session> sessionList = result;

                stepService.getWeekStep(new StepServiceCallback(){
                    @Override
                    public void onStepResult(List<Integer> result) {

                        final List<Integer> totalStepList = result;

                        stepService.getWeekGoal(new StepServiceCallback() {
                            @Override
                            public void onGoalResult(List<Integer> result) {

                                List<Integer> goalList = result;
                                List<Integer> intentionalStep = new LinkedList<>();
                                List<Integer> unintentionalStep = new LinkedList<>();
                                List<Integer> speed = new LinkedList<>();
                                for (int i = 0; i < sessionList.size(); i++) {
                                    int intentional = sessionList.get(i).deltaStep;
                                    int time = (int)sessionList.get(i).deltaTime;
                                    int total = totalStepList.get(i);
                                    intentionalStep.add(intentional);
                                    unintentionalStep.add(total - intentional);
                                    speed.add((int)Math.round(SpeedCalculator.calculateSpeed(intentional, time, height)));
                                }
                                WeeklyProgressFragmentInfo info = new WeeklyProgressFragmentInfo();
                                info.intentionalSteps = intentionalStep;
                                info.unintentionalSteps = unintentionalStep;
                                info.weekGoal = goalList;
                                info.weekSpeed = speed;
                                weeklyProgressFragment.updateView(info);
                            }
                        });
                    }
                });
            }
        });
    }

    public void updateFriendsListFragment() {
        friendService.getPendingRequests(new FriendServiceCallback() {
            @Override
            public void onPendingRequestsResult(List<Friend> result) {
                FriendsListFragmentInfo info = new FriendsListFragmentInfo();
                info.pendingFriends = result;

                friendService.getFriendList(new FriendServiceCallback() {
                    @Override
                    public void onFriendsListResult(List<Friend> result) {
                        info.friends = result;
                        friendsListFragment.updateView(info);
                    }
                });
            }
        });
    }

    @Override
    public void onAcceptButtonClicked(Friend friend) {
        if (friendService == null) {
            Log.d(TAG, "Accept friend request failed: friendService is null");
            return;
        }
        friendService.addFriend(friend, new FriendServiceCallback() {
            @Override
            public void onAcceptFriendResult(boolean hasAcceptSuccess) {
                if (hasAcceptSuccess) {
                    Toast.makeText(HomeActivity.this, R.string.friend_request_accept_success,Toast.LENGTH_LONG).show();
                    updateFriendsListFragment();
                } else {
                    Toast.makeText(HomeActivity.this, R.string.friend_request_accept_fail,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onRejectButtonClicked(Friend rejectedFriend) {
        if (friendService == null) {
            Log.d(TAG, "Reject friend request failed: friendService is null");
            return;
        }
        friendService.rejectFriend(rejectedFriend, new FriendServiceCallback() {
            @Override
            public void onRejectFriendResult(boolean hasAcceptSuccess) {
                if (hasAcceptSuccess) {
                    Toast.makeText(HomeActivity.this, R.string.friend_request_reject_success,Toast.LENGTH_LONG).show();
                    updateFriendsListFragment();
                } else {
                    Toast.makeText(HomeActivity.this, R.string.friend_request_reject_fail,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onEditNicknameButtonClicked(Friend friend) {

    }

    @Override
    public void onRemoveButtonClicked(Friend removedFriend) {
        if (friendService == null) {
            Log.d(TAG, "Remove friend from list failed: friendService is null");
            return;
        }
        friendService.removeFriend(removedFriend, new FriendServiceCallback() {
            @Override
            public void onRemoveFriendResult(boolean hasRemoveSuccess) {
                if (hasRemoveSuccess) {
                    Toast.makeText(HomeActivity.this, R.string.friend_remove_success, Toast.LENGTH_LONG).show();
                    updateFriendsListFragment();
                } else {
                    Toast.makeText(HomeActivity.this, R.string.friend_remove_fail, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_friend:
                showInputDialog(
                        InputDialogFragmentFactory.ADD_FRIEND_INPUT_DIALOG_FRAGMENT_KEY,
                        "add_friend_input_dialog",
                        null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
