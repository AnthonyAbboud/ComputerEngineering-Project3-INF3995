package com.example.mounia.client.Activites;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.lang.String;


import com.example.mounia.client.CommunicationClientServer.Communication;
import com.example.mounia.client.R;
import com.example.mounia.client.Utilitaires.Configurations;
import com.example.mounia.client.Utilitaires.Preferences;

import java.io.IOException;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.mounia.client.Utilitaires.Configurations.getFuseeName;
import static com.example.mounia.client.Utilitaires.Configurations.setFile;
import static com.example.mounia.client.Utilitaires.Configurations.setFuseeName;
import static com.example.mounia.client.Utilitaires.Configurations.setNomMap;
import static com.example.mounia.client.Utilitaires.Configurations.setPort;
import static java.lang.Thread.sleep;




/*
*   Activité correspondant au premier fragment (authentification)
 */
public class MainActivity extends AppBaseActivity implements LoaderCallbacks<Cursor>{

    /*
    * GUI
     */
    private EditText editPassword;
    private EditText editUsername;
    private EditText editIp;
    private Button buttonCancel;
    private Button buttonLogin;
    private CheckBox rememberMe;
    private CheckBox rememberPassword;


    // client et requetepour récupérer les .xml (GUI dynamique de AffichageActivity)
    private OkHttpClient okHttpClient;
    private Request request;

