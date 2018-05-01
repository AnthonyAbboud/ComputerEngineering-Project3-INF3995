package com.example.mounia.client.Fragments.Widgets;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mounia.client.CommunicationClientServer.CANEnums;
import com.example.mounia.client.CommunicationClientServer.CANMessage;
import com.example.mounia.client.CommunicationClientServer.Communication;
import com.example.mounia.client.CommunicationClientServer.CustomUpdate;
import com.example.mounia.client.CommunicationClientServer.ModuleType;
import com.example.mounia.client.Fragments.FragmentCan;
import com.example.mounia.client.Observateurs.Observateur;
import com.example.mounia.client.R;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mounianordine on 18-03-08.
 */

public class FragmentDataDisplayer extends FragmentWidget implements Observateur {

    // Exemple : <DataDisplayer>. Ce widget affiche des données correspondant à un groupe de
    // messages CAN. Il peut contenir un nombre arbitrairement grand de tags CAN, mais les
    // applications habituelles ne dépassent pas 16. La disposition des données n’est pas
    // imposée, mais doit être intuitive et claire.

    // https://stackoverflow.com/questions/47743426/error-obtaining-view-hierarchy-unexpected-error-empty-view-hierarchy-when-la

    private ConcurrentHashMap<FragmentCan, View> vuesAColorer = new ConcurrentHashMap<>();

    public FragmentDataDisplayer() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_data_displayer, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Preparer un conteneur dynamique pour chaque fragment dynamique enfant
        for (int i = 0; i < obtenirFragmentsEnfants().size(); i++)
            preparerConteneurFragmentEnfant((FragmentCan) fragmentsEnfants.get(i));

        // Ajouter les fragments enfants dynamiquement
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        for (int i = 0; i < obtenirFragmentsEnfants().size(); i++) {
            LinearLayout layout = (LinearLayout) ((ViewGroup)getView()).getChildAt(i);
            ((TextView)layout.getChildAt(0)).setText(obtenirFragmentsEnfants().get(i).obtenirAttributs().get("name"));
            transaction.add(fragmentContainerIDs.get(i), obtenirFragmentsEnfants().get(i));
        }

        transaction.commit();
    }

    public ConcurrentHashMap<FragmentCan, View> getVuesAColorer() {
        return vuesAColorer;
    }

    // Preparer un conteneur pour chaque fragment dyn. de facon aussi dyn.
    @SuppressLint("NewApi")
    public void preparerConteneurFragmentEnfant(FragmentCan fragmentCanEnfant) {

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setId(View.generateViewId());
        fragmentContainerIDs.add(linearLayout.getId());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1);
        linearLayout.setLayoutParams(layoutParams);
        //linearLayout.setMinimumHeight(300);

        TextView etiquete = new TextView(getActivity());
        TextView valeur = new TextView(getActivity());
        linearLayout.addView(etiquete);
        linearLayout.addView(valeur);

        ViewGroup viewGroup = (ViewGroup) getView();
        viewGroup.addView(linearLayout);
        vuesAColorer.put(fragmentCanEnfant, linearLayout);
    }

    @Override
    public void mettreAJour() {
        for (int i=0; i< Communication.data.size(); i++) {
            CANMessage canMessage = Communication.data.get(i);
            if (canMessage != null && canMessage.messageIsValid) {
                String msgID = CANEnums.CANSid.toString(canMessage.msgID);
                List<FragmentCan> fragmentCans = recupererFragmentCans(msgID);
                for (FragmentCan fragmentCan : fragmentCans) {
                    if (fragmentCan != null) {

                        // Si c'est le temps de mettre a jour alors le faire. Sinon, ne rien faire.
                        if (!fragmentCan.tempsDeMettreAJour()) continue;

                        // Ne pas oublier de relancer le chronometre pour ce fragment can.
                        fragmentCan.relancerChronometre();

                        // Si la donnée entrante ne provient pas de la source spécifiée, aucune action n’est prise.
                        String canMessageSrcID = ModuleType.instance().toString(canMessage.srcID);
                        if (fragmentCan.specificSourceDifferent(canMessageSrcID)) continue;

                        // Si la donnée entrante ne provient pas d’une source avec le numéro de série spécifié, aucune action n’est prise.
                        if (fragmentCan.serialNbDifferent(canMessage.srcSerial)) continue;

                        String customUpdate = fragmentCan.recupererCustomUpdate(canMessage);
                        if (customUpdate != null) {
                            String functionName = attributs.get(FragmentCan.ATTR_KEY_CUSTOM_UPDATE);
                            if (functionName != null && fragmentCan.attributs.get(FragmentCan.ATTR_KEY_CUSTOM_ACCEPTABLE) != null) {
                                int color = CustomUpdate.acceptable(functionName, canMessage) ? Color.GREEN : Color.RED;
                                if (this.getView() != null) getActivity().runOnUiThread(new OnUIThreadRunnable(fragmentCan, customUpdate, color));
                                //continue;
                            }

                            // TODO ... enlever le continue suivant et traiter
                            continue;
                        }

                        // Recuperer la donnee a afficher (data 1 ou data 2 => display)
                        // Recuperer l'unite s'il y a lieu
                        double donnee = fragmentCan.recupererDonnee(canMessage);
                        String unite = fragmentCan.recupererUnite(canMessage);

                        // Mettre la valeur de la donnee a jour selon le nombre de chiffres significatifs
                        donnee = fragmentCan.recupererChiffresSignificatifs(donnee);

                        // https://stackoverflow.com/questions/12850143/android-basics-running-code-in-the-ui-thread
                        // https://stackoverflow.com/questions/16425146/runonuithread-in-fragment
                        // Adapter la couleur (rouge ou verte ou normal) selon min et max accepatable
                        int backGroundColor = fragmentCan.getBackGroundColor(donnee);

                        // Assigner la donnee et la couleur du background au UI
                        if (this.getView() != null) {
                            getActivity().runOnUiThread(new OnUIThreadRunnable(donnee, unite, fragmentCan, backGroundColor));
                        }

                        // Test
//                    Random random = new Random(System.nanoTime());
//                    int color = random.nextInt(2);
//                    if (color == 1) color = Color.GREEN;
//                    else color = Color.RED;
//                    getActivity().runOnUiThread(new OnUIThreadRunnable(random.nextDouble(), " N", fragmentCan, color));
//                    continue;

//
                    }
                }
            }
        }
    }

    class OnUIThreadRunnable implements Runnable {
        private double donnee;
        private String unite;
        private FragmentCan fragmentCan;
        private String customUpdate = null;
        private int backGroundColor;

        public OnUIThreadRunnable(FragmentCan fragmentCan,
                                  String customUpdate,
                                  int backGroundColor) {
            this.fragmentCan = fragmentCan;
            this.customUpdate = customUpdate;
            this.backGroundColor = backGroundColor;
        }

        public OnUIThreadRunnable(double donnee,
                                  String unite,
                                  FragmentCan fragmentCan,
                                  int backGroundColor) {
            this.donnee = donnee;
            this.unite = unite;
            this.fragmentCan = fragmentCan;
            this.backGroundColor = backGroundColor;
        }

        @Override
        public void run() {
            ViewGroup vueContainerFragment = (ViewGroup) getVuesAColorer().get(fragmentCan);
            TextView vueAColorer = (TextView) vueContainerFragment.getChildAt(1);
            vueAColorer.setBackgroundColor(backGroundColor);
            if (customUpdate == null) vueAColorer.setText("" + donnee + "" + unite);
            else vueAColorer.setText(customUpdate);
        }
    }
}
