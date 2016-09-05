package com.droi.guide.activity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.droi.guide.R;
import com.droi.sdk.push.DroiPush;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PushSettingActivity extends AppCompatActivity {
    Context mContext;
    @BindView(R.id.push_setting_pick_confirm)
    Button confirmButton;
    @BindView(R.id.push_setting_switch)
    Switch pushSwitch;
    @BindView(R.id.push_setting_pick_start_silent_time)
    Button startButton;
    @BindView(R.id.push_setting_pick_end_silent_time)
    Button endButton;
    @BindView(R.id.push_setting_pick_start_silent_time_text)
    TextView startTime;
    @BindView(R.id.push_setting_pick_end_silent_time_text)
    TextView endTime;

    int silentStartHour = 0;
    int silentStartMin = 0;
    int silentEndHour = 0;
    int silentEndMin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_setting);
        ButterKnife.bind(this);
        mContext = this;
        pushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DroiPush.setPushEnabled(mContext, isChecked);
            }
        });

        final TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // DroiPush.setSilentTime(mContext,)
                silentStartHour = hourOfDay;
                silentStartMin = minute;
                startTime.setText(hourOfDay + ":" + minute);
            }
        }, 23, 0, true);

        final TimePickerDialog timePickerDialog2 = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // DroiPush.setSilentTime(mContext,)
                silentEndHour = hourOfDay;
                silentEndMin = minute;
                endTime.setText(hourOfDay + ":" + minute);
            }
        }, 6, 0, true);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog2.show();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DroiPush.setSilentTime(mContext, silentStartHour, silentStartMin, silentEndHour, silentEndMin);
            }
        });

    }


}
