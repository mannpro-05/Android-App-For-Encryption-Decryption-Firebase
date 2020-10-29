package com.example.first_page;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class playfair_output extends AppCompatActivity {

    WebView wb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playfair_output);
        wb = findViewById(R.id.webView2);
        wb.getSettings().setJavaScriptEnabled(true);
        Intent intent = getIntent();
        String key = "";
        String method = intent.getStringExtra("method");
        char[][] key_arr = (char[][])intent.getSerializableExtra("key");
        for (int i = 0; i<5;i++)
            for (int j = 0;j<5;j++)
                key += Character.toUpperCase(key_arr[i][j]);

        final String finalKey = key;
        wb.loadUrl("file:///android_asset/playfair_encryption.html");
        if(method.equals("encryption"))
        {
            System.out.println(key);
            final String ct = intent.getStringExtra("ct");

            wb.setWebViewClient(new WebViewClient(){
                public void onPageFinished(WebView view, String url){
                    wb.loadUrl("javascript:output_en('"+ ct +"','"+ finalKey +"')");
                }
            });

        }
        else
        {
            final String pt = intent.getStringExtra("pt");
            wb.setWebViewClient(new WebViewClient(){
                public void onPageFinished(WebView view, String url){
                    wb.loadUrl("javascript:output_de('"+ pt +"','"+ finalKey +"')");
                }
            });
        }


    }
}
