package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int GALLERY_REQUEST_CODE = 1002;

    private static final int CAMERA_REQUEST_CODE = 1003;

    private String access_token;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        RequestQueue requestQueue = Volley.newRequestQueue(this.requireContext());
        TextView employeeName = view.findViewById(R.id.employeeName);
        // TODO: crazy but I ain't changing the ID
        TextView employeeGender = view.findViewById(R.id.employeeAge);
        AccountDataRequest accountDataRequest = new AccountDataRequest();
        AccountDataRequest.ResponseCallback callback = new AccountDataRequest.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    Log.d("API", response.toString());
                    JSONObject data = response.getJSONObject("data");
                    String name = data.getString("first_name") + " " + data.getString("last_name");
                    String gender = data.getString("gender");
                    String avatar_url = data.getString("avatar_url");
                    String capitalizedGender = gender.substring(0, 1).toUpperCase() + gender.substring(1);
                    Glide.with(getContext())
                            .load(avatar_url)
                            .into((ImageView) view.findViewById(R.id.imageView));
                    employeeName.setText(String.format("Name: %s", name));
                    employeeGender.setText(String.format("Gender: %s", capitalizedGender));
                } catch (Exception e) {
                    Log.e("API", "Error: " + e.getMessage());
                }
            }
        };
        Task<GetTokenResult> tokenResultTask = FirebaseAuth.getInstance().getCurrentUser().getIdToken(false);
        tokenResultTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                setAccessToken(token);
                accountDataRequest.makeRequest(requestQueue, token, callback);
            }
        });
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
                    // TODO: add return to remove warning xD
                    return;
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
                ImageView imageView = view.findViewById(R.id.imageView);
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                AccountAvatarChangeRequest accountAvatarChangeRequest = new AccountAvatarChangeRequest();
                AccountAvatarChangeRequest.VolleyListener listener = new AccountAvatarChangeRequest.VolleyListener() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.d("API", response.toString());
                        Toast.makeText(getActivity(), "Profile has been changed", Toast.LENGTH_SHORT).show();
                        v.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(String message) {
                        Log.e("API", message);
                        Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                };
                accountAvatarChangeRequest.changeAvatar(getActivity(), encodedImage, access_token, listener);
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
        }
    }

    private void setImage(Uri selectedImageUri) {
        // Set the selected image to the ImageView
        ImageView imageView = getView().findViewById(R.id.imageView);
        imageView.setImageURI(selectedImageUri);
        imageView.setVisibility(View.VISIBLE);
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

    private void setAccessToken(String access_token) {
        this.access_token = access_token;
    }
}
