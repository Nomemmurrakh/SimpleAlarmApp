package com.example.simplealarmapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TimePicker;

import com.example.simplealarmapp.databinding.ActivityMainBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private ActivityMainBinding binding;
    private Context context = this;

    private boolean isEveryday;
    private String title;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        calendar = Calendar.getInstance();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        NotificationChannelCompat notificationChannel = new NotificationChannelCompat.Builder("alarm", NotificationManagerCompat.IMPORTANCE_HIGH)
                .setName("Alarm Channel")
                .build();

        notificationManager.createNotificationChannel(notificationChannel);

        binding.checkEveryday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isEveryday = isChecked;

                if (isEveryday){
                    binding.chipDays.setVisibility(View.GONE);
                }else {
                    binding.chipDays.setVisibility(View.VISIBLE);
                }
            }
        });

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        binding.btnSaveAlarm.setOnClickListener(v -> {
            title = binding.editTextTitleAlarm.getText().toString();

            String[] days;
            if(isEveryday){
                days = new String[]{};
            }else {
                days = getDaysFromChips();
            }

            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.setAction("com.example.simplealarmapp.custom_alarm");
            alarmIntent.putExtra("channelId", "alarm");
            alarmIntent.putExtra("days", days);
            alarmIntent.putExtra("title", title);

            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                    context,
                    1,
                    alarmIntent,
                    0
            );

            // Set Alarm
            if (!isEveryday){
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC,
                        calendar.getTimeInMillis(),
                        alarmPendingIntent
                );
            }else {
                alarmManager.setRepeating(
                        AlarmManager.RTC,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        alarmPendingIntent
                );
            }
        });

        binding.btnSelectTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hourOfDay = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            new TimePickerDialog(
                    context,
                    this,
                    hourOfDay,
                    minute,
                    true
            ).show();
        });
    }

    private String[] getDaysFromChips() {
        List<String> days = new ArrayList<>();
        List<Integer> chipIds = binding.chipDays.getCheckedChipIds();
        for (int id : chipIds){
            Chip day = findViewById(id);
            days.add(day.toString());
        }
        String[] arr = new String[days.size()];
        for(int x = 0; x < days.size(); x++){
            arr[x] = days.get(0);
        }
        return arr;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
    }
}