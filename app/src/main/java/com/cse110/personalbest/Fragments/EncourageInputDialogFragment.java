package com.cse110.personalbest.Fragments;

import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cse110.personalbest.R;

public class EncourageInputDialogFragment extends InputDialogFragment {

    private static final String TAG = "EncourageInputDialogFragment";

    private TextView encouragementTextView;
    private Handler handler = new Handler();

    @Override
    public View createView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_encourage_input_dialog, null);
        encouragementTextView = view.findViewById(R.id.fragment_encourage_input_dialog_tv);
        return view;
    }

    @Override
    public String createPositiveBtn() {
        return getString(R.string.ok);
    }

    @Override
    public String createNegativeBtn() {
        return null;
    }

    @Override
    public void onPositiveBtnClicked(DialogInterface dialog, int i) {
        dialog.dismiss();
    }

    @Override
    public void onNegativeBtnClicked(DialogInterface dialog, int i) {
        dialog.dismiss();
    }

    @Override
    public void setPrompt(final String prompt) {

        // retry if this dialog is not read yet
        if (encouragementTextView != null) {
            Log.d(TAG, "setPrompt: dialog is ready, setting prompt now");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    encouragementTextView.setText(prompt);
                }
            });
        } else {
            Log.d(TAG, "setPrompt: dialog not ready yet, retry layer");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setPrompt(prompt);
                }
            }, 500);
        }
    }
}
