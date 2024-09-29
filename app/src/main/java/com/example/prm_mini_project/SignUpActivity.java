package com.example.prm_mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm_mini_project.Entity.User;
import com.example.prm_mini_project.Service.AuthService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class SignUpActivity extends AppCompatActivity {
    // Variables
    Button signUpButton, backButton;
    EditText usernameEt, passwordEt, confirmPasswordEt;
    TextView tvLogin;
    AuthService authService = new AuthService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
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
        tvLogin = findViewById(R.id.tvLogin);

        // Back Button Click Event
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Check If Password Matches Confirm Password And If Username Is Already Taken
        signUpButton.setOnClickListener(v -> {
            String username = usernameEt.getText().toString();
            String password = passwordEt.getText().toString();
            String confirmPassword = confirmPasswordEt.getText().toString();

            // check if username is empty
            if (username.isEmpty()) {
                Toast.makeText(this, "Tên đăng nhập không được để trống.", Toast.LENGTH_SHORT).show();
                return;
            }

            // check if password is empty
            if (password.isEmpty()) {
                Toast.makeText(this, "Mật khẩu không được để trống.", Toast.LENGTH_SHORT).show();
                return;
            }

            // check if confirm password is empty
            if (confirmPassword.isEmpty()) {
                Toast.makeText(this, "Nhập lại mật khẩu không được để trống.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu và nhập lại mật khẩu không trùng khớp.", Toast.LENGTH_SHORT).show();
                return;
            }

            File file = getFile();

            if (isUsernameTaken(username, file)) {
                Toast.makeText(this, "Tên đăng nhập đã tồn tại.", Toast.LENGTH_SHORT).show();
            } else {
                saveUserData(username, password, file);
                Toast.makeText(this, "Đăng ký thành công.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                this.finish();
            }
        });

        // Login TextView Click Event
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            this.finish();
        });
    }

    private File getFile() {
        try {
            File file = new File(getFilesDir(), "userlist.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to check if the username already exists in the file stored in internal storage
    private boolean isUsernameTaken(String username, File file) {
        if (file == null) {
            return false;
        }

        try (
                FileInputStream fis = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = authService.jsonToUser(line);
                if (user.getUsername().equals(username)) {
                    reader.close();
                    return true; // Username found
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Username not found
    }

    private void saveUserData(String username, String password, File file) {
        if (file == null) {
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            String hashedPassword = authService.hashPassword(password);
            User user = new User(username, hashedPassword);
            String userData = authService.userToJson(user) + "\n";
            fos.write(userData.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SignUp", "Error saving user data.");
        }
    }
}