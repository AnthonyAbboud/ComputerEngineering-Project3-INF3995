package com.example.mounia.client.Composites;

import android.content.Context;
import android.view.View;

import com.example.mounia.client.Visiteurs.Visiteur;

import java.util.Collection;
import java.util.List;

/**
 * Created by passenger on 3/20/2018.
 */

public interface NoeudAbstrait
{
    NoeudAbstrait obtenirParent();
    void assignerParent(NoeudAbstrait composite);

    List<NoeudAbstrait> obtenirEnfants();
    void assignerEnfants(List<NoeudAbstrait> enfants);

    void accueillir(Visiteur visiteur);
}
