package com.example.prm_mini_project;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        resultsTable = findViewById(R.id.resultsTable);
        totalEarningsTextView = findViewById(R.id.totalEarningsTextView);

        ArrayList<Car> raceResults = getRaceResults();
        int totalEarnings = calculateTotalEarnings(raceResults);
        displayResults(raceResults);
        displayTotalEarnings(totalEarnings);
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
        for (Car result : raceResults) {
            TableRow row = new TableRow(this);
            TextView carNameTextView = new TextView(this);
            carNameTextView.setText(result.getName());
            row.addView(carNameTextView);
            TextView positionTextView = new TextView(this);
            positionTextView.setText(String.valueOf(result.getPosition()));
            row.addView(positionTextView);
            TextView earningsTextView = new TextView(this);
            earningsTextView.setText(String.valueOf(result.getEarnings()));
            row.addView(earningsTextView);
            resultsTable.addView(row);
        }
    }

    private void displayTotalEarnings(int totalEarnings) {
        totalEarningsTextView.setText("Total Earnings: " + totalEarnings);
    }
}