package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class fragment_profile extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int GALLERY_REQUEST_CODE = 1002;

    private static final int CAMERA_REQUEST_CODE = 1003;


    public fragment_profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize the Spinner
        Spinner spinnerOptions = view.findViewById(R.id.spinnerOptions);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions.setAdapter(adapter);

        // Set OnItemSelectedListener for the Spinner
        spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    // Default selection: "Upload Profile"
                    // You can choose to do nothing or perform any specific action here
                } else if (position == 1) {
                    // Option selected: Camera
                    if (checkPermissions()) {
                        openCamera();
                    }
                } else if (position == 2) {
                    // Option selected: Photo/Gallery
                    if (checkPermissions()) {
                        openGallery();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle when nothing is selected
            }
        });

        // Initialize the Upload Button
        Button uploadButton = view.findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the Upload Button
                v.setVisibility(View.GONE);

                // Show a toast message
                Toast.makeText(getActivity(), "Profile has been changed", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request the permission
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE); // Use CAMERA_REQUEST_CODE here
        } else {
            Toast.makeText(getActivity(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // Gallery result
            Uri selectedImageUri = data.getData();
            setImage(selectedImageUri);
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // Camera result
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Set the selected image to the ImageView
            ImageView imageView = getView().findViewById(R.id.imageView);
            imageView.setImageBitmap(imageBitmap);
            imageView.setVisibility(View.VISIBLE);

            // Hide the TextView
            TextView textView = getView().findViewById(R.id.textView);
            textView.setVisibility(View.GONE);
        }
    }

    private void setImage(Uri selectedImageUri) {
        // Set the selected image to the ImageView
        ImageView imageView = getView().findViewById(R.id.imageView);
        imageView.setImageURI(selectedImageUri);
        imageView.setVisibility(View.VISIBLE);

        // Hide the TextView
        TextView textView = getView().findViewById(R.id.textView);
        textView.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                // You can proceed with accessing the gallery and camera
                openGallery(); // You can change this to openCamera() if desired
            } else {

                Toast.makeText(getActivity(), "Permission denied. Please grant the permission to access the gallery.", Toast.LENGTH_SHORT).show();


            }
        }
    }
}
