package com.example.first_page;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class diffie_display extends AppCompatActivity {

    ImageView iv,iv1;
    EditText alice,bob;
    Button en,de,send,newmsg;
    TextView a_text,b_text;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    long aliceid,bobid,keyid;
    diffi_key data_key;
    diffi_alice data_alice;
    diffi_bob data_bob;
    String key_val="",alice_val="",bob_val="";


    String a_ct,b_ct,a_pt,b_pt,a_msg,b_msg;
    int flag=0;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diffie_display);
        iv = findViewById(R.id.imageView);
        iv1 = findViewById(R.id.imageView2);
        alice = findViewById(R.id.editText);
        bob = findViewById(R.id.editText2);
        en = findViewById(R.id.encrypt);
        de = findViewById(R.id.decrypt);
        send = findViewById(R.id.send);
        a_text = findViewById(R.id.alice_text);
        b_text = findViewById(R.id.bob_text);
        newmsg = findViewById(R.id.newmsg);
        iv1.setImageResource(R.drawable.female);
        iv.setImageResource(R.drawable.male);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Diffi hell man").child(mAuth.getCurrentUser().getUid());
        reference.child("Alice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    aliceid = dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("bob").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    bobid = dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("Key").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    keyid = dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (mAuth.getCurrentUser()==null)
        {
            startActivity(new Intent(getApplicationContext(),login.class));
            finish();
        }
        builder = new AlertDialog.Builder(this);
    }




    public String encryption(String pt, long key)
    {
        String ct="";
        for (int i = 0;i < pt.length();i++)
        {
            char temp = pt.charAt(i);
            if (Character.isUpperCase(temp))
                ct += ((char)((((((int)temp) - 65) + key) % 26) + 65));
            else if ((int)temp == 32)
                ct += " ";
            else
                ct += ((char)((((((int)temp) - 97) + key) % 26) + 97));
        }
        return ct;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String decryption(String ct, long key)
    {
        String pt="";
        for (int i = 0;i < ct.length();i++)
        {
            char temp = ct.charAt(i);
            if (Character.isUpperCase(temp))
                pt += ((char)((Math.floorMod(((((int)temp) - 65) - key),26)  + 65)));
            else if ((int)temp == 32)
                pt += " ";
            else
                pt += ((char)((Math.floorMod(((((int)temp) - 97) - key),26)  + 97)));
        }

        return pt;
    }

    public void encrypt(View view) {
        a_msg = alice.getText().toString();
        b_msg = bob.getText().toString();

        if (a_msg.equals(""))
            alice.setError("Field is Empty!!");
        if (b_msg.equals(""))
            bob.setError("Field is Empty!!");
        else {
            builder.setMessage("Do you want to Encrypt the data now ? After selecting YES you won't be able to alter the message.If you want to change the message then select NO").setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = getIntent();
                    long secret_key = intent.getLongExtra("secret_key",0);
                    a_ct = encryption(a_msg, secret_key);
                    b_ct = encryption(b_msg, secret_key);
                    alice.setText(a_ct);
                    bob.setText(b_ct);
                    a_text.setText("Alice's Encrypted Message is:");
                    b_text.setText("Bob's Encrypted Message is:");
                    en.setVisibility(View.INVISIBLE);
                    send.setVisibility(View.VISIBLE);
                    alice.setInputType(InputType.TYPE_NULL);
                    bob.setInputType(InputType.TYPE_NULL);
                    alice.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    bob.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    flag = 0;
                }

            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog en = builder.create();
            en.setTitle("Encrypt The Messages.");
            en.show();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void decrypt(View view) {
        Intent intent = getIntent();
        long secret_key = intent.getLongExtra("secret_key",0);
        a_pt = decryption(alice.getText().toString(), secret_key);
        b_pt = decryption(bob.getText().toString(), secret_key);
        a_text.setText("Decrypted Message Received By Alice from Bob is:");
        b_text.setText("Decrypted Message Received By Bob from Alice is:");
        alice.setText(a_pt);
        bob.setText(b_pt);
        newmsg.setVisibility(View.VISIBLE);
        de.setVisibility(View.INVISIBLE);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        data_key = new diffi_key();
        data_bob = new diffi_bob();
        data_alice = new diffi_alice();
        data_alice.setAlice_pt(a_msg);
        data_alice.setAlice_receive(a_pt);
        data_bob.setBob_pt(b_msg);
        data_bob.setBob_receive(b_pt);
        data_key.setSecreat_key(String.valueOf(secret_key));
        data_key.setAlice_private(String.valueOf(intent.getLongExtra("alice",0)));
        data_key.setBob_private(String.valueOf(intent.getLongExtra("bob",0)));
        data_key.setP(String.valueOf(intent.getLongExtra("p",0)));
        data_key.setG(String.valueOf(intent.getLongExtra("g",0)));
        data_alice.setAlice_sent(a_ct);
        data_bob.setBob_sent(b_ct);
        data_key.setDate(date);
        data_key.setTime(time);
        data_alice.setDate(date);
        data_alice.setTime(time);
        data_bob.setDate(date);
        data_bob.setTime(time);

        reference.child("Alice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    aliceid = dataSnapshot.getChildrenCount();
                    System.out.println("Alice: "+aliceid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("bob").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    bobid = dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("Key").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    keyid = dataSnapshot.getChildrenCount();
                    System.out.println("Key: "+keyid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("Key").child(String.valueOf(keyid + 1)).setValue(data_key);
        reference.child("Alice").child(String.valueOf(aliceid + 1)).setValue(data_alice);
        reference.child("bob").child(String.valueOf(bobid + 1)).setValue(data_bob);
        flag=1;
    }

    public void send(View view) {

        String temp = alice.getText().toString();
        alice.setText(bob.getText().toString());
        bob.setText(temp);
        a_text.setText("Encrypted Message Received By Alice from Bob is:");
        b_text.setText("Encrypted Message Received By Bob from Alice is:");
        send.setVisibility(View.INVISIBLE);
        de.setVisibility(View.VISIBLE);
        flag=0;
    }


    public void newmsg(View view) {
        builder.setMessage("Do you want to send a New Message With Different Set of Keys ? Or want to stick to the same Keys ?").setCancelable(false).setPositiveButton("YES Try New Keys", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(diffie_display.this,deffi_key.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"Choose Your Keys!!.", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("I'll Stick to the keys", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alice.setInputType(InputType.TYPE_CLASS_TEXT);
                bob.setInputType(InputType.TYPE_CLASS_TEXT);
                alice.setText("");
                bob.setText("");
                a_text.setText("");
                b_text.setText("");
                en.setVisibility(View.VISIBLE);
                newmsg.setVisibility(View.INVISIBLE);
                flag = 0;
            }
        });
        AlertDialog alert = builder.create();
        alert.setTitle("Change Keys.");
        alert.show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (flag!=1)
            Toast.makeText(this, "The Activity was destroyed so the Message was not sent hence the data was not stored!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mAuth.getCurrentUser().getEmail().equals("admin@gmail.com"))
        {
            getMenuInflater().inflate(R.menu.admin,menu);
        }
        else
        {
            getMenuInflater().inflate(R.menu.removedb,menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (mAuth.getCurrentUser().getEmail().equals("admin@gmail.com")) {

            switch (id) {

                case R.id.logout:
                    builder.setMessage("Are you sure that you want to logout ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAuth.signOut();
                            startActivity(new Intent(getApplicationContext(), login.class));
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle("logout.");
                    alertDialog.show();
                    break;

                case R.id.viewhis:
                    startActivity(new Intent(this, playfair_display.class));
                    break;

                case R.id.clrhistory:
                    builder.setMessage("Do you want Clear Your History ?").setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            reference.removeValue();
                            aliceid = 0;
                            Toast.makeText(getApplicationContext(), "Your history has been Deleted!!.", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Clear History");
                    alert.show();
                    break;
                case R.id.mainpage:
                    builder.setMessage("Are you sure that you want to go to the main page ?").setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(getApplicationContext(),Navigation.class));
                            Toast.makeText(getApplicationContext(), "Your history has been Deleted!!.", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertd = builder.create();
                    alertd.setTitle("main Menu");
                    alertd.show();
                    break;
            }
        } else {
            switch (id) {

                case R.id.logout:
                    builder.setMessage("Are you sure that you want to logout ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAuth.signOut();
                            startActivity(new Intent(getApplicationContext(), login.class));
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle("logout.");
                    alertDialog.show();
                    break;

                case R.id.viewhis:
                    startActivity(new Intent(this, diffie_history.class));
                    break;

                case R.id.clrhistory:
                    builder.setMessage("Do you want Clear Your History ?").setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            reference.removeValue();
                            aliceid = 0;
                            Toast.makeText(getApplicationContext(), "Your history has been Deleted!!.", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Clear History");
                    alert.show();
                    break;
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
