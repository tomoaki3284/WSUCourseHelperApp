package org.tomoaki.coursehelper.Model.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.tomoaki.coursehelper.Model.Data.Schedule;

import java.util.List;

public class GeneratorOptionsObserver extends ViewModel {
    private final MutableLiveData<List<Schedule>> schedules = new MutableLiveData<>();

    public void setData(List<Schedule> schedules) {
        this.schedules.setValue(schedules);
    }

    public LiveData<List<Schedule>> getData() {
        return schedules;
    }
}
