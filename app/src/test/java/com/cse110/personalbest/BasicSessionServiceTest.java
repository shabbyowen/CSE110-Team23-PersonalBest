package com.cse110.personalbest;

import android.content.Intent;

import com.cse110.personalbest.Events.Session;
import com.cse110.personalbest.Events.SessionServiceCallback;
import com.cse110.personalbest.Factories.StepServiceSelector;
import com.cse110.personalbest.Factories.StorageSolutionFactory;
import com.cse110.personalbest.Services.BasicSessionService;
import com.cse110.personalbest.Services.SessionService;
import com.cse110.personalbest.Utilities.TestConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ServiceController;

import java.util.List;

import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
public class BasicSessionServiceTest {

    ServiceController<BasicSessionService> serviceController;

    @Before
    public void setup() {
        TestConfig.isTesting = true;
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), BasicSessionService.class);
        intent.putExtra(SessionService.STEP_SERVICE_KEY_EXTRA, StepServiceSelector.MOCK_STEP_SERVICE_KEY);
        intent.putExtra(SessionService.STORAGE_SOLUTION_KEY_EXTRA, StorageSolutionFactory.MOCK_DICT_KEY);
        serviceController = Robolectric.buildService(BasicSessionService.class, intent);
    }

    @Test
    public void testGetWeekSession() {
        SessionService service = serviceController.create().startCommand(0, 0).get();
        service.getWeekSession(new SessionServiceCallback() {
            @Override
            public void onSessionResult(List<Session> result) {
                System.out.println(result.toString());
            }
        });
    }

}
