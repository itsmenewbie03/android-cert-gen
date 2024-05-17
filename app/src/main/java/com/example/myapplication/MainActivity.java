package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;

import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Button addResidentButton;

    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;


    AlertDialog addResidentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//        addResidentButton = findViewById(R.id.addResidentButton);
//        addResidentButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showAddResidentDialog();
//            }
//        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            Log.d("FUCK", "onCreate: " + user.getEmail());
            Task<GetTokenResult> tokenResultTask = user.getIdToken(false);
            tokenResultTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String token = task.getResult().getToken();
                    Log.d("TOKEN", "TOKEN:" + token);
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_dashboard) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
        } else if (itemId == R.id.nav_employee) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateEmployee()).commit();
        } else if (itemId == R.id.nav_document) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GenerateDocument()).commit();
        } else if (itemId == R.id.nav_resident) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ResidentDatabase()).commit();
        } else if (itemId == R.id.nav_report) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ReportFragment()).commit();
        } else if (itemId == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new fragment_profile()).commit();
        } else if (itemId == R.id.nav_logout) {
            Toast.makeText(this, "Bye Bye!", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer((GravityCompat.START));
        } else {
            super.onBackPressed();
        }
    }


//    private void showAddResidentDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View dialogView = getLayoutInflater().inflate(R.layout.add_resident_modal, null);
//        builder.setView(dialogView);
//
//        EditText nameField = dialogView.findViewById(R.id.nameField);
//        EditText ageField = dialogView.findViewById(R.id.ageField);
//        EditText birthdayField = dialogView.findViewById(R.id.birthdayField);
//        EditText residentYearsField = dialogView.findViewById(R.id.residentYearsField);
//        EditText addressField = dialogView.findViewById(R.id.addressField);
//        Button addResidentButton = dialogView.findViewById(R.id.addResidentButton);
//
//        addResidentButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Add resident logic here
//                String name = nameField.getText().toString();
//                String age = ageField.getText().toString();
//                String birthday = birthdayField.getText().toString();
//                String residentYears = residentYearsField.getText().toString();
//                String address = addressField.getText().toString();
//
//                // Validate input
//                if (!name.isEmpty() && !age.isEmpty() && !birthday.isEmpty() && !residentYears.isEmpty() && !address.isEmpty()) {
//                    // Add resident to database or perform necessary action
//                    // Dismiss the dialog
//                    addResidentDialog.dismiss();
//                } else {
//                    // Show error message if any field is empty
//                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        addResidentDialog = builder.create();
//        addResidentDialog.show();
//    }

}