package com.example.coursehelper.View;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.example.coursehelper.Model.Course;
import com.example.coursehelper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseDescriptionDialogFragment extends DialogFragment {

    CoursesFragment coursesFragment;

    String titleRaw;
    String crnRaw;
    String facultyRaw;
    String roomRaw;
    String timeContentRaw;
    String creditRaw;
    String descriptionRaw;
    String coresRaw;

    public static DialogFragment newInstance(Bundle bundle) {
        System.out.println("********************** called newInstance on Dialog");
        CourseDescriptionDialogFragment f = new CourseDescriptionDialogFragment();
        f.setArguments(bundle);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_course_description, null);

        titleRaw = getArguments().getString("title");
        timeContentRaw = getArguments().getString("timeContent");
        facultyRaw = getArguments().getString("faculty");
        roomRaw = getArguments().getString("room");
        creditRaw = getArguments().getString("credit");
        crnRaw = getArguments().getString("crn");
        descriptionRaw = getArguments().getString("description");
        coresRaw = getArguments().getString("cores");

        inflateDialog(view);
        setClickListener(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        coursesFragment = (CoursesFragment) getTargetFragment();
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
                coursesFragment.addCourseToSchedule(crnRaw);
                Toast.makeText(getContext(), "You successfully added class", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        dropButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                coursesFragment.removeCourseFromSchedule(crnRaw);
                Toast.makeText(getContext(), "You successfully dropped class", Toast.LENGTH_SHORT).show();
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

        title.setText(titleRaw);
        crn.setText(crnRaw);
        faculty.setText(facultyRaw);
        room.setText(roomRaw);
        timeContent.setText(timeContentRaw);
        credit.setText(creditRaw);
        description.setText(descriptionRaw);
        description.setMovementMethod(new ScrollingMovementMethod());
        cores.setText(coresRaw);
    }
}
