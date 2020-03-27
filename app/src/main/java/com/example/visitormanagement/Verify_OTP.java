package com.example.visitormanagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.textview.MaterialTextView;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

public class Verify_OTP extends AppCompatActivity {

    private OtpView otpView;
    private MaterialTextView timer;
    private String defaultStartString = "Time Left  0:";
    private String defaultEndString ="s";
    private int counter =30;
    private MaterialTextView phone;
    private int EDIT_NUMBER =10;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify__otp);


        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToWelcomeActivity();
            }
        });

        timer = findViewById(R.id.timer);
        phone = findViewById(R.id.phoneOfHost);
        phone.setText(getIntent().getExtras().get("phone").toString());

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                setResult(EDIT_NUMBER,data);
                finish();

            }
        });


        new CountDownTimer(30000, 1000){
            public void onTick(long millisUntilFinished){
                timer.setText(defaultStartString+String.valueOf(counter)+defaultEndString);
                counter--;
            }
            public  void onFinish(){
                Intent data = new Intent();
                setResult(RESULT_CANCELED,data);
                finish();
            }
        }.start();

        otpView = findViewById(R.id.otp_view);
        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                Intent data = new Intent();
                data.putExtra("otp",otp);
                setResult(RESULT_OK,data);
                finish();
            }
        });
    }

    private void moveToWelcomeActivity(){

        Intent intent = new Intent(Verify_OTP.this, Welcome_Activity.class);
        intent.putExtra("flag","Process Cancelled");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        AuthUI.getInstance().signOut(getApplicationContext());
        startActivity(intent);
    }
}
