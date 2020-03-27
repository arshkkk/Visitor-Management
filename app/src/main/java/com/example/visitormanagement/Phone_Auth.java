package com.example.visitormanagement;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;

public class Phone_Auth extends AppCompatActivity {

    private CountryCodePicker ccp;
    private TextInputEditText phone;
    private TextInputLayout phoneInputLayout;
    private ProgressBar progressBar;
    private Button verifyButton;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private int requestCode = 123;
    private int tries = 0;
    private int EDIT_NUMBER =10;
    private boolean suspiciousUser = false, timeOut=false, invalidOtp = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone__auth2);

        phoneInputLayout = findViewById(R.id.phoneInputLayout);


        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        phone =  findViewById(R.id.phoneOfHost);
        ccp.registerCarrierNumberEditText(phone);
        ccp.setNumberAutoFormattingEnabled(true);

        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                if(isValidNumber)
                phoneInputLayout.setError(null);
            }
        });


        //Progress Bar
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //Initiate Callback
        initiateCallback();

        //Button
        verifyButton = findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ccp.isValidFullNumber()){
                    verifyButton.setEnabled(false);
                    tries += 1;
                    progressBar.setVisibility(View.VISIBLE);
                    initiateAuthentication();

                }

                else phoneInputLayout.setError("Number not Valid");

            }
        });




    }

    private void initiateAuthentication(){



        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                ccp.getFullNumberWithPlus(),        // Phone number to verify
                40,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


    }

    private void moveToShowDataActivity(){
        Intent intent = new Intent(Phone_Auth.this, ShowDetails.class);
        progressBar.setVisibility(View.GONE);
        intent.putExtra("flag", "Authentication Successful");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void moveToGetDataActivity(){
        Intent intent = new Intent(Phone_Auth.this, GetVisitorDetails.class);
        intent.putExtra("flag", "Authentication Successful");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        progressBar.setVisibility(View.GONE);
        startActivity(intent);
    }

    private void moveToWelcomeActivity(String error){

        Intent intent = new Intent(Phone_Auth.this, Welcome_Activity.class);
        if(error.equals(" "))
            intent.putExtra("flag","Auth Failed ( Suspicious User )");
        else
            intent.putExtra("flag",error);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        AuthUI.getInstance().signOut(getApplicationContext());
        startActivity(intent);
    }


    private void initiateCallback(){

         mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
//                Log.d(TAG, "onVerificationCompleted:" + credential);
                Snackbar.make(verifyButton,"Authenticatin Successfull",Snackbar.LENGTH_LONG).show();
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
//                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Snackbar.make(verifyButton,e.getMessage(),Snackbar.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...

                    Snackbar.make(verifyButton,e.getMessage(),Snackbar.LENGTH_LONG).show();

                }

                // Show a message and update the UI
                // ...
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
//                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;


                Intent intent = new Intent(Phone_Auth.this,Verify_OTP.class);
                intent.putExtra("phone",ccp.getFormattedFullNumber());

                //Re Enable Verify Button
                verifyButton.setEnabled(true);

                startActivityForResult(intent,requestCode);


                // ...
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");


                            uploadPictureWithPhoneNumberToFirebase("visitors");
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                progressBar.setVisibility(View.GONE);

                                if(tries==3){
                                    suspiciousUser=true; invalidOtp=true;
                                    uploadPictureWithPhoneNumberToFirebase("suspicious");
                                }
                                else
                                    Snackbar.make(verifyButton,"Invalid OTP", Snackbar.LENGTH_LONG).show();


                            }
                        }
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (this.requestCode == requestCode) {
            if (resultCode == RESULT_OK) {
                String otp = data.getStringExtra("otp");
                if(!otp.equals(" ")){
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                    signInWithPhoneAuthCredential(credential);
                }

            }
            else if(resultCode==EDIT_NUMBER){
                progressBar.setVisibility(View.GONE);
            }
            else{
                    //Otp activity finished due to timeout
                suspiciousUser=true; timeOut=true;
                uploadPictureWithPhoneNumberToFirebase("suspicious");
            }
        }
    }

    private void decideWhereUserGoes(){

        if(suspiciousUser==true&&invalidOtp==true) moveToWelcomeActivity("Suscipious User ( 3 Times Invalid OTP )");
        else if(suspiciousUser==true && timeOut==true)   moveToWelcomeActivity("Timout ( Suspicious User )");

        else{
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("visitors")
                    .child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()&&dataSnapshot.hasChild("firstName")){
                        progressBar.setVisibility(View.GONE);
                        moveToShowDataActivity();
                    }

                    else{
                        progressBar.setVisibility(View.GONE);
                        moveToGetDataActivity();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Snackbar.make(verifyButton,databaseError.toString(),Snackbar.LENGTH_LONG).show();
                }
            });
        }

    }

    //category --> 1. suspicious 2. visitors
    private void uploadPictureWithPhoneNumberToFirebase(String category){

        File imageFile = new File(getExternalMediaDirs()[0],"picture.jpg");

        Uri file = Uri.fromFile(imageFile);

        try{
            Bitmap compressedBitmap = new Compressor(this)
                    .setMaxWidth(480)
                    .setMaxHeight(480)
                    .setQuality(70)
                    .compressToBitmap(imageFile);
            file = getImageUri(this,compressedBitmap);
        }
        catch (IOException e){
            Snackbar.make(verifyButton,"Image Compress Err"+e.getMessage(),Snackbar.LENGTH_LONG).show();
        }

        String key = " ";

        if(!category.equals("visitors"))
            key = FirebaseDatabase.getInstance().getReference().child(category)
                    .push().getKey();

        else key = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final String key1 = key;

        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("/"+category+"/"+
                key+"/"+file.getLastPathSegment());

        UploadTask uploadTask = imageRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

                Snackbar.make(verifyButton,exception.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...

                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {


                        Map<String,Object > hashMap = new HashMap<>();
                        hashMap.put("phoneNumber",ccp.getFullNumberWithPlus());
                        hashMap.put("uid",key1);
                        hashMap.put("photo", uri.toString());
                        if(suspiciousUser){
                            if(invalidOtp) hashMap.put("Error", "Invalid Otp");
                            else if(timeOut) hashMap.put("Error", "Timeout");
                        }
                        FirebaseDatabase.getInstance().getReference().child(category)
                                .child(key1)
                                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                decideWhereUserGoes();
                            }
                        });



                        Snackbar.make(verifyButton,"Picture and Data Uploaded Successfull",Snackbar.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(verifyButton,e.getMessage(),Snackbar.LENGTH_LONG).show();
                    }
                });




            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
