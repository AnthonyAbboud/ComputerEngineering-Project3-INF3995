package com.example.mounia.client.Composites;

import com.example.mounia.client.Visiteurs.Visiteur;

import java.util.Collection;
import java.util.List;

/**
 * Created by passenger on 3/20/2018.
 */

public abstract class Feuille implements NoeudAbstrait {

    @Override
    public void accueillir(Visiteur visiteur) { visiteur.visiter(this); }

    @Override
    public List<NoeudAbstrait> obtenirEnfants() { return null; }

    @Override
    public void assignerEnfants(List<NoeudAbstrait> enfants) {}
}
