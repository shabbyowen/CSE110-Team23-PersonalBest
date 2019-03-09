package com.cse110.personalbest.Events;

import android.widget.TextView;

import com.cse110.personalbest.Fragments.InputDialogFragment;

public interface InputDialogFragmentListener {

    boolean onInputDialogResult(Object result, InputDialogFragment dialog);
}
