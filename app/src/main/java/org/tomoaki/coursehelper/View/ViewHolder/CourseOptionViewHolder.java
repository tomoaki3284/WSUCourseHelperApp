package org.tomoaki.coursehelper.View.ViewHolder;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.coursehelper.R;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import org.tomoaki.coursehelper.Model.Data.Schedule;
import org.tomoaki.coursehelper.Model.ViewModel.OptionVHScheduleObserver;
import org.tomoaki.coursehelper.View.Fragment.GeneratorOptionsFragment;

public class CourseOptionViewHolder extends GroupViewHolder {

    private TextView title;
    private View view;
    private OptionVHScheduleObserver optionVHScheduleObserver;
    private Fragment parentFragment;

    public CourseOptionViewHolder(View itemView) {
        super(itemView);
        this.view = itemView;
        // itemView is inflater of R.layout.generator_course_layout_simple.xml
        title = itemView.findViewById(R.id.title);
    }

    public void setParentFragment(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public void setTitle(ExpandableGroup group) {
        title.setText(group.getTitle());
    }

    public void set(Schedule schedule) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //TODO: Solve this in better way, maybe interface is good idea
                //I cannot think of better way to do this in abstract way
                //This is not a way for Object Oriented Programming, terrible, so fix later
                //Maybe interface again, which needs to implement displaySelectedOptionInScheduler???
                if(parentFragment instanceof GeneratorOptionsFragment){
                    ((GeneratorOptionsFragment) parentFragment).displaySelectedOptionInScheduler(schedule);
                }else{
                    //TODO: Placeholder for Adv. Generator
                    System.out.println();
                }

                // need to return false to also activate onClick at the same time.
                // I want to activate both onClick(default) and onTouch(override here)
                // onClick for expand expandable recycler view, onTouch for passing object to schedule
                return false;
            }
        });
    }
}
