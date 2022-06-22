package com.example.gestiondefouleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {
    ImageView ProfilePictureImgV,GobackImgV;
    EditText NameET,EmailET,PasswordET,PhoneNumberET;
    ImageView SaveButton,Buttonclose;
    String UserFullname,UserEmail,UserPhoneNumber;
    private FirebaseAuth mAuth;
    FirebaseDatabase rootNode;
    String HintName,HintUserPhoneNumber,HintEmail,HintPassword;

    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ProfilePictureImgV=findViewById(R.id.ProfilePictureImgV);
        NameET=findViewById(R.id.NameET);
        EmailET=findViewById(R.id.EmailET);
        PasswordET=findViewById(R.id.PasswordET);
        PhoneNumberET=findViewById(R.id.PhoneNumberET);
        SaveButton=findViewById(R.id.SaveButton);
        GobackImgV=findViewById(R.id.GobackImgV);
        Buttonclose=findViewById(R.id.Buttonclose);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            UserFullname = extras.getString("UserFullname");
            UserEmail = extras.getString("UserEmail");
            UserPhoneNumber = extras.getString("UserPhoneNumber");
            NameET.setHint(UserFullname);
            EmailET.setHint(UserEmail);
            PhoneNumberET.setHint(UserPhoneNumber);
            PasswordET.setHint("*********");
            //hint values
            HintName=UserFullname;
            HintEmail=UserEmail;
            HintUserPhoneNumber=UserPhoneNumber;

        }

        //firebase datbase:
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        mAuth = FirebaseAuth.getInstance();

        //add new user
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditeUserInfo();
            }
        });
        GobackImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Buttonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



    private void EditeUserInfo() {
        String Name, PhoneNumber, Email, Password;
        Name = NameET.getText().toString();

        HintName=UserFullname;
        HintEmail=UserEmail;
        HintUserPhoneNumber=UserPhoneNumber;

        Email = EmailET.getText().toString();
        Password = PasswordET.getText().toString();
        PhoneNumber = PhoneNumberET.getText().toString();

        if (Name.matches(HintName) && Email.matches(HintEmail) && PhoneNumber.matches(HintUserPhoneNumber) ) {
            finish();
        }

        //check user info
        if (Name.matches("")) {
            Toast.makeText(getApplicationContext(),
                            "Veuillez entrer un nom",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (Email.matches("")) {
            Toast.makeText(getApplicationContext(),
                            "Veuillez saisir un e-mail ",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (Password.matches("")) {
            Toast.makeText(getApplicationContext(),
                            "Veuillez entrer un mot de passe",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (PhoneNumber.matches("")) {
            Toast.makeText(getApplicationContext(),
                            "Veuillez entrer un numéro de téléphone",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }




        User newuser = new User(Name, Email,UserPhoneNumber);
        String id1 = mAuth.getCurrentUser().getUid();
        //add user to realtime database
        reference.child(id1).setValue(newuser);
        Toast.makeText(EditProfileActivity.this, "données mises à jour avec succès", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
        startActivity(intent);



    }
}