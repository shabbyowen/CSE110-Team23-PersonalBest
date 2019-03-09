package com.cse110.personalbest.Factories;

import com.cse110.personalbest.Services.GoogleStepService;
import com.cse110.personalbest.Services.MockStepService;

public class StepServiceSelector implements ServiceSelector {

    public static final String GOOGLE_STEP_SERVICE_KEY = "google_step_service_key";
    public static final String MOCK_STEP_SERVICE_KEY = "mock_step_service_key";

    @Override
    public Class retrieveServiceClass(String key) {
        switch (key) {
            case GOOGLE_STEP_SERVICE_KEY:
                return GoogleStepService.class;
            case MOCK_STEP_SERVICE_KEY:
                return MockStepService.class;
            default:
                return null;
        }
    }
}
