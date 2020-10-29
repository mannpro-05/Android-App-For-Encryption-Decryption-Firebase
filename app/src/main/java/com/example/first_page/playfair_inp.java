package com.example.first_page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

public class playfair_inp extends AppCompatActivity {

    EditText pt,key;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    AlertDialog.Builder builder;
    Data data;
    long maxid = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playfair_inp);
        pt = findViewById(R.id.pt);
        key = findViewById(R.id.key);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Playfair").child(mAuth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    maxid = dataSnapshot.getChildrenCount();
                }
                System.out.println(maxid);
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
    public int search(char a[][], char target)
    {
        int flag = 0;
        for (int i=0;i<5;i++)
            for (int j=0;j<5;j++)
            {
                if (a[i][j] == target || target == 'j')
                {
                    flag=1;
                    return flag;
                }
            }

        return flag;
    }
    public char[][] matrixformation(String  key)
    {
        int counter = 0,m=97,flag;
        char[][] c = new char[5][5];
        char temp;
        for (int i=0;i<5;i++)
            for (int j=0;j<5;j++)
            {

                if(counter!=key.length())
                {
                    temp = key.charAt(counter);
                    flag = search(c,temp);
                    if (flag != 1)
                        c[i][j] = key.charAt(counter);
                    else
                        j--;
                    counter++;
                }

                else
                {
                    flag = search(c,(char)m);
                    if (flag != 1)
                    {
                        c[i][j] = (char) m;
                        m++;
                    }

                    else
                    {
                        m++;
                        j--;
                    }
                }

            }

        return c;
    }

    public String[] ptformation(String pt)
    {
        for (int i=0;i < pt.length()-1;)
        {
            if(pt.charAt(i) == pt.charAt(i+1))
            {
                pt = pt.substring(0,i+1)+"x"+pt.substring(i+1);
                i++;
            }
            i+=2;

        }
        if (pt.length()%2!=0)
            pt+='z';
        System.out.println("ptlength"+pt);
        String[] new_pt = new String[pt.length()/2];
        int temp=0;
        for (int i=0;i<new_pt.length;i++)
        {
            new_pt[i] = pt.substring(temp,temp+2);
            temp+=2;
        }


        return new_pt;
    }


    public int[] findpos(char[][] key,char target)
    {
        int[] pos = new int[2];

        for (int i=0;i<5;i++)
            for (int j=0;j<5;j++)
                if (key[i][j] == target)
                {
                    pos[0] = i;
                    pos[1] = j;
                    break;

                }

        return pos;

    }


    public void encrypt(View view) {
        String pt_1 = pt.getText().toString(), key_1 = key.getText().toString(),ct="",temp="";
        if (pt_1.equals(""))
            pt.setError("This field is empty!!");

        if (key_1.equals(""))
            key.setError("This field is empty!!");

        else {


            char[][] key_fianl = matrixformation(key_1);
            String[] pt_fianl = ptformation(pt_1);
            int r1, r2, c1, c2;
            int[] pos;
            for (int i = 0; i < pt_fianl.length; i++) {
                temp = pt_fianl[i];

                pos = findpos(key_fianl, temp.charAt(0));
                r1 = pos[0];
                c1 = pos[1];
                pos = findpos(key_fianl, temp.charAt(1));
                r2 = pos[0];
                c2 = pos[1];
                if (r1 == r2) {
                    if (c1 + 1 > 4)
                        ct += (char) key_fianl[r1][0] + "" + key_fianl[r2][c2 + 1];
                    else if (c2 + 1 > 4)
                        ct += (char) key_fianl[r1][c1 + 1] + "" + key_fianl[r2][0];
                    else
                        ct += (char) key_fianl[r1][c1 + 1] + "" + key_fianl[r2][c2 + 1];
                } else if (c1 == c2) {

                    if (r1 + 1 > 4)
                        ct += (char) key_fianl[0][c1] + "" + key_fianl[r2 + 1][c2];
                    else if (r2 + 1 > 4)
                        ct += (char) key_fianl[r1 + 1][c1] + "" + key_fianl[0][c2];
                    else
                        ct += (char) key_fianl[r1 + 1][c1] + "" + key_fianl[r2 + 1][c2];
                } else {
                    ct += (char) key_fianl[r1][c2] + "" + key_fianl[r2][c1];
                }

            }
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            data = new Data();
            data.setPt(pt_1);
            data.setMode("Encryption");
            data.setOutput(ct);
            data.setDate(date);
            data.setTime(time);
            data.setKey(key_1);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        maxid = dataSnapshot.getChildrenCount();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            reference.child(String.valueOf(maxid+1)).setValue(data);

            Intent intent = new Intent(this,playfair_output.class);

            intent.putExtra("ct", ct);
            Bundle b = new Bundle();
            b.putSerializable("key", key_fianl);
            intent.putExtra("method", "encryption");
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    public void decrypt(View view) {
        String ct_1 = pt.getText().toString(), key_1 = key.getText().toString(),pt_1="",temp="";
        if (ct_1.equals(""))
            pt.setError("This field is empty!!");

        if (key_1.equals(""))
            key.setError("This field is empty!!");

        else {
            char[][] key_fianl = matrixformation(key_1);
            String[] ct_fianl = ptformation(ct_1);
            for(int i=0;i<ct_fianl.length;i++)
                System.out.println(ct_fianl[i]);
            int r1, r2, c1, c2;
            int[] pos;
            for (int i = 0; i < ct_fianl.length; i++) {
                temp = ct_fianl[i];

                pos = findpos(key_fianl, temp.charAt(0));
                r1 = pos[0];
                c1 = pos[1];
                pos = findpos(key_fianl, temp.charAt(1));
                r2 = pos[0];
                c2 = pos[1];
                if (r1 == r2) {
                    if (c1 - 1 < 0)
                        pt_1 += (char) key_fianl[r1][4] + "" + key_fianl[r2][c2 - 1];
                    else if (c2 - 1 < 0)
                        pt_1 += (char) key_fianl[r1][c1 - 1] + "" + key_fianl[r2][4];
                    else
                        pt_1 += (char) key_fianl[r1][c1 - 1] + "" + key_fianl[r2][c2 - 1];
                } else if (c1 == c2) {

                    if (r1 - 1 < 0)
                        pt_1 += (char) key_fianl[4][c1] + "" + key_fianl[r2 - 1][c2];
                    else if (r2 - 1 < 0)
                        pt_1 += (char) key_fianl[r1 - 1][c1] + "" + key_fianl[4][c2];
                    else
                        pt_1 += (char) key_fianl[r1 - 1][c1] + "" + key_fianl[r2 - 1][c2];
                } else {
                    pt_1 += (char) key_fianl[r1][c2] + "" + key_fianl[r2][c1];
                }

            }

            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            data = new Data();
            data.setPt(ct_1);
            data.setMode("Decryption");
            data.setOutput(pt_1);
            data.setDate(date);
            data.setTime(time);
            data.setKey(key_1);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        maxid = dataSnapshot.getChildrenCount();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            reference.child(String.valueOf(maxid + 1)).setValue(data);

            Intent intent = new Intent(this, playfair_output.class);

            intent.putExtra("pt", pt_1);
            Bundle b = new Bundle();
            b.putSerializable("key", key_fianl);
            intent.putExtra("method", "decryption");
            intent.putExtras(b);
            startActivity(intent);
        }
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
                            maxid = 0;
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
                    startActivity(new Intent(this, playfair_display.class));
                    break;

                case R.id.clrhistory:
                    builder.setMessage("Do you want Clear Your History ?").setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            reference.removeValue();
                            maxid = 0;
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
