package com.cse110.personalbest.Events;

import android.app.Service;
import android.os.Binder;

public abstract class MyBinder extends Binder {

    public abstract Service getService();
}
