package org.tomoaki.coursehelper.View;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coursehelper.R;

import org.tomoaki.coursehelper.Model.Course;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeneratorAutomateFragment extends Fragment {

    private View view;

    public GeneratorAutomateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_generator_automate, container, false);
        return view;
    }
}
