package com.android.personalbest;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class InputDialogFragment extends DialogFragment {

    private TextView promptTextView;
    private EditText inputEditText;

    public InputDialogFragment() {
        // Required empty public constructor
    }

    public interface InputDialogListener{

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // inflate the view
        View view = inflater.inflate(R.layout.dialog_set_goal, null);
        promptTextView = view.findViewById(R.id.fragment_input_dialog_tv);
        inputEditText = view.findViewById(R.id.fragment_input_dialog_et);

        // build the dialog
        builder.setView(view);
        builder.setTitle("Set Goal");
        builder.setPositiveButton("Confirm", this::onConfirmBtnClicked);
        builder.setNegativeButton("Cancel", this::onCancelBtnClicked);
        return builder.create();
    }

    public void onConfirmBtnClicked(DialogInterface dialog, int i) {

    }

    public void onCancelBtnClicked(DialogInterface dialog, int i) {

    }
}
