package com.example.visitormanagement;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;

public class Welcome_Activity extends AppCompatActivity {

    private Button start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_);

        start = findViewById(R.id.start);

        if(getIntent().hasExtra("flag"))
            Snackbar.make(start,getIntent().getExtras().get("flag").toString(),Snackbar.LENGTH_LONG).show();

        //Requesting Permissions
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(DialogOnAnyDeniedMultiplePermissionsListener.Builder
                .withContext(getApplicationContext())
                .withTitle("Camera & Storage permission")
                .withMessage("Both camera and Storage permission are needed to take pictures of your cat")
                .withButtonText(android.R.string.ok)
                .build()).check();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Welcome_Activity.this, Camera.class);
                startActivity(intent);
            }
        });
    }
}
