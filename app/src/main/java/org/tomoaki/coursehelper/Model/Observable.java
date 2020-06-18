package org.tomoaki.coursehelper.Model;

import androidx.lifecycle.LiveData;

public interface Observable {
    public void setData(Schedule schedule);

    public LiveData<Schedule> getData();
}