    // Tags de deboguage pour log
    public static final String TAGMap = "Map: ";
    public static final String TAGRocket = "Rocket: ";
    public static final String TAGPDF = "Pdf: ";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        *   initialisation GUI
         */
        Configurations.setTheme(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editIp = findViewById(R.id.adresseIP);
        editUsername = (EditText) findViewById(R.id.username);
        editPassword = (EditText) findViewById(R.id.password);
        rememberMe = (CheckBox) findViewById(R.id.rememberMe);
        rememberPassword = (CheckBox) findViewById(R.id.rememberPassword);
        buttonLogin = findViewById(R.id.login);

        //crait les preferences (configurations persistentes entre deux sesions)
        Preferences.init(getBaseContext());



        // recupere les valeurs par defaut des entrées
        String ip = Preferences.get(Preferences.IP);
        String username = Preferences.get(Preferences.USERNAME);
        String password = Preferences.get(Preferences.PASSWORD);
        editIp.setText(ip);
        editUsername.setText(username);
        editPassword.setText(password);
        rememberPassword.setChecked(Boolean.parseBoolean(Preferences.get(Preferences.REMEMBER_PASSWORD)));
        rememberMe.setChecked(Boolean.parseBoolean(Preferences.get(Preferences.REMEMBER_ME)));


        //code du bouton Start
        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(final View view) {

                // recupere les valeurs entrees par l'utilisateur
                final String ip = editIp.getText().toString().trim();
                final String username = editUsername.getText().toString().trim();
                final String password = editPassword.getText().toString().trim();


                // sauve dans les préférences, selon le bouton "se souvenir de moi" et "se souvenir du mot de pass"
                Preferences.set(Preferences.IP, ip);
				if(rememberMe.isChecked()){
                    Preferences.set(Preferences.USERNAME, username);
                    Preferences.set(Preferences.REMEMBER_ME, "true");
                    //Log.i("remember me", "set");
				}
                else {
                    Preferences.set(Preferences.USERNAME, "");
                    Preferences.set(Preferences.REMEMBER_ME, "false");
                    //Log.i("remember me", "nulled");
                }
                if(rememberPassword.isChecked()) {
                    Preferences.set(Preferences.PASSWORD, password);
                    Preferences.set(Preferences.REMEMBER_PASSWORD, "true");
                    //Log.i("remember password", "set");
                }
                else {
                    Preferences.set(Preferences.PASSWORD, "");
                    Preferences.set(Preferences.REMEMBER_PASSWORD, "false");
                    //Log.i("remember password", "nulled");
                }


				// passe directement au prochain fragment (utilisé lors du développement)
				if(username.equals("bypass")) {
                    onStartClick();
                }

                // une widget à afficher durant la connection
                final ProgressDialog pdia = ProgressDialog.show(MainActivity.this, "", "", true);
                pdia.setMessage("Attempting connection...");
                pdia.show();

                // le travail de connection de fait dans le décors
                final AsyncTask<Void, Void, Void> login = new AsyncTask<Void, Void, Void>() {
                    @Override
                        protected Void doInBackground(Void... params)
                        {
                        // authentification auprès du serveur
                        if (Communication.login(username, password)) {
                            Configurations.username = username;

                           pdia.setMessage("Connection accepted!");

                           // initialise le client http
                            okHttpClient = new OkHttpClient();



                            //---/-/-/-/-/-/-/-/-/-/-/-/-/-Rocket------------   -/--------- -   -   -   -   -   -/


                            //il faut mettre une methode qui demande le nom pour la map et le remplacer dans fileName

                            String urlRocket = Configurations.serverHttpUrl + "/config/rockets";


                            //create a request to the server adress
                            request = new Request.Builder().url(urlRocket).get().build();

                            okHttpClient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {
                                    Log.i(TAGRocket, "connexion non etablie");
                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
                                    String stg;
                                    Log.i("rocket Files name", stg = response.body().string());

                                    try {
                                        JSONObject rocketJson = new JSONObject(stg);
                                        Log.i(TAGRocket,rocketJson.getString("file0"));
                                        Log.i(TAGRocket,rocketJson.getString("file1"));

                                        setFuseeName(rocketJson.getString("file0"));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    if(response.code()==200)
                                    {
                                        Log.i(TAGRocket, "Rocket chargée");

                                    }else{
                                        Log.i(TAGMap, "la Rocket n'est pas chargée");

                                    }


                                }
                            });





                            //- -   -   -   -   -   -   -   -   -   -   -   --      --      -   --  -   -   -   /



                            //----------    --------    ---------   ----------  ---------   -------//

                    //Telechargement de la map lors de la connection du user
                    // a decommenter
                    String urlBasic = Configurations.serverHttpUrl + "/config/basic";

                    //create a request to the server adress
                    request = new Request.Builder().url(urlBasic).get().build();
                    //create a call back
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Log.i(TAGMap, "connexion non etablie");
                        }

                        @Override
                        public void onResponse(final Response response) throws IOException {
                            Log.i(TAGMap, response.body().toString());



                            try {
                                String jsonData = response.body().string();
                                JSONObject nomMap = new JSONObject(jsonData);

                                setNomMap(nomMap.getString("map"));
                                setPort(nomMap.getInt("otherPort"));

                                Log.i(TAGMap, nomMap.getString("map"));
                                Log.i(TAGMap, "..." + nomMap.getInt("otherPort"));



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(response.code()==200)
                            {
                                Log.i(TAGMap, "Map chargée");

                            }else{
                                Log.i(TAGMap, "la Map n'est pas chargée");

                            }

                        }
                    });



                    //---/-/-/-/-/-/-/-/-/-/-/-/-/-nom des Pdf------------   -/--------- -   -   -   -   -   -/


                    final String urlNomPdf = Configurations.serverHttpUrl + "/config/miscFiles";


                    //create a request to the server adress
                    request = new Request.Builder().url(urlNomPdf).get().build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Log.i(TAGPDF, "connexion non etablie");
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            String s;
                            Log.i(TAGPDF,  s = response.body().string());



                            try {
                                JSONObject pdfJson = new JSONObject(s);

                                String pdfUrl = urlNomPdf + "/" + pdfJson.getString("file0");
                                Log.i("Url",pdfUrl);
                                setFile(pdfUrl);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if(response.code()== 200)
                            {
                                Log.i(TAGPDF, "les noms des pdf chargée");

                            }
                            if(response.code()== 401)
                            {
                                Log.i(TAGPDF, "les noms des pdf n'est pas chargée");

                            }


                        }
                    });





