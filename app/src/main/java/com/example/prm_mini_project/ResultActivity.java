package com.example.prm_mini_project;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_mini_project.Entity.Car;
import com.example.prm_mini_project.Entity.SessionManager;
import com.example.prm_mini_project.Entity.User;
import com.example.prm_mini_project.Service.AuthService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {
    TableLayout resultsTable;
    TextView totalEarningsTextView;
    TextView winMessageTextView;
    SessionManager sessionManager;
    AuthService authService = new AuthService();
    User user;
    String userJson;
    float totalEarnings;
    Button btnPlayAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        sessionManager = new SessionManager(this);
        user = authService.jsonToUser(sessionManager.getUserJson());
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        resultsTable = findViewById(R.id.resultsTable);
        totalEarningsTextView = findViewById(R.id.totalEarningsTextView);
        winMessageTextView = findViewById(R.id.winMessageTextView);
        ArrayList<Car> raceResults = getRaceResults();
        totalEarnings = calculateTotalEarnings(raceResults);
        displayResults(raceResults);
        displayTotalEarnings(totalEarnings);
        if (didPlayerWin(raceResults)) {
            winMessageTextView.setText("You Win!");
        } else {
            winMessageTextView.setText("You Lose!");
        }

        btnPlayAgain.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private ArrayList<Car> getRaceResults() {
        ArrayList<Car> results = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("CAR_RESULTS")) {
            results = (ArrayList<Car>) intent.getSerializableExtra("CAR_RESULTS");
        }
        return results;
    }

    private float calculateTotalEarnings(ArrayList<Car> raceResults) {
        float totalEarnings = 0;
        for (Car result : raceResults) {
            switch (result.getPosition()) {
                case 1:
                    result.setEarnings(result.getEarnings());
                    break;
                case 2:
                case 3:
                    result.setEarnings(- result.getEarnings());
                    break;
                default:
                    result.setEarnings(0);
                    break;
            }
            totalEarnings += result.getEarnings();
        }
        return totalEarnings;
    }

    private void displayResults(ArrayList<Car> raceResults) {
        boolean isEvenRow = true;
        for (Car result : raceResults) {
            TableRow row = new TableRow(this);
            if (isEvenRow) {
                row.setBackgroundColor(Color.parseColor("#EEEEEE"));
            } else {
                row.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }

            TextView positionTextView = new TextView(this);
            positionTextView.setText(getPositionString(result.getPosition()));
            positionTextView.setPadding(8, 8, 8, 8);
            positionTextView.setTextColor(Color.BLACK);
            row.addView(positionTextView);

            TextView carNameTextView = new TextView(this);
            carNameTextView.setText(result.getName());
            carNameTextView.setPadding(8, 8, 8, 8);
            carNameTextView.setTextColor(Color.BLACK);
            row.addView(carNameTextView);

            TextView earningsTextView = new TextView(this);
            earningsTextView.setText(getEarningsString(result.getEarnings()));
            earningsTextView.setPadding(8, 8, 8, 8);
            earningsTextView.setTextColor(Color.BLACK);
            row.addView(earningsTextView);

            resultsTable.addView(row);
            isEvenRow = !isEvenRow;
        }
    }

    private String getPositionString(int position) {
        switch (position) {
            case 1:
                return "1st";
            case 2:
                return "2nd";
            case 3:
                return "3rd";
            default:
                return String.valueOf(position);
        }
    }

    private String getEarningsString(int earnings) {
        if (earnings > 0) {
            return "+" + earnings;
        } else {
            return String.valueOf(earnings);
        }
    }

    private boolean didPlayerWin(ArrayList<Car> raceResults) {
        for (Car car : raceResults) {
            if (car.getPosition() == 1 && isCarBetOnByPlayer(car)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCarBetOnByPlayer(Car car) {
        return car.isCarSelected();
    }

    // display total earnings and update user balance
    private void displayTotalEarnings(float totalEarnings) {
        totalEarningsTextView.setText("Total Earnings: " + totalEarnings);
        user.setBalance(user.getBalance() + totalEarnings);
        sessionManager.updateUser(authService.userToJson(user));
        //updateJsonUserToFile(user);
    }

    // update user json with balance changes into file
    private void updateJsonUserToFile(User user) {
        File file = new File(getFilesDir(), "userlist.txt");

        if (!file.exists()) {
            Toast.makeText(this, "Chưa có tài khoản nào được tạo.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (file.length() == 0) {
            Toast.makeText(this, "Chưa có tài khoản nào được tạo.", Toast.LENGTH_SHORT).show();
            return;
        }

        try (
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
        ) {
            StringBuilder updatedContent = new StringBuilder();
            String line;
            AuthService authService = new AuthService();
            boolean userUpdated = false;

            // Read through all users and update the relevant user
            while ((line = br.readLine()) != null) {
                User existingUser = authService.jsonToUser(line);

                if (existingUser.getUsername().equals(user.getUsername())) {
                    // Update the user's balance
                    existingUser.setBalance(user.getBalance());
                    userUpdated = true;
                }

                // Append updated user or existing user to the content
                updatedContent.append(authService.userToJson(existingUser)).append("\n");
            }

            // Write updated content back to the file if user was updated
            if (userUpdated) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(updatedContent.toString().getBytes());
                    Toast.makeText(this, "User balance updated successfully.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User not found in file.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error updating user in file.", Toast.LENGTH_SHORT).show();
        }
    }
}

