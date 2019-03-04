package com.cse110.personalbest.Services;

import android.app.Service;
import com.cse110.personalbest.Events.FriendServiceCallback;

public abstract class FriendService extends Service {
    public static final String STORAGE_SOLUTION_KEY_EXTRA = "storage_solution_key_extra";
    public static final String FRIEND_SERVICE_KEY_EXTRA = "friend_service_key_extra";

    public abstract void getPendingRequests(FriendServiceCallback callback);
}
