package org.tomoaki.coursehelper.Model;

import androidx.lifecycle.LiveData;

import org.tomoaki.coursehelper.Model.Data.Schedule;

public interface Observable {
    public void setData(Schedule schedule);

    public LiveData<Schedule> getData();
}
