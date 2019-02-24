package com.cse110.personalbest.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.cse110.personalbest.Events.InputDialogFragmentListener;

public abstract class InputDialogFragment extends DialogFragment {

    protected InputDialogFragmentListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // get the builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(createView(getActivity().getLayoutInflater()));

        // create button
        String positiveText = createPositiveBtn();
        if (positiveText != null) {
            builder.setPositiveButton(positiveText, null);
        }
        String negativeText = createNegativeBtn();
        if (negativeText != null) {
            builder.setNegativeButton(negativeText, null);
        }

        // set listeners
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface d) {
                Button positiveBtn = ((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeBtn = ((AlertDialog) d).getButton(AlertDialog.BUTTON_NEGATIVE);
                if (positiveBtn != null) {
                    positiveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onPositiveBtnClicked(d, AlertDialog.BUTTON_POSITIVE);
                        }
                    });
                }
                if (negativeBtn != null) {
                    negativeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onNegativeBtnClicked(d, AlertDialog.BUTTON_NEGATIVE);
                        }
                    });
                }
            }
        });

        return dialog;
    }

    public void registerListener(InputDialogFragmentListener listener) {
        this.listener = listener;
    }

    public abstract View createView(LayoutInflater inflater);
    public abstract String createPositiveBtn();
    public abstract String createNegativeBtn();
    public abstract void onPositiveBtnClicked(DialogInterface dialog, int i);
    public abstract void onNegativeBtnClicked(DialogInterface dialog, int i);
    public abstract void setPrompt(String prompt);
}
