package com.example.mounia.client.Activites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.example.mounia.client.R;
import com.example.mounia.client.Utilitaires.Configurations;
import com.github.barteksc.pdfviewer.PDFView;

import static com.example.mounia.client.Utilitaires.Configurations.getFile;

public class PdfViewerActivity extends AppCompatActivity {

    PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configurations.setTheme(this);

        setContentView(R.layout.activity_pdf_viewer);

        pdfView = (PDFView) findViewById(R.id.pdfView);


       // pdfView.fromAsset("spec.pdf").load();
        // read a pdf from url
        //TODO .. il faut changer le url !

        WebView webview = new WebView(this);
        webview.getSettings().setUserAgentString(" ");

        setContentView(webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(getFile());


    }


}
