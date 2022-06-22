package com.example.gestiondefouleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    EditText NameET, EmailET,PhoneNumberET, PasswordET, ConfirmPasswordET;
    Button SignUpButton;
    private FirebaseAuth mAuth;
    ProgressBar ProgressBar;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //initialize views
        NameET = findViewById(R.id.NameET);
        EmailET = findViewById(R.id.EmailET);
        PhoneNumberET = findViewById(R.id.PhoneNumberET);
        PasswordET = findViewById(R.id.PasswordET);
        ConfirmPasswordET = findViewById(R.id.ConfirmPasswordET);
        SignUpButton = findViewById(R.id.SignUpButton);
        ProgressBar=findViewById(R.id.ProgressBar);

        //firebase datbase:
            //RTDB
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("users");
           //Authentification:
                mAuth = FirebaseAuth.getInstance();

        //SignUppButton clicked
        //add new user
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewUser();
            }
        });

    }


    private void registerNewUser() {
        ProgressBar.setVisibility(View.VISIBLE);

        String Name,Phone, ConfirmPass, Email, Password;
        Name = NameET.getText().toString();
        Email = EmailET.getText().toString();
        Phone= PhoneNumberET.getText().toString();
        Password = PasswordET.getText().toString();
        ConfirmPass = ConfirmPasswordET.getText().toString();

        //check user info
        if (Name.matches("")) {
            Toast.makeText(getApplicationContext(),
                            "Please enter you name",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (Email.matches("")) {
            Toast.makeText(getApplicationContext(),
                            "Please enter Email ",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (Phone.matches("")) {
            Toast.makeText(getApplicationContext(),
                            "Please enter A phoneNumber ",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (Password.matches("")) {
            Toast.makeText(getApplicationContext(),
                            "Please enter a password",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (!(ConfirmPass.matches(Password))) {
            Toast.makeText(getApplicationContext(),
                            "confirme ur password please",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }



        // create new user
        User newuser = new User(Name, Email,Phone);


        // register new user in firebase
        mAuth
                .createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ProgressBar.setVisibility(View.GONE);

                            String id = mAuth.getCurrentUser().getUid();
                            //add user to realtime database
                            reference.child(id).setValue(newuser);
                            Toast.makeText(getApplicationContext(),
                                            "Registration successful!",
                                            Toast.LENGTH_LONG)
                                    .show();
                            // if the user created intent to login activity
                            Intent intent
                                    = new Intent(SignupActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                        } else {
                            ProgressBar.setVisibility(View.GONE);

                            // Registration failed
                            Toast.makeText(
                                            getApplicationContext(),
                                            "Registration failed!!"
                                                    + " Please try again later",
                                            Toast.LENGTH_LONG)
                                    .show();


                        }
                    }
                });


    }
}