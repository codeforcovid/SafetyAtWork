package com.safeatwork.reminder.createalarm;

import android.app.Application;

import com.safeatwork.reminder.data.Alarm;
import com.safeatwork.reminder.data.AlarmRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class CreateAlarmViewModel extends AndroidViewModel {
    private AlarmRepository alarmRepository;

    public CreateAlarmViewModel(@NonNull Application application) {
        super(application);

        alarmRepository = new AlarmRepository(application);
    }

    public void insert(Alarm alarm) {
        alarmRepository.insert(alarm);
    }
}
