package com.example.mounia.client.Activites;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mounia.client.CommunicationClientServer.Communication;
import com.example.mounia.client.Factory.CreateurWidget;
import com.example.mounia.client.Fragments.Widgets.FragmentRocket;
import com.example.mounia.client.Fragments.Widgets.FragmentWidget;
import com.example.mounia.client.Observateurs.Observateur;
import com.example.mounia.client.R;
import com.example.mounia.client.Utilitaires.Configurations;
import com.example.mounia.client.Utilitaires.LectureXML;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class AffichageActivity extends AppBaseActivity {

    // Permet de lire le fichier xml d'oronos et de produire un arbre de composants
    private LectureXML lectureXML = LectureXML.getInstance();

    // La widgetRacine de l'arbre des widgets
    private FragmentWidget widgetRacine = null;

    // thread communication vers le serveur (CAN)
    private Thread communicationThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configurations.setTheme(this);

        //setContentView(R.layout.activity_affichage);
        setContentView(R.layout.activity_affichage_temps_reel);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Lire le fichier xml du UI et produire l'arbre
        Document document = lectureXML.XMLStringToDocument(LectureXML.getRocketFileContent()); // FIXME : passer un contenu valide
        /*Document document;
        if (Configurations.getFuseeName() == null)
            document = lectureXML.XMLStringToDocument(LectureXML.getRocketFileContent());
        else {
            document = lectureXML.XMLStringToDocument(Configurations.getFuseeName());
            System.out.println("NULL");
        }*/
        Node xmlRacine = lectureXML.obtenirRacineDocument(document);

        // Selon le charge, on peut assumer que le xml du UI comprend au moins une balise rocket
        widgetRacine = CreateurWidget.newInstance(CreateurWidget.ROCKET);
        ((FragmentRocket) widgetRacine).setToolbar(toolbar);

        lectureXML.construireUI(xmlRacine, widgetRacine);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.dynamic_fragment_container, widgetRacine).commit();

        //thread that sends and receives data from server
        Communication.inst = new Communication();
        communicationThread = new Thread(Communication.inst);
        communicationThread.start();
    }

    public static void abonnerLesObservateurs(FragmentWidget widget){
        if(widget instanceof Observateur) Communication.inst.ajouter((Observateur)widget);
        for(FragmentWidget enfant : widget.obtenirFragmentsEnfants()) abonnerLesObservateurs(enfant);
    }

    @Override
    public void onResume(){
        super.onResume();
        abonnerLesObservateurs(widgetRacine);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_affichage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
