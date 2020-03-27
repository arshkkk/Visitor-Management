package com.example.visitormanagement;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;

public class PhoneAuth extends AppCompatActivity {

    private boolean flag=false;
    private static final int RC_SIGN_IN = 123;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.PhoneBuilder().build());
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        createSignInIntent();


    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers


        // Create and launch sign-in intent
        new CountDownTimer(45000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if(flag==false){
//                    sendUserToFirebase("susp");
                    moveToWelcomeActivity();
                }
                else finish();

            }
        }.start();


        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);


        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                flag = true;
                progressDialog = initiateProgressDialog();

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                FirebaseDatabase.getInstance().getReference().child("visitors")
                                            .child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()&&dataSnapshot.hasChild("firstName")){
                          moveToShowDataActivity();
                        }

                        else{
                          moveToGetDataActivity();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(PhoneAuth.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });



                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...


                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                sendUserToFirebase("susp", response.getPhoneNumber());

            }
        }
    }

    private void sendUserToFirebase(String category,String phoneNumber){
        //Sending Image to Suspicious User in Firebase
        File imageFile = new File(getFilesDir(),"photo.jpg");

        File compressedImgFile= imageFile;
        try{
            compressedImgFile = new Compressor(this).compressToFile(imageFile);


        }
        catch(IOException io){
            Toast.makeText(this,io.getMessage().toString(),Toast.LENGTH_LONG).show();
        }

        Uri file = Uri.fromFile(compressedImgFile);

        String key;
        if(category.equals("susp"))
        key = FirebaseDatabase.getInstance().getReference()
                .child("susp")
                .push().getKey();

        else key=FirebaseAuth.getInstance().getCurrentUser().getUid();


        StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child("category").child(key);

        ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String url = ref.getDownloadUrl().toString();

                HashMap<String, Object> hashMap= new HashMap<>();
                hashMap.put("uid",key);
                hashMap.put("phone",phoneNumber);
                hashMap.put("photo",url);

                FirebaseDatabase.getInstance().getReference()
                        .child("susp_users").child(key)
                        .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        moveToWelcomeActivity();
                    }
                });
            }
        });

    }
    private ProgressDialog initiateProgressDialog(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("We are contacting Database for You");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        return progressDialog;
    }

    private void moveToShowDataActivity(){
        Intent intent = new Intent(PhoneAuth.this, ShowDetails.class);
        progressDialog.dismiss();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void moveToGetDataActivity(){
        Intent intent = new Intent(PhoneAuth.this, GetVisitorDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        progressDialog.dismiss();
        startActivity(intent);
    }

    private void moveToWelcomeActivity(){

        Intent intent = new Intent(PhoneAuth.this, Welcome_Activity.class);
        intent.putExtra("flag","Auth Failed ( Suspicious User )");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        AuthUI.getInstance().signOut(getApplicationContext());
        startActivity(intent);
    }


}