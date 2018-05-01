package com.example.mounia.client.Fragments.Widgets;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.ViewUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.mounia.client.CommunicationClientServer.CANEnums;
import com.example.mounia.client.CommunicationClientServer.CANMessage;
import com.example.mounia.client.CommunicationClientServer.Communication;
import com.example.mounia.client.Fragments.FragmentCan;
import com.example.mounia.client.Observateurs.Observateur;
import com.example.mounia.client.R;

import java.util.List;

/**
 * Created by mounianordine on 18-03-21.
 */

public class FragmentDisplayLogWidget extends FragmentWidget implements Observateur {
    public FragmentDisplayLogWidget() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_display_log_widget, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        for (int i = 0; i < fragmentsEnfants.size(); i++)
            preparerConteneurFragmentEnfant();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        for (int i = 0; i < obtenirFragmentsEnfants().size(); i++) {
            LinearLayout layout = (LinearLayout) ((ViewGroup)getView()).getChildAt(i);
            ((TextView)layout.getChildAt(0)).setText(obtenirFragmentsEnfants().get(i).obtenirAttributs().get("name"));
            transaction.add(fragmentContainerIDs.get(i), obtenirFragmentsEnfants().get(i));
        }

        transaction.commit();
    }

    // Preparer un conteneur pour chaque fragment dyn. de facon aussi dyn.
    public void preparerConteneurFragmentEnfant() {

        ViewGroup viewGroup = (ViewGroup) getView();
       // ScrollView scrollView = new ScrollView(getActivity());
//        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        scrollView.setLayoutParams(layoutParams1);
//        viewGroup.addView(scrollView);
       // ScrollView sv = new ScrollView(getActivity());
      //  sv.scrollTo(0, sv.getBottom());
       // viewGroup.addView(sv);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            linearLayout.setId(View.generateViewId());
            fragmentContainerIDs.add(linearLayout.getId());
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); // a revoir
        layoutParams.weight = 1;


        linearLayout.setLayoutParams(layoutParams);

        TextView etiquete = new TextView(getActivity());
        TextView valeur = new TextView(getActivity());

        linearLayout.addView(etiquete);

        linearLayout.addView(valeur);

        viewGroup.addView(linearLayout);
    }

    @Override
    public void mettreAJour() {
//        int startIndex = (int) Communication.inst.cursor;
//        List<CANMessage> data = Communication.data.subList(startIndex, Communication.data.size());
//        for (CANMessage canMessage : data) {
//            if (canMessage.messageIsValid) {
//                String msgID = CANEnums.CANSid.toString(canMessage.msgID);
//                FragmentCan fragmentCan = recupererFragmentCan(msgID); // FIXME : Les ids ne sont pas uniques ... devrait se baser sur les tags name
//                if (fragmentCan != null) {
//
//                    // Si c'est le temps de mettre a jour alors le faire. Sinon, ne rien faire.
//                    if (!fragmentCan.tempsDeMettreAJour()) continue;
//
//                    // Ne pas oublier de relancer le chronometre pour ce fragment can.
//                    fragmentCan.relancerChronometre();
//
//                    // Si la donnée entrante ne provient pas de la source spécifiée, aucune action n’est prise.
//                    // TODO : passer ModuleType.SpecificSource.toString(canMessage.srcID) au lieu de null
//                    // ...
//                    if (fragmentCan.specificSourceDifferent("")) continue;
//
//                    // Si la donnée entrante ne provient pas d’une source avec le numéro de série spécifié, aucune action n’est prise.
//                    if (fragmentCan.serialNbDifferent(canMessage.srcSerial)) continue;
//
//                    // Recuperer la donnee a afficher (data 1 ou data 2 => display)
//                    // Recuperer l'unite s'il y a lieu
//                    double donnee = fragmentCan.recupererDonnee(canMessage);
//                    String unite = fragmentCan.recupererUnite(canMessage);
//
//                    // Mettre la valeur de la donnee a jour selon le nombre de chiffres significatifs
//                    donnee = fragmentCan.recupererChiffresSignificatifs(donnee);
//
//                    // Adapter la couleur (rouge ou verte ou normal) selon min et max accepatable
//                    int backGroundColor = fragmentCan.getBackGroundColor(donnee);
//                    ViewGroup vueContainerFragment = (ViewGroup) vuesAColorer.get(fragmentCan);
//                    TextView vueAColorer = (TextView) vueContainerFragment.getChildAt(1);
//                    vueAColorer.setBackgroundColor(backGroundColor);
//                    vueAColorer.setText("" + donnee + "" + unite);
//
//                    // TODO : Considerer l'attribut customUpdate
//                    // ...
//                }
//            }
//        }
    }
}