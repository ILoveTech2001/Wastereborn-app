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
import com.example.wastereborn.model.SignupRequest;
import com.example.wastereborn.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupFragment extends Fragment {

    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private Button signupButton;
    private TextView signInLink;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // Initialize views
        nameEditText = view.findViewById(R.id.name);
        emailEditText = view.findViewById(R.id.email);
        phoneEditText = view.findViewById(R.id.phone);
        passwordEditText = view.findViewById(R.id.password);
        confirmPasswordEditText = view.findViewById(R.id.confirm_password);
        signupButton = view.findViewById(R.id.btn_signup);
        signInLink = view.findViewById(R.id.sign_in_link);

        // Initialize API service and session manager
        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(requireContext());

        // Set up click listeners
        signupButton.setOnClickListener(v -> performSignup());
        signInLink.setOnClickListener(v -> switchToLogin());

        return view;
    }

    private void performSignup() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button during signup
        signupButton.setEnabled(false);
        signupButton.setText("Creating account...");

        // Split name into first and last name
        String[] nameParts = name.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        SignupRequest signupRequest = new SignupRequest(firstName, lastName, email, phone, password);

        apiService.signup(signupRequest).enqueue(new Callback<JwtResponse>() {
            @Override
            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                signupButton.setEnabled(true);
                signupButton.setText("SIGN UP");

                if (response.isSuccessful() && response.body() != null) {
                    JwtResponse jwtResponse = response.body();
                    
                    // Save user session
                    sessionManager.saveUserSession(jwtResponse);

                    // Navigate to dashboard
                    Intent intent = new Intent(getActivity(), DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    
                    Toast.makeText(getContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMessage = "Registration failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            if (errorBody.contains("Email is already taken")) {
                                errorMessage = "Email is already taken!";
                            } else if (errorBody.contains("Phone number is already taken")) {
                                errorMessage = "Phone number is already taken!";
                            } else if (response.code() == 400) {
                                errorMessage = "Invalid data provided";
                            }
                        }
                    } catch (Exception e) {
                        // Use default error message
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JwtResponse> call, Throwable t) {
                signupButton.setEnabled(true);
                signupButton.setText("SIGN UP");
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchToLogin() {
        // Switch to login fragment
        if (getActivity() instanceof com.example.wastereborn.AuthActivity) {
            // This will be handled by the parent activity
            getActivity().findViewById(R.id.btn_sign_in).performClick();
        }
    }
}
