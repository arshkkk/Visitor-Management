package com.example.visitormanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.visitormanagement.Person.Visitor;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowDetails extends AppCompatActivity {

    private Button editButton, nextButton, cancelButton;
    private CircleImageView photo;
    private TextInputEditText phone,firstName,lastName,email;
    private Visitor visitor;
    private ProgressBar progressBar, progressBarOfPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);




        //Progress Bar
        progressBar = findViewById(R.id.progressBar);
        progressBarOfPhoto = findViewById(R.id.progressBarOfPhoto);

        //Buttons
        editButton = findViewById(R.id.editButton);
        nextButton = findViewById(R.id.nextButton);
        cancelButton = findViewById(R.id.cancelButton);

        //ImageView
        photo = findViewById(R.id.photoOfVisitor);

        //EditTextFields
        phone = findViewById(R.id.phoneOfHost);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.emailOfHost);

        //Msg from Previous Activities
        if(getIntent().hasExtra("flag"))
            Snackbar.make(nextButton,getIntent().getExtras().get("flag").toString(),Snackbar.LENGTH_LONG).show();
        else if(getIntent().hasExtra("flag1"))
            Snackbar.make(nextButton,getIntent().getExtras().get("flag1").toString(),Snackbar.LENGTH_LONG).show();

        //Adding Clicklisteners
        addClickListeners();

        //Getting from Firebase to Show
        getDataFromFirebase();


    }

    private void getDataFromFirebase(){

        FirebaseDatabase.getInstance().getReference()
                .child("visitors")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        visitor = dataSnapshot.getValue(Visitor.class);

                        inflateDataToFields();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Snackbar.make(editButton,databaseError.toString(),Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void inflateDataToFields(){

        Picasso.get().load(visitor.getPhoto()).placeholder(R.drawable.red_carpet).into(photo, new Callback() {
            @Override
            public void onSuccess() {
                progressBarOfPhoto.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(nextButton,"Error Loading Photo", Snackbar.LENGTH_LONG).show();
            }
        });

        phone.setText(visitor.getPhoneNumber());
        firstName.setText(visitor.getFirstName());
        lastName.setText(visitor.getLastName());
        email.setText(visitor.getEmail());

        Snackbar.make(nextButton,"This is Your "+visitor.getVisitCount()+"th Visit",Snackbar.LENGTH_LONG).show();

        progressBar.setVisibility(View.GONE);


    }



    private void addClickListeners(){
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToWelcomeActivity();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movetToSettingMeetingActivity();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToGetDataActivity();
            }
        });
    }


    //Intents for Moving Activities

    private void moveToGetDataActivity(){
        Intent intent = new Intent(ShowDetails.this, GetVisitorDetails.class);

        intent.putExtra("visitorClass", visitor);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void moveToWelcomeActivity(){
        Intent intent = new Intent(ShowDetails.this, Welcome_Activity.class);
        intent.putExtra("flag","Process Cancelled");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void movetToSettingMeetingActivity(){
        Intent intent = new Intent(ShowDetails.this, Setting_Up_Meeting.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }




}
