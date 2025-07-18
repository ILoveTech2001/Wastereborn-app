package com.example.wastereborn.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.example.wastereborn.R;
import com.example.wastereborn.api.ApiClient;
import com.example.wastereborn.model.ApiResponse;
import com.example.wastereborn.utils.SessionManager;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickupFragment extends Fragment {

    private Spinner spinnerWasteType, spinnerStreet, spinnerPickupSlot;
    private EditText editSpecialInstructions, editEstimatedWeight;
    private Button btnConfirmPickup;

    private final String[] wasteTypes = {"Plastic", "Electronics", "Glass", "Paper", "Organic", "General Waste"};
    private final String[] streets = {"Melen", "Bastos", "Biyem-Assi", "Odza", "Ekounou"};
    private final HashMap<String, String[]> pickupSlotsMap = new HashMap<>();

    private SharedPreferences sharedPreferences;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pickup, container, false);

        // Initialize views
        spinnerWasteType = view.findViewById(R.id.spinner_waste_type);
        spinnerStreet = view.findViewById(R.id.spinner_street);
        spinnerPickupSlot = view.findViewById(R.id.spinner_pickup_slot);
        editSpecialInstructions = view.findViewById(R.id.edit_special_instructions);
        editEstimatedWeight = view.findViewById(R.id.edit_estimated_weight);
        btnConfirmPickup = view.findViewById(R.id.btn_confirm_pickup);

        sharedPreferences = requireContext().getSharedPreferences("PickupPrefs", Context.MODE_PRIVATE);
        sessionManager = new SessionManager(requireContext());

        // Sample slot mappings
        pickupSlotsMap.put("Melen", new String[]{"Mon 8–10 AM", "Wed 3–5 PM"});
        pickupSlotsMap.put("Bastos", new String[]{"Tue 10–12 AM", "Fri 4–6 PM"});
        pickupSlotsMap.put("Biyem-Assi", new String[]{"Mon 2–4 PM", "Thu 9–11 AM"});
        pickupSlotsMap.put("Odza", new String[]{"Wed 1–3 PM", "Sat 10–12 AM"});
        pickupSlotsMap.put("Ekounou", new String[]{"Tue 8–10 AM", "Fri 3–5 PM"});

        setupSpinner(spinnerWasteType, wasteTypes);
        setupSpinner(spinnerStreet, streets);

        spinnerStreet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStreet = streets[position];
                updatePickupSlots(selectedStreet);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnConfirmPickup.setOnClickListener(v -> createPickupRequest());

        return view;
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        // Consider checking if getContext() is null, though with requireContext() in onCreateView it should be fine.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void updatePickupSlots(String street) {
        String[] slots = pickupSlotsMap.get(street);
        if (slots == null) {
            slots = new String[]{"No slots available"};
        }
        setupSpinner(spinnerPickupSlot, slots);
    }

    private void createPickupRequest() {
        String wasteType = spinnerWasteType.getSelectedItem() != null ? spinnerWasteType.getSelectedItem().toString() : "";
        String street = spinnerStreet.getSelectedItem() != null ? spinnerStreet.getSelectedItem().toString() : "";
        String slot = spinnerPickupSlot.getSelectedItem() != null ? spinnerPickupSlot.getSelectedItem().toString() : "";
        String specialInstructions = editSpecialInstructions != null ? editSpecialInstructions.getText().toString().trim() : "";
        String weightStr = editEstimatedWeight != null ? editEstimatedWeight.getText().toString().trim() : "";

        // Validation
        if (wasteType.isEmpty() || street.isEmpty() || slot.isEmpty() || "No slots available".equals(slot)) {
            Toast.makeText(getContext(), "Please complete all fields and select a valid slot", Toast.LENGTH_SHORT).show();
            return;
        }

        double estimatedWeight = 0.0;
        if (!weightStr.isEmpty()) {
            try {
                estimatedWeight = Double.parseDouble(weightStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter a valid weight", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(getContext(), "Please login to create pickup request", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        setLoading(true);

        // Create pickup request object
        PickupRequestData pickupRequest = new PickupRequestData();
        pickupRequest.pickupAddress = street + " Quarter";
        pickupRequest.pickupStreet = street;
        pickupRequest.pickupCity = "Yaoundé";
        pickupRequest.wasteType = wasteType;
        pickupRequest.estimatedWeight = estimatedWeight;
        pickupRequest.specialInstructions = specialInstructions;
        pickupRequest.preferredPickupSlot = slot;

        // Make API call
        ApiClient.getApiService().createPickupRequest(sessionManager.getAuthHeader(), pickupRequest)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                        setLoading(false);

                        if (response.isSuccessful() && response.body() != null) {
                            // Save permanent address
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user_home_address", street);
                            editor.apply();

                            Toast.makeText(getContext(), "Pickup request created successfully! You'll earn 5 points when completed.", Toast.LENGTH_LONG).show();

                            // Clear form
                            clearForm();
                        } else {
                            Toast.makeText(getContext(), "Failed to create pickup request", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                        setLoading(false);
                        Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean isLoading) {
        btnConfirmPickup.setEnabled(!isLoading);
        btnConfirmPickup.setText(isLoading ? "Creating Request..." : "Confirm Pickup");
    }

    private void clearForm() {
        if (editSpecialInstructions != null) {
            editSpecialInstructions.setText("");
        }
        if (editEstimatedWeight != null) {
            editEstimatedWeight.setText("");
        }
        // Reset spinners to first item
        spinnerWasteType.setSelection(0);
        spinnerStreet.setSelection(0);
    }

    // Inner class for pickup request data
    private static class PickupRequestData {
        public String pickupAddress;
        public String pickupStreet;
        public String pickupCity;
        public String wasteType;
        public double estimatedWeight;
        public String specialInstructions;
        public String preferredPickupSlot;
    }
}