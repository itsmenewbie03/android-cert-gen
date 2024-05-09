package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class GenerateDocument extends Fragment {
    private Button chooseDocumentButton;
    private AlertDialog documentModal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_generate_document, container, false);

        chooseDocumentButton = rootView.findViewById(R.id.chooseDocumentButton);

        chooseDocumentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDocumentModal();
            }
        });

        return rootView;
    }

    private void showDocumentModal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View modalView = getLayoutInflater().inflate(R.layout.modal_generate_document, null);
        builder.setView(modalView);

        Spinner documentTypeSpinner = modalView.findViewById(R.id.documentTypeSpinner);
        EditText firstNameEditText = modalView.findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = modalView.findViewById(R.id.lastNameEditText);
        EditText ageEditText = modalView.findViewById(R.id.ageEditText);
        EditText addressEditText = modalView.findViewById(R.id.addressEditText);
        Button saveButton = modalView.findViewById(R.id.saveButton);
        Button cancelButton = modalView.findViewById(R.id.cancelButton);

        // Define the options for the Spinner
        String[] documentTypes = {"Document 1", "Document 2", "Document3"};

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, documentTypes);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        documentTypeSpinner.setAdapter(adapter);

        // Set up click listeners for buttons
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle save button click
                // Get input values
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                // ... get other input values

                // Do something with the input values
                // For example, create the document
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle cancel button click
                documentModal.dismiss();
            }
        });

        documentModal = builder.create();
        documentModal.show();
    }
}