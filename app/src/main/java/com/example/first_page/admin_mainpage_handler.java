package com.example.first_page;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class admin_mainpage_handler extends AppCompatActivity {
    TextView tv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_mainpage);
        tv = findViewById(R.id.textView4);
    }

    public void hello(View view) {
        tv.setText("Hello");
    }
}
