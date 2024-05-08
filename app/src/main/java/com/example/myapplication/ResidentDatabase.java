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
//        headerRow = rootView.findViewById(R.id.headerRow);
//        row1 = rootView.findViewById(R.id.row1);
//        idTextView = row1.findViewById(R.id.idTextView);
//        nameTextView = row1.findViewById(R.id.nameTextView);
//        ageTextView = row1.findViewById(R.id.ageTextView);
//        addressTextView = row1.findViewById(R.id.addressTextView);
//        deleteButton = row1.findViewById(R.id.deleteButton1);
//        editButton = row1.findViewById(R.id.editButton1);
//        addResidentButton = rootView.findViewById(R.id.addResidentButton);
//
//        // Set long press listener for row1
//        // TODO: make this applied for all rows
//        row1.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                // Hide row1 content
//                idTextView.setVisibility(View.INVISIBLE);
//                nameTextView.setVisibility(View.INVISIBLE);
//                ageTextView.setVisibility(View.INVISIBLE);
//                addressTextView.setVisibility(View.INVISIBLE);
//
//                // Show delete and edit buttons
//                deleteButton.setVisibility(View.VISIBLE);
//                editButton.setVisibility(View.VISIBLE);
//                return true;
//            }
//        });
//
//        // Set click listeners for delete and edit buttons
//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle delete button click
//                Toast.makeText(getActivity(), "Delete clicked for row 1", Toast.LENGTH_SHORT).show();
//                // Restore row1 content visibility
//                idTextView.setVisibility(View.VISIBLE);
//                nameTextView.setVisibility(View.VISIBLE);
//                ageTextView.setVisibility(View.VISIBLE);
//                addressTextView.setVisibility(View.VISIBLE);
//                // Hide delete and edit buttons
//                deleteButton.setVisibility(View.INVISIBLE);
//                editButton.setVisibility(View.INVISIBLE);
//            }
//        });
//
//        editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle edit button click
//                Toast.makeText(getActivity(), "Edit clicked for row 1", Toast.LENGTH_SHORT).show();
//                // Restore row1 content visibility
//                idTextView.setVisibility(View.VISIBLE);
//                nameTextView.setVisibility(View.VISIBLE);
//                ageTextView.setVisibility(View.VISIBLE);
//                addressTextView.setVisibility(View.VISIBLE);
//                // Hide delete and edit buttons
//                deleteButton.setVisibility(View.INVISIBLE);
//                editButton.setVisibility(View.INVISIBLE);
//            }
//        });
//
//        // Set OnClickListener for the Add Resident button
//        addResidentButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to AddResidentActivity
//                Intent intent = new Intent(getActivity(), AddResidentActivity.class);
//                startActivity(intent);
//            }
//        });
//
        return rootView;
    }
}