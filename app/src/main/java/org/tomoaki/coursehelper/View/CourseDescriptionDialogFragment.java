package org.tomoaki.coursehelper.View;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;

import org.jetbrains.annotations.NotNull;
import org.tomoaki.coursehelper.Model.Course;
import org.tomoaki.coursehelper.Model.CoursesEditable;

import com.example.coursehelper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseDescriptionDialogFragment extends DialogFragment {

    public static final String LAYOUT_TO_INFLATE = "layoutToInflate";

    private CoursesEditable targetFragment;
    private Course course;
    private int layoutToInflate;

    public void setCourse(Course course) {
        if(null == course) return;
        this.course = course;
    }

    public void setLayoutToInflate(int layoutToInflate) {
        this.layoutToInflate = layoutToInflate;
    }

    public static CourseDescriptionDialogFragment newInstance(@NotNull Bundle bundle) {
        CourseDescriptionDialogFragment f = new CourseDescriptionDialogFragment();
        f.setLayoutToInflate((int) bundle.get(LAYOUT_TO_INFLATE));
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layoutToInflate, null);
        
        inflateDialog(view);
        setClickListener(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //assuming that this dialog is opened by CourseFragment only
        targetFragment = (CoursesEditable) getTargetFragment();
    }

    public void setClickListener(View view) {
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button addButton = view.findViewById(R.id.addButton);
        Button dropButton = view.findViewById(R.id.dropButton);

        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                targetFragment.addCourseToSchedule(course);
                dismiss();
            }
        });

        dropButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                targetFragment.removeCourseFromSchedule(course);
                dismiss();
            }
        });
    }

    public void inflateDialog(View view) {
        TextView title = view.findViewById(R.id.courseTitle);
        TextView crn = view.findViewById(R.id.courseCRN);
        TextView faculty = view.findViewById(R.id.courseFaculty);
        TextView room = view.findViewById(R.id.courseRoom);
        TextView timeContent = view.findViewById(R.id.courseTimeContent);
        TextView credit = view.findViewById(R.id.courseCredit);
        TextView description = view.findViewById(R.id.courseDescription);
        TextView cores = view.findViewById(R.id.courseCores);

        title.setText(course.getTitle());
        //Need to check null, because some need to inflate layout that has no info about these
        if(crn != null) crn.setText(course.getCourseCRN());
        if(faculty != null) faculty.setText(course.getFaculty());
        if(room != null) room.setText(course.getRoom());
        if(timeContent != null) timeContent.setText(course.getTimeContent());
        if(credit != null) credit.setText(Double.toString(course.getCredit()));
        if(course.getCourseDescription() == null || course.getCourseDescription().length() == 0){
            description.setText("No description available.");
        }else{
            description.setText(course.getCourseDescription());
        }
        description.setMovementMethod(new ScrollingMovementMethod());
        cores.setText(course.getCoresAsString());
    }
}
