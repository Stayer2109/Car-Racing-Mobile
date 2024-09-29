package com.example.prm_mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm_mini_project.Entity.SessionManager;

public class GameActivity extends AppCompatActivity {
    SessionManager sessionManager;
    TextView tvTitle;
    String username;
    Button btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            // If not logged in, redirect to login page
            Intent intent = new Intent(GameActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Get logged in username and continue
            username = sessionManager.getUsername();
            // Show welcome message or something with username
        }

        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Chào mừng, " + username);
        btnSignOut = findViewById(R.id.btnSignOut);

        btnSignOut.setOnClickListener(v -> {
            sessionManager.logoutUser();
            startActivity(new Intent(GameActivity.this, MainActivity.class));
            finish();
        });
    }
}