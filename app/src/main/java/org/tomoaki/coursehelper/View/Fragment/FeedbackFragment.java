package org.tomoaki.coursehelper.View.Fragment;

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

    private View view;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getParentFragmentManager();

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                fragmentManager.popBackStackImmediate();// pop itself

                int top = fragmentManager.getBackStackEntryCount()-1;
                if(top >= 0){
                    FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(top);
                    Fragment currentFragment = fragmentManager.findFragmentByTag(backStackEntry.getName());
                    currentFragment.getView().setVisibility(View.VISIBLE);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feedback, container, false);

        Button sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Implement feedback feature
                Toast.makeText(getContext(), "Please wait for future update :)", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
