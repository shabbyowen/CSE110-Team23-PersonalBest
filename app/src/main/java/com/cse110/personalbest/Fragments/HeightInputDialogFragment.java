package com.cse110.personalbest.Fragments;

import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cse110.personalbest.Events.HeightInputDialogResult;
import com.cse110.personalbest.Events.InputDialogFragmentListener;
import com.cse110.personalbest.R;

public class HeightInputDialogFragment extends InputDialogFragment {

    private EditText inchEditText;
    private EditText footEditText;
    private TextView promptTextView;

    @Override
    public View createView(LayoutInflater inflater) {

        // inflate the view
        View view = inflater.inflate(R.layout.fragment_height_input_dialog, null);
        inchEditText = view.findViewById(R.id.fragment_height_input_dialog_inch_et);
        inchEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        footEditText = view.findViewById(R.id.fragment_height_input_dialog_ft_et);
        footEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        promptTextView = view.findViewById(R.id.fragment_height_input_dialog_tv);
        promptTextView.setText(R.string.prompt_height_str);
        return view;
    }

    @Override
    public String createPositiveBtn() {
        return getString(R.string.ok);
    }

    @Override
    public String createNegativeBtn() {
        return getString(R.string.cancel);
    }

    @Override
    public void onPositiveBtnClicked(DialogInterface dialog, int i) {
        HeightInputDialogResult result = new HeightInputDialogResult();
        int foot = 0;
        int inch = 0;
        try {
            foot = Integer.valueOf(footEditText.getText().toString());
            inch = Integer.valueOf(inchEditText.getText().toString());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        result.foot = foot;
        result.inch = inch;
        if (listener != null && listener.onInputDialogResult(result, this)) {
            dialog.dismiss();
        }
    }

    @Override
    public void onNegativeBtnClicked(DialogInterface dialog, int i) {
        dialog.dismiss();
    }

    @Override
    public void setPrompt(String prompt) {
        promptTextView.setText(prompt);
    }
}
