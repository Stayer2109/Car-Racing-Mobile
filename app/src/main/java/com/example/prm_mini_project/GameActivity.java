package com.example.prm_mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm_mini_project.Entity.Car;
import com.example.prm_mini_project.Entity.SessionManager;
import com.example.prm_mini_project.Entity.User;
import com.example.prm_mini_project.Service.AuthService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameActivity extends AppCompatActivity {
    AuthService authService = new AuthService();
    TextView tvTitle;
    SessionManager sessionManager;
    String userJson;
    User user;
    Button btnSignOut, btnPlay;
    ImageView backgroundView1;
    ImageView backgroundView2;
    SeekBar car1SeekBar, car2SeekBar, car3SeekBar;
    TextView car1Position, car2Position, car3Position;
    Handler handler = new Handler();
    Runnable runnable, car1Runnable, car2Runnable, car3Runnable;
    int screenWidth;
    int speed = 8; // Speed of background movement (pixels)
    Random random = new Random();
    int finishedCarsCount = 0; // Track how many cars have finished
    Car car1Entity, car2Entity, car3Entity;
    CheckBox cbCar1, cbCar2, cbCar3;
    EditText etBetAmountCar1, etBetAmountCar2, etBetAmountCar3;
    AtomicBoolean isStop = new AtomicBoolean(false);

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
            userJson = sessionManager.getUserJson();
            user = authService.jsonToUser(userJson);
            if (user.getBalance() == 0) user.setBalance(100);
        }

        tvTitle = findViewById(R.id.tvTitle);
        backgroundView1 = findViewById(R.id.backgroundView1);
        backgroundView2 = findViewById(R.id.backgroundView2);
        btnPlay = findViewById(R.id.btnPlay);
        car1SeekBar = findViewById(R.id.car1);
        car2SeekBar = findViewById(R.id.car2);
        car3SeekBar = findViewById(R.id.car3);

        car1Position = findViewById(R.id.car1Position);
        car2Position = findViewById(R.id.car2Position);
        car3Position = findViewById(R.id.car3Position);

        cbCar1 = findViewById(R.id.cbBetCar1);
        cbCar2 = findViewById(R.id.cbBetCar2);
        cbCar3 = findViewById(R.id.cbBetCar3);

        etBetAmountCar1 = findViewById(R.id.etBetAmountCar1);
        etBetAmountCar2 = findViewById(R.id.etBetAmountCar2);
        etBetAmountCar3 = findViewById(R.id.etBetAmountCar3);

        // set tvTitle to userJson
        tvTitle.setText("Người chơi: " + user.getUsername() + "\nSố dư: " + user.getBalance() + " USD");

        // Initialize Car objects
        car1Entity = new Car("Car 1", R.drawable.racing_car1);
        car2Entity = new Car("Car 2", R.drawable.racing_car2);
        car3Entity = new Car("Car 3", R.drawable.racing_car3);

        // Prevent user interaction with SeekBars
        car1SeekBar.setOnTouchListener((v, event) -> true);
        car2SeekBar.setOnTouchListener((v, event) -> true);
        car3SeekBar.setOnTouchListener((v, event) -> true);

        OnCheckedChanged(cbCar1, etBetAmountCar1);
        OnCheckedChanged(cbCar2, etBetAmountCar2);
        OnCheckedChanged(cbCar3, etBetAmountCar3);

        btnPlay.setOnClickListener(v -> {
            // check if any car is selected and bet amount is entered
            if(!checkEtBetAmount(cbCar1, etBetAmountCar1, car1Entity) ||
                    !checkEtBetAmount(cbCar2, etBetAmountCar2, car2Entity) ||
                    !checkEtBetAmount(cbCar3, etBetAmountCar3, car3Entity)) {
                return;
            }

            // start the game
            if (!isStop.get()) {
                backgroundView1.post(() -> {
                    screenWidth = backgroundView1.getWidth();
                    startAnimation();
                    startCarAnimation();

                    // disable all checkboxes and edittexts
                    cbCar1.setEnabled(false);
                    cbCar2.setEnabled(false);
                    cbCar3.setEnabled(false);
                    etBetAmountCar1.setEnabled(false);
                    etBetAmountCar2.setEnabled(false);
                    etBetAmountCar3.setEnabled(false);
                    isStop.set(true);
                });

                btnPlay.setText("Dừng");
            }
            // stop the game
            else {
                stopAnimation();
                resetGame();
                isStop.set(false);
                btnPlay.setText("Bắt đầu");
            }
        });

        btnSignOut = findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(v -> {
            sessionManager.logoutUser();
            startActivity(new Intent(GameActivity.this, MainActivity.class));
            finish();
        });
    }

    // enable/disable EditText when CheckBox is checked/unchecked
    private void OnCheckedChanged(CheckBox cb, EditText etBetAmount) {
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etBetAmount.setEnabled(isChecked);
            }
        });
    }

    // if there is no car selected, show a toast
    private boolean checkCbBet(List<CheckBox> cbList) {
        boolean isAnyChecked = false;
        for (CheckBox cb : cbList) {
            if (cb.isChecked()) {
                isAnyChecked = true;
                break;
            }
        }

        if (!isAnyChecked) {
            Toast.makeText(GameActivity.this, "Vui lòng chọn xe cược", Toast.LENGTH_SHORT).show();
        }

        return isAnyChecked;
    }

    // check if the checkbox is checked and the bet amount is entered, then set the car's earnings and carSelected
    private boolean checkEtBetAmount(CheckBox cb, EditText etBetAmount, Car car) {
        if (!cb.isChecked()) {
            return checkCbBet(List.of(cbCar1, cbCar2, cbCar3));
        }

        String betAmountString = etBetAmount.getText().toString();
        if (betAmountString.isEmpty()) {
            Toast.makeText(GameActivity.this, "Vui lòng nhập số tiền cược", Toast.LENGTH_SHORT).show();
            return false;
        }

        int betAmount = Integer.parseInt(betAmountString);
        if (betAmount == 0) {
            Toast.makeText(GameActivity.this, "Vui lòng nhập số tiền cược", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (betAmount > user.getBalance()) {
            Toast.makeText(GameActivity.this, "Số dư không đủ", Toast.LENGTH_SHORT).show();
            return false;
        }

        car.setEarnings(betAmount);
        car.setCarSelected(true);
        return true;
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

    // stop all animations
    private void stopAnimation() {
        backgroundView2.setX(screenWidth);
        handler.removeCallbacks(runnable);
        handler.removeCallbacks(car1Runnable);
        handler.removeCallbacks(car2Runnable);
        handler.removeCallbacks(car3Runnable);
    }

    // reset game to initial state
    private void resetGame() {
        finishedCarsCount = 0;
        car1SeekBar.setProgress(0);
        car2SeekBar.setProgress(0);
        car3SeekBar.setProgress(0);
        car1Position.setText("Position: 0");
        car2Position.setText("Position: 0");
        car3Position.setText("Position: 0");

        cbCar1.setEnabled(true);
        cbCar2.setEnabled(true);
        cbCar3.setEnabled(true);
        etBetAmountCar1.setEnabled(true);
        etBetAmountCar2.setEnabled(true);
        etBetAmountCar3.setEnabled(true);
        car1Entity.reset();
        car2Entity.reset();
        car3Entity.reset();
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
                    ArrayList<Car> cars = new ArrayList<>();
                    cars.add(car1Entity);
                    cars.add(car2Entity);
                    cars.add(car3Entity);
                    setPosition(cars);

                    handler.removeCallbacks(this);
                }
            }
        };
    }

    private void setPosition(ArrayList<Car> cars) {
        if (finishedCarsCount == 3) {
            Intent intent = new Intent(GameActivity.this, ResultActivity.class);
            intent.putExtra("CAR_RESULTS", cars);
            startActivity(intent);
        }
    }
}
