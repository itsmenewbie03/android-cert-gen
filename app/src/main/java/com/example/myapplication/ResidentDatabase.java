package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;



public class ResidentDatabase extends Fragment {
    private TableRow headerRow;
    private TableRow row1;
    private TextView idTextView;
    private TextView nameTextView;
    private TextView ageTextView;
    private TextView addressTextView;
    private Button deleteButton;
    private Button editButton;
    private Button addResidentButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_resident_database, container, false);

        headerRow = rootView.findViewById(R.id.headerRow);
        row1 = rootView.findViewById(R.id.row1);
        idTextView = row1.findViewById(R.id.idTextView);
        nameTextView = row1.findViewById(R.id.nameTextView);
        ageTextView = row1.findViewById(R.id.ageTextView);
        addressTextView = row1.findViewById(R.id.addressTextView);
        deleteButton = row1.findViewById(R.id.deleteButton1);
        editButton = row1.findViewById(R.id.editButton1);
        addResidentButton = rootView.findViewById(R.id.addResidentButton);

        // Set long press listener for row1
        row1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Hide row1 content
                idTextView.setVisibility(View.INVISIBLE);
                nameTextView.setVisibility(View.INVISIBLE);
                ageTextView.setVisibility(View.INVISIBLE);
                addressTextView.setVisibility(View.INVISIBLE);

                // Show delete and edit buttons
                deleteButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);
                return true;
            }
        });

        // Set click listeners for delete and edit buttons
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle delete button click
                Toast.makeText(getActivity(), "Delete clicked for row 1", Toast.LENGTH_SHORT).show();
                // Restore row1 content visibility
                idTextView.setVisibility(View.VISIBLE);
                nameTextView.setVisibility(View.VISIBLE);
                ageTextView.setVisibility(View.VISIBLE);
                addressTextView.setVisibility(View.VISIBLE);
                // Hide delete and edit buttons
                deleteButton.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.INVISIBLE);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle edit button click
                Toast.makeText(getActivity(), "Edit clicked for row 1", Toast.LENGTH_SHORT).show();
                // Restore row1 content visibility
                idTextView.setVisibility(View.VISIBLE);
                nameTextView.setVisibility(View.VISIBLE);
                ageTextView.setVisibility(View.VISIBLE);
                addressTextView.setVisibility(View.VISIBLE);
                // Hide delete and edit buttons
                deleteButton.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.INVISIBLE);
            }
        });

        // Set OnClickListener for the Add Resident button
        // Set OnClickListener for the Add Resident button
        addResidentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show add resident dialog
                showAddResidentDialog();
            }
        });

        return rootView;
    }

    private void showAddResidentDialog() {
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_add_resident, null); // Updated here
        final EditText firstNameEditText = view.findViewById(R.id.firstNameEditText);
        final EditText lastNameEditText = view.findViewById(R.id.lastNameEditText);
        final EditText ageEditText = view.findViewById(R.id.ageEditText);
        final EditText addressEditText = view.findViewById(R.id.addressEditText);
        Button saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        builder.setView(view);
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String age = ageEditText.getText().toString();
                String address = addressEditText.getText().toString();

                // Perform validation here if required

                // Example: Check if first name is not empty
                if (firstName.trim().isEmpty()) {
                    firstNameEditText.setError("First name is required");
                    return;
                }

                // Add your logic to save the resident or update UI
                // For now, just display a toast
                Toast.makeText(getActivity(), "Added Resident: " + firstName + " " + lastName, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}