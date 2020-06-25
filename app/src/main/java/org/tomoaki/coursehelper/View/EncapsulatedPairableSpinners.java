package org.tomoaki.coursehelper.View;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.coursehelper.R;

import org.tomoaki.coursehelper.Model.MultiFilterable;
import org.tomoaki.coursehelper.Model.PairableSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EncapsulatedPairableSpinners {

    private View view;
    private Context context;
    private List<PairableSpinner> spinners;

    /**
     *  In order to use this encapsulated spinners, fragment/activity needs to implements
     * MultiFilterable interface. Because this interface implements essential method for
     * multi-filtering, which communicate to PairableSpinner(Model Object) to filter out
     * courses.
     */
    private MultiFilterable embeddedIn;

    public EncapsulatedPairableSpinners(View view, Context context, MultiFilterable parentFragment) {
        this.view = view;
        this.context = context;
        embeddedIn = parentFragment;
        setUpSpinner();
    }

    public List<PairableSpinner> getSpinners() {
        return this.spinners;
    }

    public void setUpSpinner() {
        Spinner coreSpinner = view.findViewById(R.id.coreSpinner);
        Spinner subjectSpinner = view.findViewById(R.id.subjectSpinner);
        Spinner specialSpinner = view.findViewById(R.id.specialSpinner);
        PairableSpinner corePSpin = new PairableSpinner("core", coreSpinner, 0, null);
        PairableSpinner subjectPSpin = new PairableSpinner("subject", subjectSpinner, 0, null);
        PairableSpinner specialPSpin = new PairableSpinner("special" , specialSpinner, 0, null);
        spinners = new ArrayList<>(Arrays.asList(corePSpin, subjectPSpin, specialPSpin));

        ArrayAdapter<CharSequence> adapterCore = ArrayAdapter.createFromResource(context, R.array.cores, android.R.layout.simple_spinner_item);
        adapterCore.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coreSpinner.setAdapter(adapterCore);
        coreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                corePSpin.setParent(parent);
                corePSpin.setPosition(position);
                if(subjectPSpin.getParent()!=null && specialPSpin.getParent()!=null){
                    embeddedIn.filterCourses();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        ArrayAdapter<CharSequence> adapterSubject = ArrayAdapter.createFromResource(context, R.array.subjects, android.R.layout.simple_spinner_item);
        adapterSubject.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(adapterSubject);
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjectPSpin.setParent(parent);
                subjectPSpin.setPosition(position);
                if(corePSpin.getParent()!=null && specialPSpin.getParent()!=null){
                    embeddedIn.filterCourses();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        ArrayAdapter<CharSequence> adapterSpecial = ArrayAdapter.createFromResource(context, R.array.special, android.R.layout.simple_spinner_item);
        adapterSpecial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialSpinner.setAdapter(adapterSpecial);
        specialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                specialPSpin.setParent(parent);
                specialPSpin.setPosition(position);
                if(subjectPSpin.getParent()!=null && corePSpin.getParent()!=null){
                    embeddedIn.filterCourses();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }
}
