package org.tomoaki.coursehelper.View.Fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.coursehelper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RateProfessorFragment extends Fragment {

    private final String RATE_MY_PROF_URL = "https://www.ratemyprofessors.com/search.jsp?queryoption=TEACHER&queryBy=schoolDetails&schoolID=1185&schoolName=Westfield+State+University&dept=select";
    public final static String FRAG_TAG = "com.exmaple.coursehelper.View.RateProfessorFragment";

    public RateProfessorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rate_professor, container, false);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getParentFragmentManager();
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

        WebView webView = view.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(RATE_MY_PROF_URL);

        return view;
    }
}
