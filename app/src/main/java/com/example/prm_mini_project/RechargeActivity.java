package com.example.prm_mini_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prm_mini_project.Entity.SessionManager;
import com.example.prm_mini_project.Entity.User;
import com.example.prm_mini_project.Service.AuthService;

public class RechargeActivity extends AppCompatActivity {

    EditText etRechargeAmount;
    Button btnRecharge;
    SessionManager sessionManager;
    AuthService authService = new AuthService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge); // Create the layout (activity_recharge.xml)

        etRechargeAmount = findViewById(R.id.etRechargeAmount);
        btnRecharge = findViewById(R.id.btnRecharge);
        sessionManager = new SessionManager(this);

        btnRecharge.setOnClickListener(view -> {
            String amountString = etRechargeAmount.getText().toString();

            if (amountString.isEmpty()) {
                Toast.makeText(RechargeActivity.this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount = Integer.parseInt(amountString);
            if (amount <= 0) {
                Toast.makeText(RechargeActivity.this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            rechargeBalance(amount);
        });
    }

    private void rechargeBalance(int amount) {
        // 1. Get current user from SharedPreferences
        User currentUser = authService.jsonToUser(sessionManager.getUserJson());

        // 2. Update user balance
        currentUser.setBalance(currentUser.getBalance() + amount);

        // 3. Save updated user back to SharedPreferences
        sessionManager.updateUser(authService.userToJson(currentUser));

        // 4. Provide feedback to the user (e.g., a Toast message)
        Toast.makeText(RechargeActivity.this, "Nạp tiền thành công!", Toast.LENGTH_SHORT).show();

        // 5. (Optional) Close RechargeActivity or navigate back
        finish();
    }
}