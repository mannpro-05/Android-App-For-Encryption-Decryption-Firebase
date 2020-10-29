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

import java.security.KeyStore;

public class diffie_history extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference reference;
    WebView wb;
    String key_val="",alice_val="",bob_val="";
    long aliceid,bobid,keyid;
    int a,k,b;
    public void alice(String alice_data)
    {
        alice_val += alice_data;
    }
    public void bob(String bob_data)
    {
        bob_val += bob_data;
    }
    public void key(String key_data)
    {
        key_val += key_data;
    }
    public int alice_initialize()
    {
        long r = aliceid;
        for (long i=1;i<=r;i++)
        {
            reference = FirebaseDatabase.getInstance().getReference("Diffi hell man").child(mAuth.getCurrentUser().getUid()).child("Alice").child(String.valueOf(i));
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String alice_pt = dataSnapshot.child("alice_pt").getValue().toString();
                        String alice_send = dataSnapshot.child("alice_sent").getValue().toString();
                        String alice_receive = dataSnapshot.child("alice_receive").getValue().toString();
                        String time = dataSnapshot.child("time").getValue().toString();
                        String date = dataSnapshot.child("date").getValue().toString();
                        String alice_data = alice_pt+ "," +alice_send+ "," +alice_receive+ "," +date+ "," +time+"@";
                        alice(alice_data);
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
        return 1;
    }

    public int bob_initialize() {
        long r = bobid;
        for (long i = 1; i <= r; i++) {
            reference = FirebaseDatabase.getInstance().getReference("Diffi hell man").child(mAuth.getCurrentUser().getUid()).child("bob").child(String.valueOf(i));
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String bob_pt = dataSnapshot.child("bob_pt").getValue().toString();
                        String bob_send = dataSnapshot.child("bob_sent").getValue().toString();
                        String bob_receive = dataSnapshot.child("bob_receive").getValue().toString();
                        String time = dataSnapshot.child("time").getValue().toString();
                        String date = dataSnapshot.child("date").getValue().toString();
                        String bob_data = bob_pt + "," + bob_send + "," + bob_receive + "," + date + "," + time + "@";
                        bob(bob_data);
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return 1;
    }

    public int key_initialize() {
        long r = keyid;
        for (long i = 1; i <= r; i++) {
            reference = FirebaseDatabase.getInstance().getReference("Diffi hell man").child(mAuth.getCurrentUser().getUid()).child("Key").child(String.valueOf(i));
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String key_alice = dataSnapshot.child("alice_private").getValue().toString();
                        String key_bob = dataSnapshot.child("bob_private").getValue().toString();
                        String key_p = dataSnapshot.child("p").getValue().toString();
                        String key_g = dataSnapshot.child("g").getValue().toString();
                        String key_secret = dataSnapshot.child("secreat_key").getValue().toString();
                        String time = dataSnapshot.child("time").getValue().toString();
                        String date = dataSnapshot.child("date").getValue().toString();
                        String key_data = key_alice + "," + key_bob + "," + key_p + "," +key_g+ "," +key_secret + "," +date + "," + time + "@";
                        System.out.println(key_data);
                        key(key_data);
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diffie_history);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Diffi hell man").child(mAuth.getCurrentUser().getUid());
        wb = findViewById(R.id.webView_diffi);
        Intent intent = getIntent();
        aliceid = intent.getLongExtra("alice",0);
        reference.child("Alice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    aliceid = dataSnapshot.getChildrenCount();
                    System.out.println("hey"+aliceid);
                    if (aliceid!=0)
                    {
                        bobid = aliceid;
                        keyid = aliceid;
                        bob_initialize();
                        key_initialize();
                        alice_initialize();
                        final long finalR = aliceid;
                        final long finalC = 5;
                        final long finalC_keys = 7;
                        wb.getSettings().setJavaScriptEnabled(true);
                        wb.loadUrl("file:///android_asset/diffi_display.html");
                        wb.setWebViewClient(new WebViewClient(){
                            public void onPageFinished(WebView view, String url){
                                System.out.println("hello"+key_val+"bob:0"+bob_val);
                                wb.loadUrl("javascript:view('"+ alice_val +"','"+ bob_val +"','"+ key_val +"','"+ finalR +"','"+ finalC +"','"+ finalC_keys +"')");
                            }
                        });
                    }

                    }
                else{
                    final long finalR = 0;
                    final long finalC = 0;
                    final long finalC_keys = 0;
                    wb.getSettings().setJavaScriptEnabled(true);
                    wb.loadUrl("file:///android_asset/diffi_display.html");
                    wb.setWebViewClient(new WebViewClient(){
                        public void onPageFinished(WebView view, String url){
                            wb.loadUrl("javascript:view('"+ alice_val +"','"+ bob_val +"','"+ key_val +"','"+ finalR +"','"+ finalC +"','"+ finalC_keys +"')");
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
