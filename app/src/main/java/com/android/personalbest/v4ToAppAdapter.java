package com.android.personalbest;

import android.app.Fragment;

public class v4ToAppAdapter extends Fragment {
    android.support.v4.app.Fragment v4Fragment;

    public v4ToAppAdapter() { }

    public void setArguments(android.support.v4.app.Fragment v4Fragment) {
        this.v4Fragment = v4Fragment;
    }

    public android.support.v4.app.Fragment getV4Fragment() {return v4Fragment;}
}
