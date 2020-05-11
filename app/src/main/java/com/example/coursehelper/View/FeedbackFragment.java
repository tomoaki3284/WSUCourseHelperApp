package com.example.coursehelper.View;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.coursehelper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment {

    public static final String FRAG_TAG = "com.exmaple.coursehelper.View.FeedbackFragment";
    public static final String FRAG_TAG2 = "com.exmaple.coursehelper.View.FeedbackFragment2";

    private View view;

    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                System.out.println("Back Button Pressed");

                fragmentManager.popBackStack();

                //Assuming that previous fragment on back stack is CoursesScheduleTagFragment
                //Also this fragment is currently only open after CoursesScheduleTagFragment is being push to back stack
                CoursesScheduleTabFragment myFragment = (CoursesScheduleTabFragment) fragmentManager.findFragmentByTag(CoursesScheduleTabFragment.FRAG_TAG);
                if (myFragment != null) {
                    myFragment.getView().setVisibility(View.VISIBLE);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_feedback, container, false);

        Button sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Implement feedback feature
                Toast.makeText(getContext(), "Not Implemented Yet", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
