package com.example.prm_mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    // Variables
    Button howToPlayButton;
    Button signUnButton;
    Button backgroundViewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Mappings
        howToPlayButton = findViewById(R.id.howToPlayButton);
        signUnButton = findViewById(R.id.signUpButton);
        backgroundViewButton = findViewById(R.id.backgroundViewButton);

        //region Set onClickListener For How Buttons
        howToPlayButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HowToPlayActivity.class));
        });

        signUnButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        });
        //endregion
        backgroundViewButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BackgroundView.class));
        });

    }
}