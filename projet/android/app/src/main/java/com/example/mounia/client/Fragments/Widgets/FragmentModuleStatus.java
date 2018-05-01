package com.example.mounia.client.Fragments.Widgets;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mounia.client.CommunicationClientServer.CANEnums;
import com.example.mounia.client.CommunicationClientServer.ModuleType;
import com.example.mounia.client.R;

import java.util.HashMap;

/**
 * Created by mounianordine on 18-03-08.
 */

public class FragmentModuleStatus extends FragmentWidget {

    public FragmentModuleStatus() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_modulestatus, container, false);
    }


    //FIXME..
    @Override
    public void onStart() {

        super.onStart();

//        for (FragmentWidget fragmentDyn : obtenirFragmentsEnfants()) {
//            preparerConteneurFragmentEnfant();
//        }
//
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        for (int i = 0; i < obtenirFragmentsEnfants().size(); i++) {
//            LinearLayout layout = (LinearLayout) ((ViewGroup)getView()).getChildAt(i);
//            ((TextView)layout.getChildAt(0)).setText(obtenirFragmentsEnfants().get(i).obtenirAttributs().get("name"));
//            transaction.add(fragmentContainerIDs.get(i), obtenirFragmentsEnfants().get(i));
//        }
//        transaction.commit();
    }

    private int nLines = 0;
    private int nColumns = 0;
    private int nModules = 0;
    // ModuleType, textbox, time since last message
    private HashMap<Integer, Pair<TextView, Long>> traduction;
    private TextView[][] modules;

    private void addModule(int id){
        if(nModules< nLines*nColumns) {
            TextView text = modules[nModules/nLines][nModules%nLines];
            text.setText(ModuleType.map.get(id));
            text.setBackgroundColor(Color.GREEN);
            traduction.put(id, new Pair(text, 0));
        }
    }


    // Preparer un conteneur pour chaque fragment dyn. de facon aussi dyn.
    public void preparerConteneurFragmentEnfant() {
        GridLayout layout = new GridLayout(getActivity());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(0,0,0,10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layout.setId(View.generateViewId());
            fragmentContainerIDs.add(layout.getId());
        }

        try{
            nLines = Integer.parseInt(attributs.get("nGrid"));
            nColumns = Integer.parseInt(attributs.get("nColumns"));
        } catch(Exception e){
            Log.e("ModuleStatus", "couldnt get grid layout : " + e.getMessage());
        }

        modules = new TextView[nLines][];
        for(int i=0; i<nLines; i++){
            modules[i] = new TextView[nColumns];
            for(int j=0; j<nColumns; j++) {
                GridLayout.Spec row = GridLayout.spec(i);
                GridLayout.Spec col = GridLayout.spec(j);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(row, col);
                params.setGravity(Gravity.FILL_VERTICAL); // fill vertical so we take up the full 2 rows
                // dummy text views to fill some space
                TextView text = new TextView(getActivity());
                text.setPadding(32, 32, 32, 32); // add some random padding to make the views bigger
                text.setLayoutParams(params);
                text.setText("Nothing");
                text.setGravity(Gravity.CENTER);
                text.setBackgroundColor(Color.GRAY);

                layout.addView(text, params);
                modules[i][j] = text;
            }
        }
        ViewGroup viewGroup = (ViewGroup) getView();
        viewGroup.addView(layout);
    }
}
