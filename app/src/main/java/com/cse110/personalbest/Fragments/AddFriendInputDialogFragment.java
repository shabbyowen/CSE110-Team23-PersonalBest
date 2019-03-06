package com.cse110.personalbest.Fragments;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.cse110.personalbest.Events.AddFriendInputDialogResult;
import com.cse110.personalbest.R;

public class AddFriendInputDialogFragment extends InputDialogFragment {

    TextView promptTextView;
    EditText userEmailTextView;

    @Override
    public View createView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_add_friend_input_dialog, null);
        promptTextView = view.findViewById(R.id.fragment_add_friend_input_dialog_tv);
        userEmailTextView = view.findViewById(R.id.fragment_add_friend_input_dialog_et);
        promptTextView.setText(R.string.prompt_add_friend_str);
        return view;
    }

    @Override
    public String createPositiveBtn() {
        return getString(R.string.confirm);
    }

    @Override
    public String createNegativeBtn() {
        return getString(R.string.cancel);
    }

    @Override
    public void onPositiveBtnClicked(DialogInterface dialog, int i) {
        AddFriendInputDialogResult result = new AddFriendInputDialogResult();
        String inputEmail = userEmailTextView.getText().toString();
        result.userEmail = new String(userEmailTextView.getText().toString());
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
