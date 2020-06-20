package org.tomoaki.coursehelper.Model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GeneratorObserver extends ViewModel implements Observable {
    private final MutableLiveData<Schedule> schedule = new MutableLiveData<>();

    public void setData(Schedule schedule) {
        this.schedule.setValue(schedule);
    }

    public LiveData<Schedule> getData() {
        return schedule;
    }
}
