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
// import android.widget.ImageView; // ImageView was imported but not used, can be removed
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wastereborn.R;

import java.util.HashMap;

public class PickupFragment extends Fragment {

    private Spinner spinnerWasteType, spinnerStreet, spinnerPickupSlot;
    private Button btnConfirmPickup;

    private final String[] wasteTypes = {"Plastic", "electronics", "Glass", "Paper", "Organic","general waste"};
    private final String[] streets = {"Melen", "Bastos", "Biyem-Assi", "Odza", "Ekounou"};
    private final HashMap<String, String[]> pickupSlotsMap = new HashMap<>();

    private SharedPreferences sharedPreferences;

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
        btnConfirmPickup = view.findViewById(R.id.btn_confirm_pickup);

        sharedPreferences = requireContext().getSharedPreferences("PickupPrefs", Context.MODE_PRIVATE);

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

        btnConfirmPickup.setOnClickListener(v -> {
            // It's good practice to ensure getSelectedItem() is not null before calling toString()
            // though with ArrayAdapters of Strings it's usually safe if an item is selected.
            String wasteType = spinnerWasteType.getSelectedItem() != null ? spinnerWasteType.getSelectedItem().toString() : "";
            String street = spinnerStreet.getSelectedItem() != null ? spinnerStreet.getSelectedItem().toString() : "";
            String slot = spinnerPickupSlot.getSelectedItem() != null ? spinnerPickupSlot.getSelectedItem().toString() : "";


            if (wasteType.isEmpty() || street.isEmpty() || slot.isEmpty() || "No slots available".equals(slot)) {
                Toast.makeText(getContext(), "Please complete all fields and select a valid slot", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save permanent address
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_home_address", street);
            editor.apply();

            // Later: Send pickup request to backend
            Toast.makeText(getContext(), "Pickup scheduled at " + slot + " in " + street, Toast.LENGTH_LONG).show();
        });

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
        String[] slots = pickupSlotsMap.get(street); // Get the value associated with the key
        if (slots == null) { // Check if the key was not found (get returns null)
            slots = new String[]{"No slots available"}; // Provide default value
        }
        setupSpinner(spinnerPickupSlot, slots);
    }
}