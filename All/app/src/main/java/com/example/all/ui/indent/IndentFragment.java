package com.example.all.ui.indent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.all.R;
import com.example.all.UserDataSingleton;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class IndentFragment extends Fragment {
    private final String TAG = "INDENT_GENERATOR";
    public  String HsdAmountValue;
    public String HsdValue;
    public  String RemarksValue;
    public String VehicleNumberValue;
    public int VehicleNumberIDValue;
    public String DriverNameValue;
    public  int DriverIDValue;
    public String BunkNameValue;
    public int BunkIDValue;
    public String DateValue;
    public String BunkCashValue;
    public String sqlDateFormat;
    public  String IssuedByValue;
    public boolean BycanValue;
    public String indentNoValue;
    public String username;
    private final Dictionary<String, Integer> driver_dict= new Hashtable<>();
    public String DriverName;
    public String UniqueID;



    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";

    String call="3.7.171.201", db="BTS", un="user", passwords="Mk@5942";
    Connection connect;
    ResultSet rs;

    DatePickerDialog datePickerDialog;
    Dialog dialog;
    ArrayList list;

    @SuppressLint("NewApi")
    private Connection CONN(String _user, String _pass, String _DB,
                            String _server) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnURL = "jdbc:jtds:sqlserver://" + _server + ";"
                    + "databaseName=" + _DB + ";user=" + _user + ";password="
                    + _pass + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("SQLException ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("lassNotFoundException ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("Exception  ERRO", e.getMessage());
        }
        return conn;
    }
    EditText date ,Remarks,IssuedBy,BunkCash,hsd,hsdAmount;
    public TextView DriverDropDownList,VehicleNumberDropDownList,BunkDropDownList;
    CheckBox checkBox;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_indent, container, false);

        // Initialize your views
        DriverDropDownList = rootView.findViewById(R.id.driverrdropdown);
        IssuedBy = rootView.findViewById(R.id.issuesby_edit);
        username = UserDataSingleton.getInstance().getUsername();
        IssuedBy.setText(username);
        VehicleNumberDropDownList = rootView.findViewById(R.id.vehicalnumberdropdown);
        BunkDropDownList = rootView.findViewById(R.id.bunkdropdown);
        date = rootView.findViewById(R.id.editTextDate);
        hsd = rootView.findViewById(R.id.hsdeditText);
        hsdAmount = rootView.findViewById(R.id.hsdamountedittext);
        checkBox = rootView.findViewById(R.id.bycancheckbox);
        Remarks = rootView.findViewById(R.id.remarksedittext);
        BunkCash = rootView.findViewById(R.id.bunkcash);
        // Initialize other views...

        // Set initial date on date EditText
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        date.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
        DateValue = date.getText().toString().trim();
        sqlDateFormat = mYear  + "-" + (mMonth + 1) + "-" + mDay;

        // Set up click listener for date EditText
        date.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                DatePicker();
            }
        });

        // Set up click listener for submit button
        Button submit = rootView.findViewById(R.id.submitbutton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Validate().isEmpty()) {
                    Toast.makeText(getContext(), Validate(), Toast.LENGTH_SHORT).show();
                    return;
                }
                ValidateDatabase();
            }
        });

        // Attempt to establish a database connection
        connect = CONN(un, passwords, db, call);
        if (connect == null) {
            // Handle connection failure gracefully, e.g., show an error message
            Toast.makeText(requireContext(), "Failed to establish database connection", Toast.LENGTH_SHORT).show();
            return rootView;
        }

        Bunk();
        Driver();
        VehicleNumber();

        // Set up other views and event listeners...

        return rootView;
    }


    public void DatePicker(){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    sqlDateFormat =year  + "-" + (monthOfYear + 1) + "-" +dayOfMonth ;
                },mYear,mMonth,mDay);

        datePickerDialog.show();

    }

    public void Bunk(){
        Dictionary<String, Integer> Bunk_dict= new Hashtable<>();
        List Bunk_list = new ArrayList();
        connect = CONN(un, passwords, db, call);
        try {
            PreparedStatement statement = connect.prepareStatement("EXEC GetDieselBunk");

            rs = statement.executeQuery();
            while (rs.next()) {
                Bunk_list.add(rs.getString("Bunk Name"));
                Bunk_dict.put(rs.getString("Bunk Name"),rs.getInt("PartyID"));

            }
        } catch (SQLException e) {
            Toast.makeText(requireContext(), e.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
        }

        BunkDropDownList.setOnClickListener(v -> {
            // Initialize dialog
            dialog=new Dialog(getContext());

            // set custom dialog
            dialog.setContentView(R.layout.dialog_searchable_spinner);

            // set custom height and width
            dialog.getWindow().setLayout(850,1000);

            // set transparent background
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // show dialog
            dialog.show();
            // Assigning name to dailog box
            TextView BunkName = (TextView) dialog.findViewById(R.id.dropdownname);
            BunkName.setText("Select Bunk ");
            // Initialize and assign variable
            EditText editText=dialog.findViewById(R.id.edit_text);
            ListView listView=dialog.findViewById(R.id.list_view);
            // Initialize array adapter
            ArrayAdapter<String> adapter=new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,Bunk_list );
            // set adapter
            listView.setAdapter(adapter);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            listView.setOnItemClickListener((parent, view, position, id) -> {
                // when item selected from list
                // set selected item on textView
                BunkDropDownList.setText(adapter.getItem(position));
                BunkNameValue=BunkDropDownList.getText().toString();
                BunkIDValue = Bunk_dict.get(BunkNameValue);
                // Dismiss dialog
                dialog.dismiss();

            });


        });
    }

    public void Driver(){

        List Driver_list = new ArrayList();
        connect = CONN(un, passwords, db, call);
        try {
            PreparedStatement statement = connect.prepareStatement("EXEC GetDriverForAdvance");

            rs = statement.executeQuery();
            while (rs.next()) {
                Driver_list.add(rs.getString("Driver").replace("-","- "));
                driver_dict.put(rs.getString("Driver").replace("-","- "),rs.getInt("DriverID"));

            }
        } catch (SQLException e) {
            Toast.makeText(requireContext(), e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        DriverDropDownList.setOnClickListener(v -> {
            // Initialize dialog
            dialog=new Dialog(requireContext());

            // set custom dialog
            dialog.setContentView(R.layout.dialog_searchable_spinner);

            // set custom height and width
            dialog.getWindow().setLayout(850,1000);

            // set transparent background
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // show dialog
            dialog.show();
            // Assigning name to dailog box
            TextView DriverName = (TextView) dialog.findViewById(R.id.dropdownname);
            DriverName.setText("Select Driver ");
            // Initialize and assign variable

            EditText editText=dialog.findViewById(R.id.edit_text);
            ListView listView=dialog.findViewById(R.id.list_view);
            // Initialize array adapter
            ArrayAdapter<String> adapter=new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,Driver_list );
            // set adapter
            listView.setAdapter(adapter);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            listView.setOnItemClickListener((parent, view, position, id) -> {
                // when item selected from list
                // set selected item on textView
                DriverDropDownList.setText(adapter.getItem(position));
                DriverNameValue=DriverDropDownList.getText().toString();
                DriverIDValue = driver_dict.get(DriverNameValue);
                // Dismiss dialog
                dialog.dismiss();
            });


        });

    }
    public void VehicleNumber(){
        Dictionary<String, Integer> Vehicle_dict= new Hashtable<>();

        connect = CONN(un, passwords, db, call);
        try {
            PreparedStatement statement = connect.prepareStatement("EXEC GetVehicleFromVehicleDetails");
            list = new ArrayList();
            rs = statement.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("VehicleNo"));
                Vehicle_dict.put(rs.getString("VehicleNo"),rs.getInt("VehicleID"));

            }

        } catch (SQLException e) {
            Toast.makeText(requireContext(), e.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
        }

        VehicleNumberDropDownList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize dialog
                dialog=new Dialog(requireContext());

                // set custom dialog
                dialog.setContentView(R.layout.dialog_searchable_spinner);

                // set custom height and width
                dialog.getWindow().setLayout(850,1000);

                // set transparent background
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // show dialog
                dialog.show();
                TextView BunkName = (TextView) dialog.findViewById(R.id.dropdownname);
                BunkName.setText("Select vehicle number");
                // Initialize and assign variable
                EditText editText=dialog.findViewById(R.id.edit_text);
                ListView listView=dialog.findViewById(R.id.list_view);
                // Initialize array adapter
                ArrayAdapter<String> adapter=new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,list);
                // set adapter
                listView.setAdapter(adapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener((parent, view, position, id) -> {
                    // when item selected from list
                    // set selected item on textView
                    VehicleNumberDropDownList.setText(adapter.getItem(position));
                    VehicleNumberValue=VehicleNumberDropDownList.getText().toString();
                    VehicleNumberIDValue=Vehicle_dict.get(VehicleNumberValue);
                    // Dismiss dialog
                    dialog.dismiss();

                    try {
                        // Updated query to include the @VehicleID parameter
                        String statusQuery = "EXEC GetLatestDriver @VehicleID = ?";
                        PreparedStatement statusStatement = connect.prepareStatement(statusQuery);

                        // Bind the selected VehicleNumberIDValue as the parameter value
                        statusStatement.setInt(1, VehicleNumberIDValue);

                        ResultSet statusRs = statusStatement.executeQuery();
                        if (statusRs.next()) {
                            // Log the response
                            DriverName = statusRs.getString("DriverName");
                            DriverNameValue=DriverName;
                            DriverDropDownList.setText(DriverName);
                            String DriverID = statusRs.getString("DriverID");
                            Log.d("GetLatestDriver", "Driver Name: " + DriverName);
                            Log.d("GetLatestDriver", "Driver ID: " + DriverID);


                        } else {
                            Log.e("GetVehicleStatusExternal", "No status found for the selected vehicle.");
                        }
                    } catch (SQLException e) {
                        Log.e("GetVehicleStatusExternal", "Error executing stored procedure: " + e.getMessage());
                    }

                });

            }
        });

    }
    public String Validate() {
        //HSD or Hsd amount

        HsdAmountValue = hsdAmount.getText().toString().trim();
        HsdValue = hsd.getText().toString().trim();
        if (TextUtils.isEmpty(HsdValue) && TextUtils.isEmpty(HsdAmountValue)) {
            return "Enter  HSD or HSD Amount";
        }
        if (!TextUtils.isEmpty(HsdValue) && !TextUtils.isEmpty(HsdAmountValue)) {
            return "Enter either HSD or HSD Amount";
        }

        RemarksValue = Remarks.getText().toString().trim();
        if (checkBox.isChecked()) {
            BycanValue = true;
            if (TextUtils.isEmpty(RemarksValue)) {
                return "Please enter Remarks";
            }
        } else {
            if (TextUtils.isEmpty(VehicleNumberValue)) {
                return "Please Select VehicleNumber";
            }
            if (TextUtils.isEmpty(DriverNameValue)) {
                return "Please Select Driver";
            }
        }
        // For Selecting Bunk
        if (TextUtils.isEmpty(BunkNameValue)) {
            return "Select Bunk ";
        }

        IssuedByValue = IssuedBy.getText().toString().trim();
        Log.d("Username", "Username from UserDataSingleton: " + username);

        if (TextUtils.isEmpty(IssuedByValue)) {
            return "Please enter IssuedBy";
        }


        BunkCashValue = BunkCash.getText().toString();
        if (TextUtils.isEmpty(BunkCashValue)) {
            return "Please enter correct BunkCash";
        }
        return "";
    }
    public void ValidateDatabase(){
        connect = CONN(un, passwords, db, call);
        try {
            PreparedStatement statement = connect.prepareStatement("EXEC GetDieselIndentOndateAndVehicle " + "@Date='"+ sqlDateFormat+"', @VehicleID="+VehicleNumberIDValue);
            rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getString("HSD")!=null && rs.getString("HSDAmount")!=null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Do you want to add more Diesel ?");
                    builder.setMessage("Diesel already issued for this vehicle today. HSD:"+rs .getString("HSD")+
                            "and HSD Amount: "+rs.getString("HSDAmount")+ " Do you want to issue more?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                        AddDatatoDatabase();
                        IssuedBy.setText("Test");
                        VehicleNumberDropDownList.setText("");
                        BunkDropDownList.setText("");
                        BunkCash.setText("");
                        hsd.setText("");
                        hsdAmount.setText("0");
                        DriverDropDownList.setText("");
                        checkBox.setChecked(false);
                        Remarks.setText("");
                        Intent indent = new Intent(requireActivity(), IndentGenerator.class);
                        indent.putExtra("DateValue",DateValue);
                        if (!HsdValue.isEmpty()){
                            indent.putExtra("HsdValue",HsdValue);
                        }else {
                            indent.putExtra("HsdAmountValue",HsdAmountValue);
                        }
                        indent.putExtra("VehicleNumberValue",VehicleNumberValue);
                        indent.putExtra("BunkNameValue",BunkNameValue);
                        indent.putExtra("DriverNameValue",DriverNameValue);
                        indent.putExtra("IssuedByValue",IssuedByValue);
                        indent.putExtra("RemarksValue",RemarksValue);
                        indent.putExtra("BunkCashValue",BunkCashValue);
                        indent.putExtra("IndentValue",indentNoValue);
                        indent.putExtra("UniqueID",UniqueID);
                        startActivity(indent);
                    });
                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });

                    // Create the Alert dialog
                    AlertDialog alertDialog = builder.create();
                    // Show the Alert Dialog box
                    alertDialog.show();
                }
                else{
                    AddDatatoDatabase();
                    AlertDialog.Builder builder_SaveData = new AlertDialog.Builder(requireContext());
                    builder_SaveData.setTitle("Successful!!!");
                    builder_SaveData.setMessage("Data Added Successfully !!!...");
                    builder_SaveData.setCancelable(false);
                    builder_SaveData.setNeutralButton("ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });
                    AlertDialog alertDialog_SaveData = builder_SaveData.create();
                    // Show the Alert Dialog box
                    alertDialog_SaveData.show();
                    IssuedBy.setText("");
                    VehicleNumberDropDownList.setText("");
                    BunkDropDownList.setText("");
                    BunkCash.setText("");
                    hsd.setText("");
                    hsdAmount.setText("");
                    DriverDropDownList.setText("");
                    checkBox.setChecked(false);
                    Remarks.setText("");
                    Intent indent = new Intent(requireActivity() , IndentGenerator.class);
                    indent.putExtra("DateValue",DateValue);
                    if (!HsdValue.isEmpty()){
                        indent.putExtra("HsdValue",HsdValue);
                    }else {
                        indent.putExtra("HsdAmountValue",HsdAmountValue);
                    }
                    indent.putExtra("VehicleNumberValue",VehicleNumberValue);
                    indent.putExtra("BunkNameValue",BunkNameValue);
                    indent.putExtra("DriverNameValue",DriverNameValue);
                    indent.putExtra("IssuedByValue",IssuedByValue);
                    indent.putExtra("RemarksValue",RemarksValue);
                    indent.putExtra("BunkCashValue",BunkCashValue);
                    indent.putExtra("IndentValue",indentNoValue);
                    indent.putExtra("UniqueID",UniqueID);
                    startActivity(indent);
                }
                break;
            }
        } catch (SQLException e) {
            Toast.makeText(requireContext(), e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }
    public void AddDatatoDatabase() {
        connect = CONN(un, passwords, db, call);
        try {
            // Generate a uniqueID with 6 random characters
            UniqueID = generateRandomString(6);
            PreparedStatement statement = connect.prepareStatement("EXEC InsertDieselIndent @Date=?, @VehicleID=?, @DriverID=?, @Bunk=?, @Remarks=?, @IssuedBy=?, @BunkCash=?, @HSD=?, @HSDAmount=?, @ByCan=?, @UniqueID=?");
            statement.setString(1,sqlDateFormat);
            if (!VehicleNumberValue.isEmpty()) {
                statement.setInt(2, VehicleNumberIDValue);
            }
            if (!DriverNameValue.isEmpty()) {
                statement.setInt(3, DriverIDValue);
            }
            statement.setInt(4, BunkIDValue);
            statement.setString(5, RemarksValue);
            statement.setString(6, IssuedByValue);
            if (!BunkCashValue.isEmpty()) {
                statement.setString(7, BunkCashValue);
            } else {
                statement.setString(7, "0");
            }
            if (!HsdValue.isEmpty()) {
                statement.setString(8, HsdValue);
            } else {
                statement.setString(8, "0");
            }
            if (!HsdAmountValue.isEmpty()) {
                statement.setString(9, HsdAmountValue);
            } else {
                statement.setString(9, "0");
            }
            if (!HsdAmountValue.isEmpty()) {
                statement.setInt(10, 1);
            } else {
                statement.setInt(10, 0);
            }
            statement.setString(11, UniqueID); // Pass the generated uniqueID as a parameter
            rs=   statement.executeQuery();

            while (rs.next()) {
                indentNoValue=(rs.getString("")) ;
                break;
            }
            Toast.makeText(requireContext(), "data added", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        Set<Character> uniqueCharacters = new HashSet<>();

        while (uniqueCharacters.size() < length) {
            char randomChar = CHARACTERS.charAt(random.nextInt(CHARACTERS.length()));
            uniqueCharacters.add(randomChar);
        }

        for (Character ch : uniqueCharacters) {
            sb.append(ch);
        }

        return sb.toString();
    }
}
