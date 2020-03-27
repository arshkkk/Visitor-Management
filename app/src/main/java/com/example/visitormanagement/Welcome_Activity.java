package com.example.visitormanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class Welcome_Activity extends AppCompatActivity {

    private Button start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_);

        start = findViewById(R.id.start);

        if(getIntent().hasExtra("flag"))
            Snackbar.make(start,getIntent().getExtras().get("flag").toString(),Snackbar.LENGTH_LONG).show();


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Welcome_Activity.this, Camera.class);
                startActivity(intent);
            }
        });
    }
}
