package com.android.personalbest;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;


import static android.content.Context.MODE_PRIVATE;

public class DailyGoalFragment extends Fragment {

    private Button recordBtn;
    private EditText new_goal;
    private AlertDialog changeGoalDialog;
    private Button changeGoalBtn;
    private Button addStepsBtn;

    public DailyGoalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_daily_goal, container, false);

        recordBtn = fragmentView.findViewById(R.id.daily_goal_record_btn);
        recordBtn.setOnClickListener(this::onRecordBtnClicked);

        changeGoalBtn = fragmentView.findViewById(R.id.daily_goal_change_goal_btn);
        changeGoalBtn.setOnClickListener(this::onChangeGoalBtnClicked);

        addStepsBtn = fragmentView.findViewById(R.id.daily_goal_add_steps_btn);
        addStepsBtn.setOnClickListener(this::onAddStepsBtnClicked);

        return fragmentView;
    }

    public void onRecordBtnClicked(View view) {

    }

    public void onChangeGoalBtnClicked(View view) {

    }

    public void onAddStepsBtnClicked(View view) {

    }

    public void editDailyGoal(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.dialog_set_goal, null, false );

        builder.setView(rootView);
        builder.setTitle("Set Goal");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {


                if(Integer.valueOf(new_goal.toString()) < 0){
                    builder.setMessage("Please use your fucking brain");
                }else{
                    SharedPreferences sharedPreferences =
                        getActivity().getSharedPreferences("user_name", MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("daily_goal", new_goal.getText().toString());

                    editor.apply();

                    Toast.makeText(getActivity(), "Saved", Toast.LENGTH_LONG).show();

                    dialog.dismiss();
                }
            }
        });

        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });


        changeGoalDialog = builder.create();

        new_goal = rootView.findViewById();


        changeGoalDialog.show();
    }
}