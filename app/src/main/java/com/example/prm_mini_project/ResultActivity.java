package com.example.prm_mini_project;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_mini_project.Entity.Car;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {
    TableLayout resultsTable;
    TextView totalEarningsTextView;
    TextView winMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        resultsTable = findViewById(R.id.resultsTable);
        totalEarningsTextView = findViewById(R.id.totalEarningsTextView);
        winMessageTextView = findViewById(R.id.winMessageTextView);
        ArrayList<Car> raceResults = getRaceResults();
        int totalEarnings = calculateTotalEarnings(raceResults);
        displayResults(raceResults);
        displayTotalEarnings(totalEarnings);
        if (didPlayerWin(raceResults)) {
            winMessageTextView.setText("You Win!");
        } else {
            winMessageTextView.setText("You Lose!");
        }
    }

    private ArrayList<Car> getRaceResults() {
        ArrayList<Car> results = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("CAR_RESULTS")) {
            results = (ArrayList<Car>) intent.getSerializableExtra("CAR_RESULTS");
        }
        return results;
    }

    private int calculateTotalEarnings(ArrayList<Car> raceResults) {
        int totalEarnings = 0;
        for (Car result : raceResults) {
            switch (result.getPosition()) {
                case 1:
                    result.setEarnings(result.getEarnings() + 100);
                    break;
                case 2:
                    result.setEarnings(result.getEarnings() - 20);
                    break;
                case 3:
                    result.setEarnings(result.getEarnings() - 50);
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
            case 1: return "1st";
            case 2: return "2nd";
            case 3: return "3rd";
            default: return String.valueOf(position);
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

        return false;
    }

    private void displayTotalEarnings(int totalEarnings) {
        totalEarningsTextView.setText("Total Earnings: " + totalEarnings);
    }
}