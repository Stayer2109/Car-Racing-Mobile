package com.example.prm_mini_project;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class SignUpActivity extends AppCompatActivity {
    // Variables
    Button signUpButton;
    Button backButton;
    EditText usernameEt;
    EditText passwordEt;
    EditText confirmPasswordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_acvitity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Mappings
        signUpButton = findViewById(R.id.signUpButton);
        backButton = findViewById(R.id.backButton);
        usernameEt = findViewById(R.id.usernameEt);
        passwordEt = findViewById(R.id.passwordEt);
        confirmPasswordEt = findViewById(R.id.confirmPasswordEt);

        // Back Button Click Event
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Check If Password Matches Confirm Password And If Username Is Already Taken
        signUpButton.setOnClickListener(v -> {
            String username = usernameEt.getText().toString();
            String password = passwordEt.getText().toString();
            String confirmPassword = confirmPasswordEt.getText().toString();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu và nhập lại mật khẩu không trùng khớp.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isUsernameTaken(username)) {
                Toast.makeText(this, "Tên đăng nhập đã tồn tại.", Toast.LENGTH_SHORT).show();
            } else {
                saveUserData(username, password);
                Toast.makeText(this, "Đăng ký thành công.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to check if the username already exists in the file stored in internal storage
    private boolean isUsernameTaken(String username) {
        try {
            File file = new File(getFilesDir(), "userlist.txt"); // Internal storage file
            if (!file.exists()) {
                return false; // If file does not exist, return false
            }

            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(","); // Assuming each line has "username,password"
                if (userDetails.length > 0 && userDetails[0].equals(username)) {
                    // Username found in the file
                    return true;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Username not found
    }

    private void saveUserData(String username, String password) {
        try {
            // Create or open the file in internal storage
            File file = new File(getFilesDir(), "userlist.txt");

            // Check if the file exists
            if (file.exists()) {
                return;
            } else {
                file.createNewFile();
            }

            // Open file in append mode (true) to save new data
            FileOutputStream fos = new FileOutputStream(file, true);

            // Prepare data to save in the desired format
            String userData = "Username: " + username + ", Password: " + password + "\n";

            // Write data to the file
            fos.write(userData.getBytes());

            // Close the output stream
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SignUp", "Error saving user data.");
        }
    }
}