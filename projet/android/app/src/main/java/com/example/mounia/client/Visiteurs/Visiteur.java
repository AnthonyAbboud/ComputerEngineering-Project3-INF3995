package com.example.mounia.client.Visiteurs;

import com.example.mounia.client.Composites.Feuille;
import com.example.mounia.client.Composites.NoeudAbstrait;
import com.example.mounia.client.Composites.NoeudComposite;

/**
 * Created by passenger on 3/20/2018.
 */

public abstract class Visiteur {

    public void visiter(NoeudAbstrait noeudAbstrait) {}

    public void visiter(NoeudComposite noeudComposite) {}

    public void visiter(Feuille feuille) {}

//    public abstract void visiter(DualHWidget dualHWidget);
//
//    public abstract void visiter(DualVWidget dualVWidget);
//
//    public abstract void visiter(DataDisplayer dataDisplayer);
//
//    public abstract void visiter(TAB tab);
//
//    public abstract void visiter(Rocket rocket);
//
//    public abstract void visiter(TabContainer tabContainer);
//
//    public abstract void visiter(Grid grid);
//
//    public abstract void visiter(GridContainer gridContainer);
//
//    public abstract void visiter(FindMe findMe);
//
//    public abstract void visiter(Plot plot);
//
//    public abstract void visiter(Can can);
//
//    public abstract void visiter(ButtonArray buttonArray);
//
//    public abstract void visiter(Modulestatus modulestatus);
//
//    public abstract void visiter(DebugOptions debugOptions);
//
//    public abstract void visiter(RadioStatus radioStatus);
//
//    public abstract void visiter(Map map);
//
//    public abstract void visiter(CustomCANSender customCANSender);
//
//    public abstract void visiter(DisplayLogWidget displayLogWidget);
//
//    // TODO : ajouter les autres methodes visiter ici si necessaire
//    // ...
}
