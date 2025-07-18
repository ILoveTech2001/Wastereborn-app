package com.example.wastereborn.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wastereborn.DashboardActivity;
import com.example.wastereborn.R;
import com.example.wastereborn.api.ApiClient;
import com.example.wastereborn.api.ApiService;
import com.example.wastereborn.model.JwtResponse;
import com.example.wastereborn.model.LoginRequest;
import com.example.wastereborn.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordText, signUpLink;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize views
        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.btn_login);
        forgotPasswordText = view.findViewById(R.id.forgot_password);
        signUpLink = view.findViewById(R.id.sign_up_link);

        // Initialize API service and session manager
        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(requireContext());

        // Set up click listeners
        loginButton.setOnClickListener(v -> performLogin());
        forgotPasswordText.setOnClickListener(v -> handleForgotPassword());
        signUpLink.setOnClickListener(v -> switchToSignup());

        return view;
    }

    private void performLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button during login
        loginButton.setEnabled(false);
        loginButton.setText("Signing in...");

        LoginRequest loginRequest = new LoginRequest(email, password);

        apiService.login(loginRequest).enqueue(new Callback<JwtResponse>() {
            @Override
            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                loginButton.setEnabled(true);
                loginButton.setText("SIGN IN");

                if (response.isSuccessful() && response.body() != null) {
                    JwtResponse jwtResponse = response.body();
                    
                    // Save user session
                    sessionManager.saveUserSession(jwtResponse);

                    // Navigate to dashboard
                    Intent intent = new Intent(getActivity(), DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    
                    Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JwtResponse> call, Throwable t) {
                loginButton.setEnabled(true);
                loginButton.setText("SIGN IN");
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleForgotPassword() {
        Toast.makeText(getContext(), "Forgot password feature coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void switchToSignup() {
        // Switch to signup fragment
        if (getActivity() instanceof com.example.wastereborn.AuthActivity) {
            // This will be handled by the parent activity
            getActivity().findViewById(R.id.btn_sign_up).performClick();
        }
    }
}
