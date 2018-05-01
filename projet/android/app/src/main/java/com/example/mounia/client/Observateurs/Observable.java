package com.example.mounia.client.Observateurs;

/**
 * Created by passenger on 3/26/2018.
 */

public interface Observable {
    void ajouter(Observateur observateur);
    void enlever(Observateur observateur);
    void avertir();
}