                    //- -   -   -   -   -   -   -   -   -   -   -   --      --      -   --  -   -   -   /



                    //---/-/-/-/-/-/-/-/-/-/-/-/-/-Rocket names------------   -/--------- -   -   -   -   -   -/


                    //il faut mettre une methode qui demande le nom pour la map et le remplacer dans fileName


                    final String urlRocketName = Configurations.serverHttpUrl + "/config/rockets/" + "10_polaris.xml";


                    //create a request to the server adress
                    request = new Request.Builder().url(urlRocketName).get().build();


                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Log.i(TAGRocket, "connexion non etablie");
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            String fuseName ;
                            Log.i(TAGRocket, fuseName = response.body().string());
                            setFuseeName(fuseName);


                            if(response.code()==200)
                            {
                                Log.i(TAGRocket, "la rocket" + getFuseeName() + " est chargée");

                            }
                            if(response.code()==401){
                                Log.i(TAGMap, "401: non autorisé");

                            }
                            if(response.code()==404){
                                Log.i(TAGMap, "404 : fichier inexistant");

                            }


                        }
                    });


                    //- -   -   -   -   -   -   -   -   -   -   -   --      --      -   --  -   -   -   /



                    //------------------------------------Map -------------------------------------------- -//


                    //il faut mettre une methode qui demande le nom pour la map et le remplacer dans fileName

                    String urlMap = Configurations.serverHttpUrl + "/config/map";


                    //create a request to the server adress
                    request = new Request.Builder().url(urlMap).get().build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Log.i(TAGRocket, "connexion non etablie");
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            Log.i(TAGRocket, response.body().string());

                            if(response.code()==200)
                            {
                                Log.i(TAGRocket, "200: Map chargée");

                            }else{
                                Log.i(TAGMap, "la Map n'est pas chargée");

                            }


                        }
                    });





                    //- -   -   -   -   -   -   -   -   -   -   -   --      --      -   --  -   -   -   /


                    //---/-/-/-/-/-/-/-/-/-/-/-/-/-pdf by names------------   -/--------- -   -   -   -   -   -/


                    //il faut mettre une methode qui demande le nom pour la map et le remplacer dans fileName

                    final String pdfName = "spec.pdf";

                    final String urlPdfName = Configurations.serverHttpUrl + "/config/miscFiles/" + pdfName;


                    //create a request to the server adress
                    request = new Request.Builder().url(urlPdfName).get().build();


                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Log.i(TAGPDF, "connexion non etablie");
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            Log.i(TAGPDF, response.body().string());


                            if(response.code()==200)
                            {
                                Log.i(TAGPDF, "le fichier" + pdfName + " est chargée");

                            }
                            if(response.code()==401){
                                Log.i(TAGPDF, "401: non autorisé");

                            }
                            if(response.code()==404){
                                Log.i(TAGPDF, "404 : fichier inexistant");

                            }


                        }
                    });


                    //- -   -   -   -   -   -   -   -   -   -   -   --      --      -   --  -   -   -   /


                            // ferme le widget qui informae de la tentative de connection
                            pdia.dismiss();
                            // start next fragment ModeReception
                            onStartClick();

                        } // end of <if conection is successful>
                        else pdia.setMessage("Connection failed!");
                        return null;
                    }
                    // ferme le widget qui informe de la tentative de connection en cas de raté
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        try {
                            sleep(Configurations.connectTimeout/4);
                            pdia.dismiss();
                        }catch (Exception e){}
                    }
                // lance la tache asynchrone
                }.execute();
            }//end of click boutton connect

        });
    }


    /*
    * Pour ouvrir le layout de mode de reception
    */
    public void onStartClick(){
        Intent intent = new Intent(this, AffichageActivity.class);
        intent.putExtra("USER_NAME", editUsername.getText().toString());
        startActivity(intent);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

