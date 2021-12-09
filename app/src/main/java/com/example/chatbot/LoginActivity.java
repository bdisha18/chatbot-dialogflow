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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText e_email, e_password;
    Button signinBtn;
    TextView signup_link;
    String s_email, s_password;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        e_email = findViewById(R.id.e_email);
        e_password = findViewById(R.id.e_password);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressbar = findViewById(R.id.progressBar);

        signup_link = findViewById(R.id.signup_link);
        signup_link.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        signinBtn = findViewById(R.id.signin_btn);
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }

            private void loginUser() {
                s_email = e_email.getText().toString().trim();
                s_password = e_password.getText().toString().trim();

                // validations for input email and password
                if (TextUtils.isEmpty(s_email)) {
                    Toast.makeText(getApplicationContext(), "Please enter email!!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(s_password)) {
                    Toast.makeText(getApplicationContext(), "Please enter password!!", Toast.LENGTH_LONG).show();
                    return;
                }

                // signin existing user
                firebaseAuth.signInWithEmailAndPassword(s_email, s_password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Login successful!!", Toast.LENGTH_LONG).show();

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String id = user.getUid();

                                databaseReference = firebaseDatabase.getReference("users").child(id);
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String username = snapshot.child("username").getValue(String.class);
                                        Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
                                        i.putExtra("name", username);
                                        startActivity(i);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                // hide the progress bar
                                progressbar.setVisibility(View.GONE);

                                // if sign-in is successful

                            } else {

                                // sign-in failed
                                Toast.makeText(getApplicationContext(), "Login failed!!", Toast.LENGTH_LONG).show();

                                // hide the progress bar
                                progressbar.setVisibility(View.GONE);
                            }
                        });
            }
        });


    }
}