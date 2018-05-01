package com.example.mounia.client.Composites;

import com.example.mounia.client.Visiteurs.Visiteur;

import java.util.Collection;
import java.util.List;

/**
 * Created by passenger on 3/20/2018.
 */

public abstract class NoeudComposite implements NoeudAbstrait {

    protected List<NoeudAbstrait> enfants = null;

    public abstract void accueillir(Visiteur visiteur);

    @Override
    public List<NoeudAbstrait> obtenirEnfants() { return enfants; }

    @Override
    public void assignerEnfants(List<NoeudAbstrait> enfants) {
        this.enfants = enfants;
    }
}
