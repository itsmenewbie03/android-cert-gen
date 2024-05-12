package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

    private JSONArray residentList;

    // TODO: I'm so annoyed getting token in each request, let's just store it in a variable
    private String access_token;

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
                    setResidentList(resident_list);
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
                        String id = String.valueOf(i + 1);
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
                } catch (Exception e) {
                    Log.e("API_RESPONSE", "error parsing failed due to: " + e.getMessage());
                    ;
                }
            }
        };

        tokenResultTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                setAccessToken(token);
                myRequest.makeRequest(requestQueue, token, callback);
            }
        });

        addResidentButton = rootView.findViewById(R.id.addResidentButton);
        addResidentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddResidentDialog();
            }
        });


        Button deleteResidentButton = rootView.findViewById(R.id.deleteResidentButton);
        deleteResidentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteResidentDialog();
            }
        });


        Button editResidentButton = rootView.findViewById(R.id.editResidentButton);
        editResidentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditResidentDialog();
            }
        });

        return rootView;
    }

    private void showEditResidentDialog() {
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.modal_edit_resident, null); // Updated here
        final EditText firstNameEditText = view.findViewById(R.id.firstNameEditText);
        final EditText lastNameEditText = view.findViewById(R.id.lastNameEditText);
        final EditText addressEditText = view.findViewById(R.id.addressEditText);
        final EditText genderEditText = view.findViewById(R.id.genderEditText);

        Button updateButton = view.findViewById(R.id.updateButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        List<CustomSpinnerItem> residents_for_edit = new ArrayList<>();
        final String[] target = {""};

        for (int i = 0; i < residentList.length(); i++) {
            try {
                JSONObject resident = residentList.getJSONObject(i);
                String firstName = resident.getString("first_name");
                String lastName = resident.getString("last_name");
                String middleName = resident.has("middle_name") ? resident.getString("middle_name") : "";

                StringBuilder nameBuilder = new StringBuilder(firstName);

                if (!middleName.isEmpty()) {
                    nameBuilder.append(" ").append(middleName);
                }
                nameBuilder.append(" ").append(lastName);
                String fullName = nameBuilder.toString();
                String id = resident.getString("_id");
                residents_for_edit.add(new CustomSpinnerItem(id, fullName, firstName, lastName, middleName, "69", resident.getString("gender"), resident.getString("address")));
            } catch (Exception e) {
                Log.e("RESIDENT LIST", "error parsing failed due to: " + e.getMessage());
            }
        }
        ArrayAdapter<CustomSpinnerItem> userAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner, residents_for_edit);
        Spinner residentSpinner = view.findViewById(R.id.resident_edit_spinner);
        final CustomSpinnerItem[] update_data = {null};
        residentSpinner.setAdapter(userAdapter);
        residentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CustomSpinnerItem resident = (CustomSpinnerItem) parent.getSelectedItem();
                firstNameEditText.setText(resident.getFirst_name());
                lastNameEditText.setText(resident.getLast_name());
                addressEditText.setText(resident.getAddress());
                genderEditText.setText(resident.getGender());
                Toast.makeText(getContext(), "Selected " + resident.getName(), Toast.LENGTH_SHORT).show();
                // another stupid workaround xD
                target[0] = resident.getId();
                Log.d("SELECTED_RESIDENT", "Selected Resident: " + resident.getId() + " " + target[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        builder.setView(view);
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (target[0].isEmpty()) {
                    Toast.makeText(getActivity(), "Please select a resident to edit", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Show a test toast
                ResidentUpdateRequest request = new ResidentUpdateRequest();
                // define the callback
                ResidentUpdateRequest.VolleyListener callback = new ResidentUpdateRequest.VolleyListener() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Toast.makeText(getActivity(), "Resident Updated Successfully!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String message) {
                        Log.d("API", "Update failed" + message);
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                };
                update_data[0] = new CustomSpinnerItem("", "", firstNameEditText.getText().toString(), lastNameEditText.getText().toString(), "", "69", genderEditText.getText().toString(), addressEditText.getText().toString());
                request.updateUser(getActivity(), target[0], update_data[0], access_token, callback);
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

    private void showDeleteResidentDialog() {
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.modal_delete_resident, null); // Updated here
        Button deleteButton = view.findViewById(R.id.deleteButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        List<CustomSpinnerItem> residents = new ArrayList<>();
        final String[] target = {""};

        for (int i = 0; i < residentList.length(); i++) {
            try {
                JSONObject resident = residentList.getJSONObject(i);
                String firstName = resident.getString("first_name");
                String lastName = resident.getString("last_name");
                String middleName = resident.has("middle_name") ? resident.getString("middle_name") : "";

                StringBuilder nameBuilder = new StringBuilder(firstName);

                if (!middleName.isEmpty()) {
                    nameBuilder.append(" ").append(middleName);
                }
                nameBuilder.append(" ").append(lastName);
                String fullName = nameBuilder.toString();
                String id = resident.getString("_id");
                residents.add(new CustomSpinnerItem(id, fullName));
            } catch (Exception e) {
                Log.e("RESIDENT LIST", "error parsing failed due to: " + e.getMessage());
                ;
            }
        }
        ArrayAdapter<CustomSpinnerItem> userAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner, residents);
        Spinner residentSpinner = view.findViewById(R.id.resident_spinner);
        residentSpinner.setAdapter(userAdapter);
        residentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CustomSpinnerItem resident = (CustomSpinnerItem) parent.getSelectedItem();
                Toast.makeText(getContext(), "Selected " + resident.getName(), Toast.LENGTH_SHORT).show();
                // another stupid workaround xD
                target[0] = resident.getId();
                // LOG the selected resident
                Log.d("SELECTED_RESIDENT", "Selected Resident: " + resident.getId() + " " + target[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        builder.setView(view);
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (target[0].isEmpty()) {
                    Toast.makeText(getActivity(), "Please select a resident to delete", Toast.LENGTH_SHORT).show();
                    return;
                }
                ResidentDeleteRequest request = new ResidentDeleteRequest();
                ResidentDeleteRequest.VolleyListener callback = new ResidentDeleteRequest.VolleyListener() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Toast.makeText(getActivity(), "Resident Deleted Successfully!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String message) {
                        Log.d("API", "Deletion failed" + message);
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                };
                request.deleteUser(getActivity(), target[0], access_token, callback);
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
                // TODO: i'm too tired to rename the rest of variables xD
                String gender = ageEditText.getText().toString();
                String address = addressEditText.getText().toString();

                // Perform validation here if required

                // Example: Check if first name is not empty
                if (firstName.trim().isEmpty()) {
                    firstNameEditText.setError("First name is required");
                    return;
                }

                if (lastName.trim().isEmpty()) {
                    lastNameEditText.setError("Last name is required");
                    return;
                }

                if (gender.trim().isEmpty()) {
                    ageEditText.setError("Gender is required");
                    return;
                }

                if (address.trim().isEmpty()) {
                    addressEditText.setError("Address is required");
                    return;
                }
                // Add your logic to save the resident or update UI
                // For now, just display a toast
                User user = new User();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setGender(gender);
                user.setAddress(address);

                ResidentRegisterRequest request = new ResidentRegisterRequest();
                Task<GetTokenResult> tokenResultTask = FirebaseAuth.getInstance().getCurrentUser().getIdToken(false);

                tokenResultTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult().getToken();
                        request.registerUser(getContext(), user, token, new ResidentRegisterRequest.VolleyListener() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                Toast.makeText(getActivity(), "Resident Added Successfully!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String message) {
                                Log.d("API", "Registration failed" + message);
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

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

    private void setResidentList(JSONArray residentList) {
        this.residentList = residentList;
    }

    private void setAccessToken(String access_token) {
        this.access_token = access_token;
    }
}