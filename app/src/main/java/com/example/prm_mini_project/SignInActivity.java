package com.example.prm_mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm_mini_project.Entity.SessionManager;
import com.example.prm_mini_project.Entity.User;
import com.example.prm_mini_project.Service.AuthService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class SignInActivity extends AppCompatActivity {
    Button btnSignIn, btnExit;
    EditText etUsername, etPassword;
    SessionManager sessionManager;
    TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSignIn = findViewById(R.id.btnSignIn);
        btnExit = findViewById(R.id.btnExit);
        tvRegister = findViewById(R.id.tvRegister);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        btnSignIn.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (username.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (login(username, password)) {
                // if username and password are correct, start the game
                sessionManager = new SessionManager(this);
                sessionManager.createLoginSession(username);
                startActivity(new Intent(SignInActivity.this, GameActivity.class));
                finish();
            }
            else{
                Toast.makeText(this, "Đăng nhập thất bại, tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
            }
        });

        btnExit.setOnClickListener(v -> {
            finish();
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            finish();
        });
    }

    private boolean login(String username, String password) {
        File file = new File(getFilesDir(), "userlist.txt");
        if (!file.exists()) {
            Toast.makeText(this, "Chưa có tài khoản nào được tạo.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (file.length() == 0) {
            Toast.makeText(this, "Chưa có tài khoản nào được tạo.", Toast.LENGTH_SHORT).show();
            return false;
        }

        try (
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
            )
        {
            String line;
            AuthService authService = new AuthService();
            // get user by username from file
            while ((line = br.readLine()) != null) {
                User user = authService.jsonToUser(line);
                if (user == null) {
                    continue;
                }

                if (user.getUsername().equals(username)) {
                    // check password
                    if (authService.checkPassword(password, user.getHashedPassword())) {
                        return true;
                    } else {
                        Toast.makeText(this, "Sai mật khẩu.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Nguoi", Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}