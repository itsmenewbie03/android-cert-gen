package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class CreateEmployee extends Fragment {

    Button addEmployeeButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_employee, container, false);

        addEmployeeButton = rootView.findViewById(R.id.addEmployee);
        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEmployeeModal();
            }
        });

        return rootView;
    }

    private void showAddEmployeeModal() {
        // Inflate the modal layout
        View modalView = getLayoutInflater().inflate(R.layout.modal_add_employee, null);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(modalView);
        AlertDialog dialog = builder.create();

        // Initialize views
        EditText firstNameEditText = modalView.findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = modalView.findViewById(R.id.lastNameEditText);
        EditText ageEditText = modalView.findViewById(R.id.ageEditText);
        EditText addressEditText = modalView.findViewById(R.id.addressEditText);
        EditText roleEditText = modalView.findViewById(R.id.roleEditText);
        Button saveButton = modalView.findViewById(R.id.saveButton);
        Button cancelButton = modalView.findViewById(R.id.cancelButton);

        // Save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle save action
                // Here you can get the data from EditTexts and do whatever you want with it
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                int age = Integer.parseInt(ageEditText.getText().toString());
                String address = addressEditText.getText().toString();
                String role = roleEditText.getText().toString();

                // Close the dialog
                dialog.dismiss();

                // You can perform further actions here like saving the data to a database
            }
        });

        // Cancel button click listener
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the dialog
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }
}