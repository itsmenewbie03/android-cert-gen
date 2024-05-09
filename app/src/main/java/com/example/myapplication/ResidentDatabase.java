package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


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
        // TODO: try to load the resident database list I know this will fail but IDC
        ResidentDatabaseList myRequest = new ResidentDatabaseList();
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());

        // TODO: i love null pointer exception, let's throw a lot of them xD
        Task<GetTokenResult> tokenResultTask = FirebaseAuth.getInstance().getCurrentUser().getIdToken(false);

        TableView resident_table = rootView.findViewById(R.id.resident_table_view);
        AbstractTableAdapter<ColumnHeader, RowHeader, Cell> adapter = new MyTableViewAdapter();
        // TODO: fuck you I gotta bind the adapter first
        // fuck this shit
        resident_table.setAdapter(adapter);
        List<ColumnHeader> columnHeaderItems = new ArrayList<>();
        columnHeaderItems.add(new ColumnHeader("ID"));
        columnHeaderItems.add(new ColumnHeader("Name"));
        columnHeaderItems.add(new ColumnHeader("Gender"));
        columnHeaderItems.add(new ColumnHeader("Address"));
        adapter.setColumnHeaderItems(columnHeaderItems);

        ResidentDatabaseList.ResponseCallback callback = new ResidentDatabaseList.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {

                    JSONArray resident_list = response.getJSONArray("data");
                    for (int i = 0; i < resident_list.length(); i++) {
                       // TODO: parse each resident
                        JSONObject resident = resident_list.getJSONObject(i);
                        String firstName = resident.getString("first_name");
                        String lastName = resident.getString("last_name");
                        String middleName = resident.has("middle_name") ? resident.getString("middle_name") : "";

                        StringBuilder nameBuilder = new StringBuilder(firstName);

                        if (!middleName.isEmpty()) {
                            nameBuilder.append(" ").append(middleName);
                        }

                        nameBuilder.append(" ").append(lastName);
                        String id = String.valueOf(i+1);
                        String fullName = nameBuilder.toString();
                        String gender = resident.getString("gender");
                        String address = resident.getString("address");
                        RowHeader rowHeader = new RowHeader(id);
                        List<Cell> cells = new ArrayList<>();
                        cells.add(new Cell(id));
                        cells.add(new Cell(fullName));
                        cells.add(new Cell(gender));
                        cells.add(new Cell(address));
                        adapter.addRow(i, rowHeader, cells);
                    }
                } catch (Exception e){
                    Log.e("API_RESPONSE", "error parsing failed due to: " + e.getMessage());;
                }
            }
        };
      
        tokenResultTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                myRequest.makeRequest(requestQueue, token, callback);
            }
        });

        addResidentButton = rootView.findViewById(R.id.addResidentButton);
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