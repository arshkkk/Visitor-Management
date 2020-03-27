package com.example.visitormanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.visitormanagement.Person.Host;
import com.example.visitormanagement.Person.Visitor;
import com.example.visitormanagement.meeting.Meeting;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Booking_Meeting extends AppCompatActivity {

    private Button confirmMeeting, goBack;
    private TextView phone,email,fullName;
    private CircleImageView photo;
    private String hostId;
    private ProgressBar progressBarOfPhoto;
    private Host hostDownloaded;
    private Visitor visitorDownloaded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking__meeting);



        hostId = getIntent().getExtras().get("hostId").toString();

        //Progress Bar
        progressBarOfPhoto = findViewById(R.id.progressBarOfPhoto);

        //Buttons
        confirmMeeting = findViewById(R.id.confirmMeeting);
        goBack = findViewById(R.id.goHome);

        //TextView
        phone = findViewById(R.id.phoneOfHost);
        fullName = findViewById(R.id.fullNameOfHost);
        email = findViewById(R.id.emailOfHost);

        //ImageView
        photo = findViewById(R.id.photoOfVisitor);

        //addClickListeners
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Getting Data from firebase and inflating to fields
        getDataFromFirebase();

        confirmMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadMeetingDataToFirebase();

            }
        });

    }

    private void getDataFromFirebase(){
        //Host Data
        FirebaseDatabase.getInstance().getReference()
                .child("hosts")
                .child(hostId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        hostDownloaded = dataSnapshot.getValue(Host.class);
                        inflateDataToFields(dataSnapshot.getValue(Host.class));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Snackbar.make(confirmMeeting,databaseError.toString(),Snackbar.LENGTH_LONG).show();
                    }
                });

    }

    private void getVisitorData(){

        FirebaseDatabase.getInstance().getReference()
                .child("visitors")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        visitorDownloaded = dataSnapshot.getValue(Visitor.class);

                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Snackbar.make(confirmMeeting,databaseError.toString(),Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void inflateDataToFields(Host host){

        Picasso.get().load(host.getPhoto()).placeholder(R.drawable.red_carpet).into(photo, new Callback() {
            @Override
            public void onSuccess() {
                progressBarOfPhoto.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(confirmMeeting,"Error Loading Photo", Snackbar.LENGTH_LONG).show();
            }
        });

        phone.setText(host.getPhoneNumber());
        fullName.setText(host.getFullName());
        email.setText(host.getEmail());

        getVisitorData();



    }

    private void uploadMeetingDataToFirebase() {

        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm a");

        String timeString = time.format(new Date());
        String dateString = date.format(new Date());



        String key = FirebaseDatabase.getInstance().getReference().child("meetings").push().getKey();

        Meeting meeting = new Meeting(hostId, FirebaseAuth.getInstance().getCurrentUser().getUid(),key,timeString,dateString);



                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/visitors/" + meeting.getVisitorId()+"/visitCount", Integer.toString(Integer.parseInt(visitorDownloaded.getVisitCount())+1));
                        childUpdates.put("/hosts/" + meeting.getHostId() + "/visitorsCount" , Integer.toString(Integer.parseInt(hostDownloaded.getVisitorsCount())+1));
                        childUpdates.put("/meetings/"+key,meeting);

                        FirebaseDatabase.getInstance().getReference()
                                .updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(Booking_Meeting.this, Meeting_Booked.class);
                                intent.putExtra("meetingId",key);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(confirmMeeting,e.getMessage(),Snackbar.LENGTH_LONG).show();

                            }
                        });
                    }



    }


