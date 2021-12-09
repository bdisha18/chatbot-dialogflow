package com.example.chatbot;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {
    ImageView dashboardBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dashboardBtn = findViewById(R.id.dashboard_btn);
        dashboardBtn.setOnClickListener(view -> {
            Intent i = new Intent(DashboardActivity.this, ChatActivity.class);
            String username = getIntent().getExtras().getString("name");
            i.putExtra("name", username);
            startActivity(i);
        });
    }
}