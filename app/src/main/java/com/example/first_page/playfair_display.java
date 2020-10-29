package com.example.first_page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class playfair_display extends AppCompatActivity {
    WebView wb;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    long id;
    String fdata="";
    public void copy(String data)
    {
        fdata+=data;
    }
    public void history()
    {
        final long r;
        if (id == 0) {
            return;
        } else {
            r = id;
            for (int i = 1; i <= r; i++) {
                {
                    reference = FirebaseDatabase.getInstance().getReference("Playfair").child(mAuth.getCurrentUser().getUid()).child(String.valueOf(i));
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                String inp = dataSnapshot.child("pt").getValue().toString();
                                String mode = dataSnapshot.child("mode").getValue().toString();
                                String key = dataSnapshot.child("key").getValue().toString();
                                String opt = dataSnapshot.child("output").getValue().toString();
                                String date = dataSnapshot.child("date").getValue().toString();
                                String time = dataSnapshot.child("time").getValue().toString();
                                String data_1 = inp +","+ key +","+ mode +","+ opt +","+ date +","+ time+"@";
                                copy(data_1);
                            }
                            catch (Exception e)
                            {

                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playfair_display);
        wb = findViewById(R.id.webView);
        wb.getSettings().setJavaScriptEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()==null)
        {
            startActivity(new Intent(getApplicationContext(),login.class));
            finish();
        }
        reference = FirebaseDatabase.getInstance().getReference("Playfair").child(mAuth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    id = dataSnapshot.getChildrenCount();
                if (id!=0)
                {
                    history();

                    wb.loadUrl("file:///android_asset/playfair_display.html");
                    final long c = 6,r = id;
                    wb.setWebViewClient(new WebViewClient() {
                        public void onPageFinished(WebView view, String url) {

                            wb.loadUrl("javascript:view('" + fdata + "','" + r + "','" + c + "')");
                        }
                    });
                }
                else
                {
                    final long c = 0,r = id;
                    wb.loadUrl("file:///android_asset/playfair_display.html");
                    wb.setWebViewClient(new WebViewClient() {
                        public void onPageFinished(WebView view, String url) {
                            wb.loadUrl("javascript:view('" + fdata + "','" + r + "','" + c + "')");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
