package com.android.personalbest.models;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private List<Listener> listeners;

    public interface Listener {
        void onUpdate(Object o);
    }

    public Model() {
        listeners = new ArrayList<>();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void update(Object o) {
        for (Listener listener: listeners) {
            listener.onUpdate(o);
        }
    }
}
