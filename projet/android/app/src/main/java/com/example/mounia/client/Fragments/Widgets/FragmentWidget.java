package com.example.mounia.client.Fragments.Widgets;

import android.support.v4.app.Fragment;

import com.example.mounia.client.Fragments.FragmentCan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by passenger on 3/28/2018.
 */

public abstract class FragmentWidget extends Fragment {

    // Le nombre d'attributs pour chaque tag est variable
    protected Map<String, String> attributs = new HashMap<>();

    // Chaque widget a un parent excepte la racine
    protected FragmentWidget fragmentParent = null;

    // Les widgets fragmentsEnfants
    protected List<FragmentWidget> fragmentsEnfants = new ArrayList<>();

    // Liste d'ids generes dyn. pour chaque conteneur de fragment situe dans la vue de celui-ci
    protected List<Integer> fragmentContainerIDs = new ArrayList<>();

    /**
     * Permet de recuperer le fragment can enfant qui correspond a cet id.
     *
     * @param canMessageID correspond a l'attribut msgID d'un message can, converti en texte
     *                     au moyen de CANEnums.CANsid.toString().
     *
     * @return Renvoie le premier fragment can trouve si au moins un des fragments can enfants
     * de ce fragment a le meme id que le parametre. Renvoie null sinon.
     */

    public List<FragmentCan> recupererFragmentCans(String canMessageID) {
        List<FragmentCan> fragmentCans = new ArrayList<>();
        for (FragmentWidget fragmentWidget : obtenirFragmentsEnfants()) {
            FragmentCan fragmentCan = (FragmentCan) fragmentWidget;
            String id = fragmentCan.attributs.get(FragmentCan.ATTR_KEY_ID);
            if (id != null && id.equals(canMessageID)) fragmentCans.add(fragmentCan);
        }

        return fragmentCans;
    }

    public void assignerAttributs(Map<String, String> attributs) { this.attributs = attributs; }

    public Map<String, String> obtenirAttributs() { return this.attributs; }

    public FragmentWidget obtenirFragmentParent() { return fragmentParent; }

    public void assignerFragmentParent(FragmentWidget parent) { this.fragmentParent = parent; }

    public void assignerFragmentsEnfants(List<FragmentWidget> enfants) {
        this.fragmentsEnfants = enfants;
    }

    public List<FragmentWidget> obtenirFragmentsEnfants() { return this.fragmentsEnfants; }
}
