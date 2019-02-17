package com.android.personalbest;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeightPromptFragment extends DialogFragment {

    private TextView promptTextView;
    private EditText inputEditTextFt;
    private EditText inputEditTextInch;
    private HeightPromptListener listener;
    private String tag;
    private int prompt;
    private int height;
    public static final String HEIGHT_SHARED_PREF = "personal_best_height";
    public static final String HEIGHT = "user_height";

    public HeightPromptFragment() {
        // Required empty public constructor
    }

    // implement this interface to get the result from the dialog
    public interface HeightPromptListener{
        boolean onInputResult(String tag, String result, TextView view);
    }

    public static HeightPromptFragment newInstance(HeightPromptListener listener, String tag, int prompt) {

        // initialize a new input dialog fragment
        HeightPromptFragment fragment = new HeightPromptFragment();

        // preset the listener for callback, tag for , prompt for asking user for inputs
        fragment.listener = listener;
        fragment.tag = tag;
        fragment.prompt = prompt;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // get the builder and the inflater
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // inflate the view
        View view = inflater.inflate(R.layout.fragment_input_height, null);
        promptTextView = view.findViewById(R.id.fragment_input_dialog_tv);
        inputEditTextFt = view.findViewById(R.id.fragment_input_dialog_et_ft);
        inputEditTextInch = view.findViewById(R.id.fragment_input_dialog_et_inch);

        // set prompt
        promptTextView.setText(prompt);

        // build the dialog
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, this::onConfirmBtnClicked);

        // over write the default closing action after pressing the confirm button
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            Button button = ((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener((v) -> onConfirmBtnClicked(d, AlertDialog.BUTTON_POSITIVE));
        });
        return dialog;
    }

    public void onConfirmBtnClicked(DialogInterface dialog, int i) {

        SharedPreferences sp = getContext().getSharedPreferences(HEIGHT_SHARED_PREF, Context.MODE_PRIVATE);

        // callback, if listener says true, dismiss this dialog
        if ((listener.onInputResult(tag, inputEditTextFt.getText().toString(), promptTextView)) && (listener.onInputResult(tag, inputEditTextInch.getText().toString(), promptTextView))) {
            // validate entered goal
            int ft;
            int inch;

            try {
                ft = Integer.valueOf(inputEditTextFt.getText().toString());
                inch = Integer.valueOf(inputEditTextInch.getText().toString());
                height = 12*ft + inch;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (height > 0 && height < 110) {
                sp.edit().putInt(HEIGHT, height).apply();
                Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_LONG).show();
                dialog.dismiss();

            } else {
                promptTextView.setText(R.string.height_instruction_failed);
            }

        }

    }

}

