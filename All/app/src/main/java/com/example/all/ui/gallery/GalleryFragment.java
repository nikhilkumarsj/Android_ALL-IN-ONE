package com.example.all.ui.gallery;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.all.R;
import com.example.all.UserDataSingleton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GalleryFragment extends Fragment {

    private TrustManager[] trustAllCerts; // Declared as a class member variable

    private TableLayout ResponseTable;
    private Button submitButton;
    private Calendar calendar;
    private AlertDialog alertDialog;
    private TextInputEditText Date, IndentNumber;
    private List<ShipmentAPIResponse> apiResponseList = new ArrayList<>();
    private String Accept;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        ResponseTable = view.findViewById(R.id.ResponseTab);
        submitButton = view.findViewById(R.id.submit);
        Date = view.findViewById(R.id.Date);
        IndentNumber = view.findViewById(R.id.IndentNumber);
        calendar = Calendar.getInstance();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetTableData();
            }
        });

        TextInputEditText dateEditText = view.findViewById(R.id.Date);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });

        return view;

    }

    private void GetTableData(){
        // Initialize trustAllCerts array
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        String indentNoValue = IndentNumber.getText().toString();
        String dateValue = Date.getText().toString();

        String bunked = String.valueOf(UserDataSingleton.getInstance().getBunkID());

        Log.d("MainActivity","BunkID: "+bunked);

        Log.d("MainActivity", "IndentNumber Value: " + indentNoValue);
        Log.d("MainActivity", "Date Value: " + dateValue);

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://external.balajitransports.in/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            APIService apiService = retrofit.create(APIService.class);

            ShipmentPostModel shipmentPostModel = new ShipmentPostModel(dateValue, indentNoValue, bunked);

            Call<List<ShipmentAPIResponse>> call = apiService.postData(shipmentPostModel);

            Log.d("API Request", "Method: " + call.request().method());
            Log.d("API Request", "URL: " + call.request().url());
            Log.d("API Request", "Headers: " + call.request().headers());
            Log.d("API Request", "Body: " + shipmentPostModel.toString());

            call.enqueue(new Callback<List<ShipmentAPIResponse>>() {
                @Override
                public void onResponse(Call<List<ShipmentAPIResponse>> call, Response<List<ShipmentAPIResponse>> response) {
                    Log.d("API Response", "Raw Response: " + response.raw().toString());

                    if (response.isSuccessful()) {
                        List<ShipmentAPIResponse> apiResponseList = response.body();
                        displayResponseInTable(apiResponseList);
                    } else {

                        try {
                            Log.e("API Error", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<ShipmentAPIResponse>> call, Throwable t) {
                    Log.e("API Failure", t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void displayResponseInTable(List<ShipmentAPIResponse> apiResponseList){
        this.apiResponseList = apiResponseList;

        ResponseTable.removeAllViews();

        TableRow headerRow = new TableRow(requireContext());
        TableRow.LayoutParams buttonParams = new TableRow.LayoutParams();



        addHeadingCell(headerRow," ", Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"IndentID", Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"IndentNo",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"Inddate",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"VehicleNo",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"DriverName",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"IssuedBy",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"HSDLeters",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"HSDAmount",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"Remarks",Color.parseColor("#FFD700"));

        ResponseTable.addView(headerRow);

        for (ShipmentAPIResponse response : apiResponseList) {
            TableRow dataRow = new TableRow(requireContext());

// Replace the checkbox with a button
            Button actionButton = new Button(requireContext());
            actionButton.setText("Select");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Show popup screen with Accept and Reject options
                    showActionPopup(response);
                }
            });

// Add button to the current dataRow
            dataRow.addView(actionButton);
            addTableCell(dataRow, response.getIndentID());
            addTableCell(dataRow,response.getIndentNo());
            addTableCell(dataRow,response.getInddate());
            addTableCell(dataRow,response.getVehicleNo());
            addTableCell(dataRow,response.getDriverName());
            addTableCell(dataRow,response.getIssuedBy());
            addTableCell(dataRow,response.getHSDLeters());
            addTableCell(dataRow,response.getHSDAmount());
            addTableCell(dataRow,response.getRemarks());

            // Add more data fields as needed
            ResponseTable.addView(dataRow);

        }
    }

    private void addTableCell(TableRow tableRow, String text) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        tableRow.addView(textView);
    }

    private void addHeadingCell(TableRow tableRow, String text, int backgroundColor) {
        TextView headingTextView = new TextView(requireContext());
        headingTextView.setText(text);
        headingTextView.setPadding(16, 16, 16, 16);
        headingTextView.setTextSize(18); // Set the text size as needed for the heading
        headingTextView.setBackgroundColor(backgroundColor);
        tableRow.addView(headingTextView);
    }

    public void showDatePicker(View view) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, selectedMonth);
                        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                        updateDateEditText();
                    }
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }


    // Helper method to update the date TextInputEditText with the selected date
    private void updateDateEditText() {
        String myFormat = "yyyy-MM-dd"; // Choose the desired date format
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        Date.setText(sdf.format(calendar.getTime()));
    }
    private void showActionPopup(ShipmentAPIResponse selectedResponse) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Select Action");

        View view = getLayoutInflater().inflate(R.layout.popup_layout, null);
        Button acceptButton = view.findViewById(R.id.acceptButton);
        Button rejectButton = view.findViewById(R.id.rejectButton);


// Assuming you have TextViews in popup_layout for displaying data
        TextView indentIdTextView = view.findViewById(R.id.indentIdTextView);
        TextView indentNoTextView = view.findViewById(R.id.indentNoTextView);
        TextView inddateTextView = view.findViewById(R.id.inddateTextView);
        TextView vehicleNoTextView = view.findViewById(R.id.vehicleNoTextView);
        TextView driverNameTextView = view.findViewById(R.id.driverNameTextView);
        TextView issuedByTextView = view.findViewById(R.id.issuedByTextView);
        TextView hsdLetersTextView = view.findViewById(R.id.hsdLetersTextView);
        TextView hsdAmountTextView = view.findViewById(R.id.hsdAmountTextView);
        TextView remarksTextView = view.findViewById(R.id.remarksTextView);

// Set the text for each TextView with the corresponding data
        indentIdTextView.setText("Indent ID: " + selectedResponse.getIndentID());
        indentNoTextView.setText("Indent No: " + selectedResponse.getIndentNo());
        inddateTextView.setText("Inddate: " + selectedResponse.getInddate());
        vehicleNoTextView.setText("VehicleNo: " + selectedResponse.getVehicleNo());
        driverNameTextView.setText("DriverName: " + selectedResponse.getDriverName());
        issuedByTextView.setText("IssuedBy: " + selectedResponse.getIssuedBy());
        hsdLetersTextView.setText("HSDLeters: " + selectedResponse.getHSDLeters());
        hsdAmountTextView.setText("HSDAmount: " + selectedResponse.getHSDAmount());
        remarksTextView.setText("Remarks: " + selectedResponse.getRemarks());

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog("Accept", selectedResponse);
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog("Reject", selectedResponse);
            }
        });

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showConfirmationDialog(String action, ShipmentAPIResponse selectedResponse) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Confirmation");

        String message = "Do you want to " + action.toLowerCase() + "?";
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked OK, perform the action
                if ("Accept".equals(action)) {
                    Accept = "1";
                } else if ("Reject".equals(action)) {
                    Accept = "0";
                }

                SaveData(selectedResponse);
                alertDialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked Cancel, dismiss the dialog
                alertDialog.dismiss();
            }
        });
        AlertDialog confirmationDialog = builder.create();
        confirmationDialog.show();
    }


    private void SaveData(ShipmentAPIResponse selectedResponse){
        // Default value for Accept (0 for Reject)

        String IndentID = selectedResponse.getIndentID();


        Log.d("MainActivity", "Accept Value: " + Accept);
        Log.d("MainActivity", "IndentID Value: " + IndentID);

        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();


            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl("https://external.balajitransports.in/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            APIService apiService =retrofit.create(APIService.class);

            PostDataModel postDataModel = new PostDataModel(IndentID,Accept);

            Call<ResponseBody> call = apiService.SendData(postDataModel);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if ( response.code() >= 200 && response.code() <300 ) {
                        Log.d("MainActivity", "Success: ");
                    } else {
                        Log.e("MainActivity", "Failed to get a successful response");
                        int code = response.code();
                        Log.e("Statuscode ", String.valueOf(code));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("MainActivity", "Failed to make a POST request", t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
