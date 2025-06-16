package com.example.a2048test3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a2048test3.database.GameApi;
import com.example.a2048test3.database.LoginRequest;
import com.example.a2048test3.database.LoginResponse;
import com.example.a2048test3.database.MoveCountRequest;
import com.example.a2048test3.database.RegisterRequest;
import com.example.a2048test3.database.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button backButton;
    private Button loginButton;
    private Button registerButton;
    private TextView statusTextView;
    private GameApi gameApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Setup window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.goBackButton);
        statusTextView = findViewById(R.id.statusTextView);  // TextView to display status messages


        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Wprowadź login i hasło", Toast.LENGTH_SHORT).show();
                return;
            }

            // Attempt to log in
            LoginRequest loginRequest = new LoginRequest(username, password);
            gameApi.login(loginRequest).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        // Successfully logged in
                        statusTextView.setText("Login udany!");  // Optionally show success message

                        // Send username and password to MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", username);  // Pass the username
                        intent.putExtra("password", password);  // Pass the password
                        startActivity(intent);
                        finish();  // Close the login activity
                    } else {
                        statusTextView.setText("Login nieudany. Sprawdź swoje dane.");
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    statusTextView.setText("Login nieudany: " + t.getMessage());
                }
            });
        });

        // Back Button
        backButton.setOnClickListener(v -> finish());  // Powrót

        // Register Button
        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Wprowadź login i hasło", Toast.LENGTH_SHORT).show();
                return;
            }

            // Attempt to register
            RegisterRequest registerRequest = new RegisterRequest(username, password);
            gameApi.register(registerRequest).enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    if (response.isSuccessful()) {
                        // Successfully registered
                        statusTextView.setText("Rejestracja udana! Zaloguj się.");
                        Toast.makeText(LoginActivity.this, "Rejestracja udana!", Toast.LENGTH_SHORT).show();
                    } else {
                        statusTextView.setText("Rejestracja nieudana! Spróbuj znowu.");
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    statusTextView.setText("Rejestracja nieudana: " + t.getMessage());
                }
            });
        });

    }
}