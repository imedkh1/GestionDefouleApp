package com.example.gestiondefouleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText EmailET,PasswordET;
    Button LoginButton;
    TextView SignUpTV;
    ProgressBar ProgressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //initialize views
        SignUpTV=findViewById(R.id.SignUpTV);
        EmailET=findViewById(R.id.EmailET);
        PasswordET=findViewById(R.id.PasswordET);
        LoginButton=findViewById(R.id.LoginButton);
        ProgressBar=findViewById(R.id.ProgressBar);

        // instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });



        SignUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });
    }


    private void login() {
        ProgressBar.setVisibility(View.VISIBLE);
        String email,password;
        email=EmailET.getText().toString();
        password=PasswordET.getText().toString();

        // validations for input email and password
        if (email.matches("")) {
            Toast.makeText(getApplicationContext(),
                            "Veuillez saisir un e-mail !!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (password.matches("")) {
            Toast.makeText(getApplicationContext(),
                            "Veuillez entrer un mot de passe!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // signin existing user

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful()) {
                                    ProgressBar.setVisibility(View.GONE);

                                    Toast.makeText(getApplicationContext(),
                                                    "Connexion réussie",
                                                    Toast.LENGTH_LONG)
                                            .show();


                                    // if sign-in is successful
                                    // intent to home activity
                                    Intent intent
                                            = new Intent(LoginActivity.this,
                                            MainActivity.class);
                                    startActivity(intent);
                                }

                                else {

                                    // sign-in failed
                                    ProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),
                                                    "Échec de la connexion",
                                                    Toast.LENGTH_LONG)
                                            .show();


                                }
                            }
                        });

    }
}