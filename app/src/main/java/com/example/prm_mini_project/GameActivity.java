package com.example.prm_mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm_mini_project.Entity.Car;
import com.example.prm_mini_project.Entity.SessionManager;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    SessionManager sessionManager;
    String username;
    Button btnSignOut;
    ImageView backgroundView1;
    ImageView backgroundView2;
    SeekBar car1SeekBar;
    SeekBar car2SeekBar;
    SeekBar car3SeekBar;

    TextView car1Position;
    TextView car2Position;
    TextView car3Position;

    Handler handler = new Handler();
    Runnable runnable;
    Runnable car1Runnable;
    Runnable car2Runnable;
    Runnable car3Runnable;
    int screenWidth;
    int speed = 8; // Speed of background movement (pixels)
    Random random = new Random();
    int finishedCarsCount = 0; // Track how many cars have finished

    Car car1Entity;
    Car car2Entity;
    Car car3Entity;

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
            username = sessionManager.getUsername();
        }

        backgroundView1 = findViewById(R.id.backgroundView1);
        backgroundView2 = findViewById(R.id.backgroundView2);
        car1SeekBar = findViewById(R.id.car1);
        car2SeekBar = findViewById(R.id.car2);
        car3SeekBar = findViewById(R.id.car3);

        car1Position = findViewById(R.id.car1Position);
        car2Position = findViewById(R.id.car2Position);
        car3Position = findViewById(R.id.car3Position);

        // Initialize Car objects
        car1Entity = new Car("Car 1", R.drawable.racing_car1);
        car2Entity = new Car("Car 2", R.drawable.racing_car2);
        car3Entity = new Car("Car 3", R.drawable.racing_car3);

        // Prevent user interaction with SeekBars
        car1SeekBar.setOnTouchListener((v, event) -> true);
        car2SeekBar.setOnTouchListener((v, event) -> true);
        car3SeekBar.setOnTouchListener((v, event) -> true);

        backgroundView1.post(() -> {
            screenWidth = backgroundView1.getWidth();
            startAnimation();
            startCarAnimation();
        });

        btnSignOut = findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(v -> {
            sessionManager.logoutUser();
            startActivity(new Intent(GameActivity.this, MainActivity.class));
            finish();
        });
    }

    private void startAnimation() {
        backgroundView2.setX(screenWidth);
        runnable = new Runnable() {
            @Override
            public void run() {
                backgroundView1.setX(backgroundView1.getX() - speed);
                backgroundView2.setX(backgroundView2.getX() - speed);

                if (backgroundView1.getX() + backgroundView1.getWidth() <= 0) {
                    backgroundView1.setX(backgroundView2.getX() + backgroundView2.getWidth());
                }

                if (backgroundView2.getX() + backgroundView2.getWidth() <= 0) {
                    backgroundView2.setX(backgroundView1.getX() + backgroundView1.getWidth());
                }

                handler.postDelayed(this, 16);
            }
        };
        handler.post(runnable);
    }

    private void startCarAnimation() {
        car1Runnable = createCarRunnable(car1SeekBar, car1Entity, car1Position);
        car2Runnable = createCarRunnable(car2SeekBar, car2Entity, car2Position);
        car3Runnable = createCarRunnable(car3SeekBar, car3Entity, car3Position);

        handler.post(car1Runnable);
        handler.post(car2Runnable);
        handler.post(car3Runnable);
    }

    private Runnable createCarRunnable(SeekBar carSeekBar, Car carEntity, TextView carPositionTextView) {
        return new Runnable() {
            @Override
            public void run() {
                if (carSeekBar.getProgress() < 100) {
                    int randomDistance = random.nextInt(10) + 1;
                    carSeekBar.setProgress(carSeekBar.getProgress() + randomDistance);
                    handler.postDelayed(this, random.nextInt(500) + 300);
                } else {
                    finishedCarsCount++;
                    String positionText = "";
                    if (finishedCarsCount == 1) {
                        positionText = "1st";
                        carEntity.setPosition(1);
                    } else if (finishedCarsCount == 2) {
                        positionText = "2nd";
                        carEntity.setPosition(2);
                    } else if (finishedCarsCount == 3) {
                        positionText = "3rd";
                        carEntity.setPosition(3);
                    }

                    carPositionTextView.setText(positionText);
                    setPosition(carEntity);

                    handler.removeCallbacks(this);
                }
            }
        };
    }

    private void setPosition(Car carEntity) {
        if (finishedCarsCount == 3) {
            Intent intent = new Intent(GameActivity.this, ResultActivity.class);
            intent.putExtra("car1", car1Entity);
            intent.putExtra("car2", car2Entity);
            intent.putExtra("car3", car3Entity);
//            startActivity(intent);
//            finish();
        }
    }
}
