package com.cse110.personalbest.Factories;

import com.cse110.personalbest.Services.BasicSessionService;

public class SessionServiceSelector implements ServiceSelector {

    public static final String BASIC_SESSION_SERVICE_KEY = "basic_session_service_key";

    @Override
    public Class retrieveServiceClass(String key) {
        switch (key) {
            case BASIC_SESSION_SERVICE_KEY:
                return BasicSessionService.class;
            default:
                return null;
        }
    }
}
