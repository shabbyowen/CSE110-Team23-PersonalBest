package com.cse110.personalbest.Services;

import android.app.Service;

import com.cse110.personalbest.Events.ObservableServiceListener;

import java.util.LinkedList;
import java.util.List;

public abstract class ObservableService extends Service {

    protected List<ObservableServiceListener> listeners = new LinkedList<>();

    public abstract void notifyListener();

    public void registerListener(ObservableServiceListener listener) {

        // prevent repeating add
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unregisterListener(ObservableServiceListener listener) {
        listeners.remove(listener);
    }
}
