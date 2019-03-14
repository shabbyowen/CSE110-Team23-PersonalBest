package com.cse110.personalbest.Utilities;

import com.cse110.personalbest.Services.BasicSessionService;
import com.cse110.personalbest.Services.GoogleStepService;

public class MockStorageSolution implements StorageSolution {

    @Override
    public void put(String key, String value) {

    }

    @Override
    public String get(String key, String defaultVal) {
        if (key.equals(GoogleStepService.DAILY_GOAL)) {
            return "[{\"goal\":123,\"time\":0},{\"goal\":321,\"time\":1}]";
        } else if (key.equals(BasicSessionService.SESSION_LIST)) {
            return "[{\"deltaStep\":0,\"deltaTime\":2179,\"startStep\":0,\"startTime\":1551041720736},{\"deltaStep\":0,\"deltaTime\":3728,\"startStep\":0,\"startTime\":1551217715222},{\"deltaStep\":0,\"deltaTime\":4283,\"startStep\":0,\"startTime\":1551217721735}]";
        }
        return defaultVal;
    }

    @Override
    public void put(String key, Long value) {

    }

    @Override
    public Long get(String key, Long defaultVal) {
        return defaultVal;
    }

    @Override
    public void put(String key, Integer value) {

    }

    @Override
    public Integer get(String key, Integer defaultVal) {
        return defaultVal;
    }
}
