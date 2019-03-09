package com.cse110.personalbest.Factories;

import com.cse110.personalbest.Services.BasicFriendService;

public class FriendServiceSelector implements ServiceSelector {
    public static final String BASIC_FRIEND_SERVICE_KEY = "basic_friend_service_key";
    public static final String MOCK_FRIEND_SERVICE_KEY = "mock_friend_service_key";

    @Override
    public Class retrieveServiceClass(String key) {
        switch (key) {
            case BASIC_FRIEND_SERVICE_KEY:
                return BasicFriendService.class;
            case MOCK_FRIEND_SERVICE_KEY:
                return MockFriendService.class;
            default:
                return null;
        }

    }
}
