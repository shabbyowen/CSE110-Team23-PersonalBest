package com.cse110.personalbest.Services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.cse110.personalbest.Events.MyBinder;
import com.cse110.personalbest.Events.ObservableServiceListener;
import com.cse110.personalbest.Events.Session;
import com.cse110.personalbest.Events.SessionServiceCallback;
import com.cse110.personalbest.Events.SessionServiceListener;
import com.cse110.personalbest.Events.StepServiceCallback;
import com.cse110.personalbest.Events.StepServiceListener;
import com.cse110.personalbest.Factories.ServiceSelector;
import com.cse110.personalbest.Factories.StepServiceSelector;
import com.cse110.personalbest.Utilities.DateCalculator;
import com.cse110.personalbest.Utilities.StorageSolution;
import com.cse110.personalbest.Factories.StorageSolutionFactory;
import com.cse110.personalbest.Utilities.TimeMachine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class BasicSessionService extends SessionService implements StepServiceListener {

    public static final String CURRENT_SESSION = "current_session";
    public static final String SESSION_LIST = "session_list";

    private static final String TAG = "BasicSessionService";
    private static final int UPDATE_DELAY = 1 * 1000;

    private final IBinder binder = new SessionServiceBinder();

    private String storageSolutionKey = StorageSolutionFactory.SHARED_PREF_KEY;
    private String stepServiceKey = StepServiceSelector.GOOGLE_STEP_SERVICE_KEY;
    private StorageSolution storageSolution;
    private Handler handler = new Handler();
    private Session currentSession;
    private StepService stepService;

    private ServiceConnection stepServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder binder = (MyBinder) service;
            stepService = (StepService) binder.getService();
            stepService.registerListener(BasicSessionService.this);

            // reload current session
            currentSession = loadCurrentSession();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            stepService.registerListener(BasicSessionService.this);
            stepService = null;
        }
    };

    private Runnable sessionUpdateTask = new Runnable() {

        @Override
        public void run() {
            notifyListener();
            handler.postDelayed(sessionUpdateTask, UPDATE_DELAY);
        }
    };

    @Override
    public void onStepChanged(int step) {
        if (currentSession != null) {
            currentSession.deltaStep = step - currentSession.startStep;
        }
    }

    public class SessionServiceBinder extends MyBinder {

        @Override
        public Service getService() {
            return BasicSessionService.this;
        }
    }

    public BasicSessionService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // use the correct storage solution base on key
        String key = intent.getStringExtra(STORAGE_SOLUTION_KEY_EXTRA);
        if (key != null) {
            storageSolutionKey = key;
        }
        storageSolution = StorageSolutionFactory.create(storageSolutionKey, this);

        // get the correct service key
        String key2 = intent.getStringExtra(STEP_SERVICE_KEY_EXTRA);
        if (key2 != null) {
            stepServiceKey = key2;
        }
        startService(getStepServiceIntent());
        bindService(getStepServiceIntent(), stepServiceConnection, BIND_AUTO_CREATE);

        // start updating sessions
        handler.removeCallbacks(sessionUpdateTask);
        handler.post(sessionUpdateTask);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // unbind the service
        unbindService(stepServiceConnection);
    }

    private Intent getStepServiceIntent() {
        ServiceSelector serviceSelector = new StepServiceSelector();
        Intent intent = new Intent(this, serviceSelector.retrieveServiceClass(stepServiceKey));
        intent.putExtra(StepService.STORAGE_SOLUTION_KEY_EXTRA, StorageSolutionFactory.SHARED_PREF_KEY);
        return intent;
    }

    @Override
    public void notifyListener() {

        // check for date change before notifying
        if (currentSession != null) {
            Date now = TimeMachine.now();
            Date start = new Date(currentSession.startTime);
            if (!DateCalculator.isSameDate(now, start)) {
                currentSession.deltaTime = DateCalculator.toClosestMidnightTmr(start).getTime() - start.getTime();
                recordSession(currentSession);
                currentSession = null;
            }
        }

        // notify all listeners
        for (ObservableServiceListener listener : listeners) {
            if (listener instanceof SessionServiceListener) {
                SessionServiceListener sessionListener = (SessionServiceListener) listener;
                if (currentSession != null) {
                    currentSession.deltaTime = TimeMachine.nowMillis() - currentSession.startTime;
                }
                sessionListener.onCurrentSessionUpdate(currentSession);
            }
        }
    }

    @Override
    public void startSession() {
        if (currentSession == null) {
            final Session session = new Session();
            stepService.getTodayStep(new StepServiceCallback() {
                @Override
                public void onStepResult(List<Integer> result) {
                    int step = result.get(0);
                    session.startStep = step;
                    session.startTime = TimeMachine.nowMillis();
                    currentSession = session;
                }
            });
        } else {
            Log.e(TAG, "Cannot start session: There is a session running!");
        }
    }

    @Override
    public void endSession() {
        if (currentSession != null) {
            recordSession(currentSession);
            currentSession = null;
        } else {
            Log.e(TAG, "Cannot end session: No session is running!");
        }
    }

    @Override
    public void saveNow() {
        saveCurrentSession(currentSession);
    }

    @Override
    public boolean isWorkingOut() {
        return currentSession != null;
    }

    @Override
    public void getWeekSession(SessionServiceCallback callback) {
        List<Session> sessions = loadSessionList();
        List<Session> result = new LinkedList<>();

        if (sessions == null) {
            return;
        }

        for (int i = 0; i < 7; i++) {
            result.add(new Session());
        }

        // sessions is chronological
        Date now = TimeMachine.now();
        for (int i = sessions.size() - 1; i >= 0; i--) {
            Session session = sessions.get(i);
            Date startTime = new Date(session.startTime);
            int difference = DateCalculator.dateDifference(now, startTime);
            if (difference >= 7) {
                break;
            }
            Session target = result.get(difference);
            target.startTime = session.startTime;
            target.startStep = session.startStep;
            target.deltaTime += session.deltaTime;
            target.deltaStep += session.deltaStep;
        }

        // reverse the order of the list
        Collections.reverse(result);
        callback.onSessionResult(result);
    }

    public void recordSession(Session session) {

        if (session == null) {
            Log.e(TAG, "trying to record a null session!");
            return;
        }

        List<Session> list = loadSessionList();
        if (list == null) {
            list = new LinkedList<>();
        }
        list.add(session);
        saveSessionList(list);
    }

    public List<Session> loadSessionList() {
        Gson gson = new Gson();
        String json = storageSolution.get(SESSION_LIST, "");
        Type type = new TypeToken<List<Session>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveSessionList(List<Session> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        storageSolution.put(SESSION_LIST, json);
    }

    public Session loadCurrentSession() {

        Gson gson = new Gson();
        String json = storageSolution.get(CURRENT_SESSION, "");
        final Session session = gson.fromJson(json, Session.class);

        if (session != null) {
            // check if date changed
            final Date startDate = new Date(session.startTime);
            final Date now = TimeMachine.now();

            // not the same day
            if (!DateCalculator.isSameDate(now, startDate)) {

                // calculate the delta step
                stepService.getWeekStep(new StepServiceCallback() {
                    @Override
                    public void onStepResult(List<Integer> result) {

                        // get the final step count on that day
                        int difference = DateCalculator.dateDifference(now, startDate);
                        if (difference < 7) {
                            session.deltaStep = result.get(result.size() - 1 - difference);
                        } else {
                            /* TODO: fix me, currently if user didn't open the app for seven days, the lasted session step count is lost */
                            session.deltaStep = 0;
                        }

                        // calculate the delta time
                        session.deltaTime = DateCalculator.toClosestMidnightTmr(startDate).getTime() - startDate.getTime();

                        // save this session
                        recordSession(session);
                    }
                });

                // can return null first then async the code above
                return null;

            // still the same day
            } else {
                return session;
            }
        } else {
            return null;
        }
    }

    public void saveCurrentSession(Session session) {

        Log.d(TAG, "Saving current session");

        // save empty string if session is null
        if (session == null) {
            storageSolution.put(CURRENT_SESSION, "");
            return;
        }

        Gson gson = new Gson();
        String json = gson.toJson(session);
        storageSolution.put(CURRENT_SESSION, json);
    }

    // method stubs

    @Override
    public void onGoalChanged(int goal) {
        // does nothing
    }

    @Override
    public void onGoalMet() {
        // does nothing
    }

    @Override
    public void onEncouragement() {
        // does nothing
    }
}
