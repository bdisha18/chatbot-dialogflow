package com.example.chatbot;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatbot.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText addUsername, addEmail, addPassword, addContact;
    Button signupBtn;
    TextView signinLink;
    String username, email, password, contact;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        addUsername = findViewById(R.id.add_username);
        addEmail = findViewById(R.id.add_email);
        addContact = findViewById(R.id.add_contact);
        addPassword = findViewById(R.id.add_password);
        progressbar = findViewById(R.id.progressbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        signinLink = findViewById(R.id.signin_link);
        signinLink.setOnClickListener(view -> {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
        });

        signupBtn = findViewById(R.id.signup_btn);
        signupBtn.setOnClickListener(view -> register());
    }

    public void register() {
        progressbar.setVisibility(View.VISIBLE);

        username = addUsername.getText().toString().trim();
        email = addEmail.getText().toString().trim();
        contact = addContact.getText().toString().trim();
        password = addPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Enter Username!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(contact)) {
            Toast.makeText(getApplicationContext(), "Enter Contact No.!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter Email Address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter Password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        String id = user.getUid();
                        databaseReference = firebaseDatabase.getReference("users").child(id);

                        User userModel = new User(username, email, contact, password);
                        databaseReference.setValue(userModel);

                        Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        progressbar.setVisibility(View.GONE);

                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(i);
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        progressbar.setVisibility(View.GONE);
                    }
                });
    }


}
