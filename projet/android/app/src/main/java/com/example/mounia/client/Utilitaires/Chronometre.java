package com.example.mounia.client.Utilitaires;

/**
 * Created by passenger on 4/8/2018.
 */

public class Chronometre {
    private long tempsDebut;

    public Chronometre() { lancer(); }

    public void lancer() { tempsDebut = System.currentTimeMillis() / 1000; }

    /**
     * @return Le nombres de secondes ecoulees
     */
    public long secondesEcoulees() { return  (System.currentTimeMillis() / 1000) - tempsDebut; }
}
