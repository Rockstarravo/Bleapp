package com.ev.bleapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FieldsActivity extends AppCompatActivity {


    private View simFieldsContainer;
    private View wifiFieldsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fields);

        // Get the device information passed from MainActivity
        String deviceInfo = getIntent().getStringExtra("DEVICE_INFO");
        Log.d("FieldsActivity", "Received device info: " + deviceInfo);

        // Display the device information
        TextView deviceInfoTextView = findViewById(R.id.deviceInfoTextView);
        deviceInfoTextView.setText(deviceInfo);

        simFieldsContainer = findViewById(R.id.simFieldsContainer);
        wifiFieldsContainer = findViewById(R.id.wifiFieldsContainer);
        MaterialButtonToggleGroup toggleButtonGroup = findViewById(R.id.toggleButtonGroup);

        // Set initial visibility (assuming SIM is selected by default)
        updateFieldsVisibility(R.id.simButton);

        // Set up toggle listener
        toggleButtonGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                updateFieldsVisibility(checkedId);
            }
        });

        // Set SIM as default selection
        toggleButtonGroup.check(R.id.simButton);

        FloatingActionButton submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle form submission here
                Toast.makeText(FieldsActivity.this, "Form Submitted", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateFieldsVisibility(int checkedId) {
        if (checkedId == R.id.simButton) {
            simFieldsContainer.setVisibility(View.VISIBLE);
            wifiFieldsContainer.setVisibility(View.GONE);
        } else if (checkedId == R.id.wifiButton) {
            simFieldsContainer.setVisibility(View.GONE);
            wifiFieldsContainer.setVisibility(View.VISIBLE);
        }
    }
}