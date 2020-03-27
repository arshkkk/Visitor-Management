package com.example.visitormanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.visitormanagement.Person.Host;
import com.example.visitormanagement.Person.Visitor;
import com.example.visitormanagement.meeting.Meeting;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Meeting_Booked extends AppCompatActivity {

    private Button goHome;
    private TextView meetingId,date,time, fullNameOfVisitor, fullNameOfHost;
    private TextView phoneOfVisitor, phoneOfHost,emailOfVisitor, emailOfHost, uidOfVisitor, uidOfHost;
    private CircleImageView photoOfVisitor, photoOfHost;
    private String hostId, meetingIdString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting__booked);


        meetingIdString = getIntent().getExtras().get("meetingId").toString();

        //Visitor
        fullNameOfVisitor = findViewById(R.id.fullNameOfVisitor);
        emailOfVisitor = findViewById(R.id.emailOfVisitor);
        phoneOfVisitor = findViewById(R.id.phoneOfVisitor);
        photoOfVisitor = findViewById(R.id.photoOfVisitor);
        uidOfVisitor = findViewById(R.id.uidOfVisitor);

        //Meeting
        meetingId = findViewById(R.id.meetingId);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);

        //Host
        fullNameOfHost = findViewById(R.id.fullNameOfHost);
        phoneOfHost = findViewById(R.id.phoneOfHost);
        emailOfHost = findViewById(R.id.emailOfHost);
        photoOfHost = findViewById(R.id.photoOfHost);
        uidOfHost = findViewById(R.id.uidOfHost);



        getDataFromFirebase();

        goHome = findViewById(R.id.goHome);
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToWelcomeActivity();
            }
        });
    }

    private void getDataFromFirebase(){
        getMeetingData();
        getVisitorData();
    }

    private void getMeetingData(){

        FirebaseDatabase.getInstance().getReference().child("meetings")
                .child(meetingIdString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    inflateToMeeting(dataSnapshot.getValue(Meeting.class));

                    else Snackbar.make(goHome,"Meeting Data Not Present", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inflateToMeeting(Meeting meeting){
        meetingId.setText(meeting.getMeetingId());
        date.setText(meeting.getDate());
        time.setText(meeting.getTime());

        getHostData(meeting.getHostId());

    }


    private void getHostData(String hostId){

        FirebaseDatabase.getInstance().getReference().child("hosts")
                .child(hostId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    inflateToHost(dataSnapshot.getValue(Host.class));

                    else Snackbar.make(goHome,"Meeting Data Not Present", Snackbar.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void inflateToHost(Host host){
        Picasso.get().load(host.getPhoto()).into(photoOfHost);
        fullNameOfHost.setText(host.getFullName());
        emailOfHost.setText(host.getEmail());
        phoneOfHost.setText(host.getPhoneNumber());
        uidOfHost.setText(host.getUid());
    }


    private void getVisitorData(){
        FirebaseDatabase.getInstance().getReference().child("visitors")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                            inflateToVisitor(dataSnapshot.getValue(Visitor.class));

                            else Snackbar.make(goHome,"Meeting Data Not Present", Snackbar.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void inflateToVisitor(Visitor visitor){
        Picasso.get().load(visitor.getPhoto()).into(photoOfVisitor);
        fullNameOfVisitor.setText(visitor.getFullName());
        emailOfVisitor.setText(visitor.getEmail());
        phoneOfVisitor.setText(visitor.getPhoneNumber());
        uidOfVisitor.setText(visitor.getUid());

    }

    private void moveToWelcomeActivity(){

        Intent intent = new Intent(Meeting_Booked.this, Welcome_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        AuthUI.getInstance().signOut(getApplicationContext());
        startActivity(intent);
    }
}
