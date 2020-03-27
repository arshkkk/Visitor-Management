package com.example.visitormanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.visitormanagement.Person.Visitor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class GetVisitorDetails extends AppCompatActivity {

    private TextInputEditText phone, firstName, lastName, email;
    private Button nextButton,cancelButton;
    private ProgressBar progressBar,progressBarOfPhoto;
    private CircleImageView photo;
    private Visitor visitorDownloaded;
    private TextInputLayout firstNameTextInputLayout, lastNameTextInputLayout, emailTextInputLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_visitor_details);

        //ImageView
        photo = findViewById(R.id.photoOfVisitor);

        //EditText
        phone = findViewById(R.id.phoneOfHost);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.emailOfHost);

        //Buttons
        nextButton = findViewById(R.id.nextButton);
        cancelButton = findViewById(R.id.cancelButton);

        //ProgressBar
        progressBar = findViewById(R.id.progressBar1);
        progressBarOfPhoto= findViewById(R.id.progressBarOfPhoto);


        //TextInput Layouts
        firstNameTextInputLayout = findViewById(R.id.firstNameTextInputLayout);
        lastNameTextInputLayout = findViewById(R.id.lastNameTextInputLayout);
        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);

        //addClickListener
        addClickListener();



        progressBar.setVisibility(View.VISIBLE);

        //Response from previous Intent
        if(getIntent().hasExtra("flag"))
            Snackbar.make(nextButton,getIntent().getExtras().get("flag").toString(),Snackbar.LENGTH_LONG).show();


        visitorDownloaded=getDataFromFirebase();



    }

    //Uploading Data to Database
    private void uploadDataToDatabase(Visitor visitor){

        Map<String, Object> dataUpdate = new HashMap<>();
        dataUpdate.put("/"+FirebaseAuth.getInstance().getCurrentUser().getUid() , visitor);

        FirebaseDatabase.getInstance().getReference()
                .child("visitors")
                .updateChildren(dataUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);

                moveToShowDataActivity();
            }
        });


    }

    public boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else{

            emailTextInputLayout.setError("Email Not Valid");
            return false;

        }
    }



    private Visitor getDataFromFirebase(){

        FirebaseDatabase.getInstance().getReference()
                .child("visitors")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        visitorDownloaded = dataSnapshot.getValue(Visitor.class);

                        inflateDataToFields(visitorDownloaded);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Snackbar.make(nextButton,databaseError.toString(),Snackbar.LENGTH_LONG).show();
                    }
                });
        return visitorDownloaded;
    }

    private void inflateDataToFields(Visitor visitor){

        Picasso.get().load(visitor.getPhoto()).into(photo, new Callback() {
            @Override
            public void onSuccess() {
                progressBarOfPhoto.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        phone.setText(visitor.getPhoneNumber());
        firstName.setText(visitor.getFirstName());
        lastName.setText(visitor.getLastName());
        email.setText(visitor.getEmail());

        progressBar.setVisibility(View.GONE);


    }

    private void addClickListener(){

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Getting All Data from Text Fields
                String emailString = email.getText().toString();
                String phoneString = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                String uidString = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String firstNameString = firstName.getText().toString();
                String lastNameString = lastName.getText().toString();

                //Verifying if data is valid and then uploading to database
                if(firstNameString.isEmpty()) firstNameTextInputLayout.setError("cannot be empty");
                if(lastNameString.isEmpty()) lastNameTextInputLayout.setError("cannot be empty");

                if( isEmailValid(emailString) && !firstNameString.isEmpty() && !lastNameString.isEmpty()){

                    Visitor visitorToUpload = new Visitor(firstNameString,lastNameString,visitorDownloaded.getVisitCount(),visitorDownloaded.getPhoto(),phoneString,uidString,emailString, firstNameString+" "+lastNameString);

                    uploadDataToDatabase(visitorToUpload);
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                moveToWelcomeActivity();
            }
        });
    }

    private void moveToShowDataActivity(){

            Intent intent = new Intent(GetVisitorDetails.this, ShowDetails.class);
            intent.putExtra("flag1","Data Saved Successfully");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


    }

    private void moveToWelcomeActivity(){

        Intent intent = new Intent(GetVisitorDetails.this, Welcome_Activity.class);
        intent.putExtra("flag","Process Cancelled");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


    }
}
