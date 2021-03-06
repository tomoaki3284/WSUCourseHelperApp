package org.tomoaki.coursehelper.Model.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.tomoaki.coursehelper.Model.Observable;
import org.tomoaki.coursehelper.Model.Data.Schedule;

public class GeneratorObserver extends ViewModel implements Observable {
    private final MutableLiveData<Schedule> schedule = new MutableLiveData<>();

    public void setData(Schedule schedule) {
        this.schedule.setValue(schedule);
    }

    public LiveData<Schedule> getData() {
        return schedule;
    }
}
