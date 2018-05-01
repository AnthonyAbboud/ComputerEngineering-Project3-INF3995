package com.example.mounia.tp3;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;


import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Attributs privés
    VerticalViewPager viewPager;
    Adapteur adapteur;
    static final int NOMBRE_DE_PAGE = 5;
    Button footer_btn;
    private int positionCourante;
    URL url;
    URL urlText;
    ImageView imageView;
    /*
    //pour le test hors serveur sans FPGA
    String url3 = "https://tr1.cbsistatic.com/hub/i/r/2014/05/16/365c7d15-dc77-412d-88d7-0e03dbd048b0/resize/770x/95fc2b206107b9c836bb38168cbcffd6/1024px-logoandroid051614.png";
    String url2 = "http://samples.openweathermap.org/data/3.0/triggers?appid=b1b15e88fa797225412429c1c50c122a1";
    String url1 = "https://media.licdn.com/mpr/mpr/shrinknp_200_200/AAEAAQAAAAAAAAVYAAAAJGRiY2YyNTMwLTQ5NTEtNDI1Yi1iNTRjLTVmNjJmYzU5MGM4OA.jpg";
    //*/

    //pour test serveur

    //url Error
    //String url3 = "http://132.207.89.92/images/test.popopo";

    //*
    //url
    String url3 = "http://132.207.89.92/test2";
    //text
    String url2 = "http://132.207.89.92/test1";
    //photo
    String url1 = "http://132.207.89.92/images/test.png";
    //*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapteur = new Adapteur(getSupportFragmentManager());
        viewPager = findViewById((R.id.viewpager));
        viewPager.setAdapter(adapteur);
        viewPager.setCurrentItem(1,false);
        footer_btn = findViewById(R.id.boutonFooter);
        footer_btn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewPager.setCurrentItem(1,false);

                // Reactiver les boutons
                //getImageButton.setEnabled(true);
                //getUrlButton.setEnabled(true);
                //getTextButton.setEnabled(true);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                if (Build.VERSION.SDK_INT >= 18) {
                    // Essayer d'effacer les reponses des requetes
                    if (textWebView != null) textWebView.loadUrl("about:blank");
                    if (urlWebView != null) urlWebView.loadUrl("about:blank");
                    if (webViewImage != null) webViewImage.loadUrl("about:blank");
                }
            }

            @Override
            public void onPageSelected(int position)
            {
                positionCourante = position;
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
                if (positionCourante == 0)
                    viewPager.setCurrentItem(3, false);
                if (positionCourante == 4)
                    viewPager.setCurrentItem(1, false);
            }
        });
    }

    // Fonction qui permet de faire la connexion entre l'app et le serveur
    WebView urlWebView = null;
    public void getURL(View view)
    {
        TextView textView = findViewById(R.id.TextURL);
        textView.setText("Voici le texte !");
        urlWebView = findViewById(R.id.webViewUrl);
        urlWebView.getSettings().setJavaScriptEnabled(true);
        if(!url3.equals("http://132.207.89.92/test2"))
        {
            Toast.makeText(this, "Veullez verifier votre Url", Toast.LENGTH_LONG).show();
        }
        urlWebView.loadUrl(url3);

        // desactiver le bouton
        //getUrlButton = view;
        //view.setEnabled(false);
    }

    private View getTextButton = null;
    private View getUrlButton = null;
    private View getImageButton = null;

    // Fonction qui permet de faire la connexion entre l'app et le serveur
    WebView webViewImage;
    public void getImage(View view) {
        webViewImage = findViewById(R.id.webViewImage);
        webViewImage.getSettings().setJavaScriptEnabled(true);
        webViewImage.loadUrl(url1);

        // desactiver le bouton
        //getImageButton = view;
        //view.setEnabled(false);
    }

    // Fonction qui permet de faire la connexion entre l'app et le serveur
    WebView textWebView;
    public void getText(View view) {
        TextView textView = (TextView) findViewById(R.id.Text);
        textView.setText("Voici le texte !");
        textWebView = findViewById(R.id.webView);
        textWebView.getSettings().setJavaScriptEnabled(true);
        textWebView.loadUrl(url2);

        // desactiver le bouton
        //getTextButton = view;
        //view.setEnabled(false);
    }

    // Class Adapteur
    public static class Adapteur extends FragmentPagerAdapter
    {
        ArrayList<Fragment> listeFragment = new ArrayList();

        public Adapteur(FragmentManager fragmentManager)
        {
            super(fragmentManager);
            // Initialisation de nos fragments
            listeFragment.add(FragmentImage.newInstance(0));
            listeFragment.add(FragmentTexte.newInstance(1));
            listeFragment.add (FragmentURL.newInstance(2));
            listeFragment.add(FragmentImage.newInstance(3));
            listeFragment.add(FragmentTexte.newInstance(4));
        }

        // Cette methode retourne le nombre de pages
        @Override
        public int getCount()
        {
           return NOMBRE_DE_PAGE;
        }

        // cette methode retourne le fragement selon la position
        @Override
        public Fragment getItem(int position)
        {
            int i = position % listeFragment.size();
            return listeFragment.get(i);
        }
    }

    // Classe FragementImage est associé au fragement_image.xml
    public static class FragmentImage extends Fragment
    {
        private static final String MY_NUM_KEY = "num";

        static FragmentImage newInstance(int num)
        {
            FragmentImage f = new FragmentImage();
            Bundle args = new Bundle();
            args.putInt(MY_NUM_KEY, num);
            f.setArguments(args);
            return f;
        }

        public FragmentImage()
        {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            return inflater.inflate(R.layout.fragment_image, container, false);
        }

    }

    // Classe FragementTexte est associé au fragement_text.xml
    public static class FragmentTexte extends Fragment
    {
        private static final String MY_NUM_KEY = "num";

        static FragmentTexte newInstance(int num)
        {
            FragmentTexte f = new FragmentTexte();
            Bundle args = new Bundle();
            args.putInt(MY_NUM_KEY, num);
            f.setArguments(args);
            return f;
        }

        public FragmentTexte()
        {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            return inflater.inflate(R.layout.fragment_text, container, false);
        }
    }

    // Classe FragementURL est associé au fragement_url.xml
    public static class FragmentURL extends Fragment
    {
        private static final String MY_NUM_KEY = "num";

        static FragmentURL newInstance(int num)
        {
            FragmentURL f = new FragmentURL();
            Bundle args = new Bundle();
            args.putInt(MY_NUM_KEY, num);
            f.setArguments(args);
            return f;
        }

        public FragmentURL()
         {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            return inflater.inflate(R.layout.fragment_url, container, false);
        }
    }
}
