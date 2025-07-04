package com.example.wastereborn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SignupFragment extends Fragment {

    private Spinner spinnerLocation;
    private EditText editOtherLocation, editName, editEmail, editPassword, editConfirmPassword;
    private Button btnSignUp;

    private String[] yaoundeLocations = {
            "Choose location", "Melen", "Bastos", "Biyem-Assi", "Odza", "Mvog-Ada", "Ekounou", "Emana",
            "Ngousso", "Nkolbisson", "Essos", "Etoudi", "Other"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // Initialize views
        spinnerLocation = view.findViewById(R.id.spinner_location);
        editOtherLocation = view.findViewById(R.id.edit_other_location);
        editName = view.findViewById(R.id.name);
        editEmail = view.findViewById(R.id.email);
        editPassword = view.findViewById(R.id.password);
        editConfirmPassword = view.findViewById(R.id.confirm_password);
        btnSignUp = view.findViewById(R.id.btn_signup);

        // Setup location dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, yaoundeLocations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(adapter);

        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = yaoundeLocations[position];
                if (selected.equals("Other")) {
                    editOtherLocation.setVisibility(View.VISIBLE);
                } else {
                    editOtherLocation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editOtherLocation.setVisibility(View.GONE);
            }
        });

        // Signup button listener
        btnSignUp.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            String confirmPassword = editConfirmPassword.getText().toString().trim();
            String selectedLocation = spinnerLocation.getSelectedItem().toString();
            String finalLocation = selectedLocation.equals("Other")
                    ? editOtherLocation.getText().toString().trim()
                    : selectedLocation;

            // Validation
            if (name.isEmpty()) {
                editName.setError("Name is required");
                return;
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editEmail.setError("Valid email is required");
                return;
            }

            if (password.isEmpty() || password.length() < 6) {
                editPassword.setError("Password must be at least 6 characters");
                return;
            }

            if (!password.equals(confirmPassword)) {
                editConfirmPassword.setError("Passwords do not match");
                return;
            }

            if (selectedLocation.equals("Choose location")) {
                Toast.makeText(getContext(), "Please select a valid location", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedLocation.equals("Other") && finalLocation.isEmpty()) {
                editOtherLocation.setError("Please enter your location");
                return;
            }

            // ✅ All good — navigate to DashboardActivity
            Toast.makeText(getContext(), "Signup successful! 🎉", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), DashboardActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
