package com.example.all.ui.home;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.all.APIService;
import com.example.all.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private Calendar calendar;

    private TableLayout ResponseTable;
    private TextInputEditText dateEditText;
    private TextInputEditText toDateEditText;
    private Button submitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendar = Calendar.getInstance();
        ResponseTable = view.findViewById(R.id.ResponseTab);
        dateEditText = view.findViewById(R.id.date);
        toDateEditText = view.findViewById(R.id.toDate);
        submitButton = view.findViewById(R.id.submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayShipmentInputValues();
                GetTableData();
            }
        });
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle date field click
                showFromDatePicker();
            }
        });

        toDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle toDate field click
                showToDatePicker();
            }
        });
    }

    // Rest of the methods remain the same...

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public  void displayShipmentInputValues() {
        String dateValue = dateEditText.getText().toString();
        String todateValue = toDateEditText.getText().toString();

    }

    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }};


    private void GetTableData(){
        String dateValue= dateEditText.getText().toString();
        String toDateValue = toDateEditText.getText().toString();

        Log.d("MainActivity", "Date Value: " + dateValue);
        Log.d("MainActivity", "ToDate Value: " + toDateValue);

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
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

            APIService apiService= retrofit.create(APIService.class);

            ShipmentPostModel shipmentPostModel=new ShipmentPostModel(dateValue,toDateValue);

            Call<List<ShipmentAPIResponse>> call=apiService.postData(shipmentPostModel);

            call.enqueue(new Callback<List<ShipmentAPIResponse>>() {
                @Override
                public void onResponse(Call<List<ShipmentAPIResponse>> call, Response<List<ShipmentAPIResponse>> response) {
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
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();

                }
            });

            // Continue with your retrofit setup...
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void displayResponseInTable(List<ShipmentAPIResponse> apiResponseList){

        ResponseTable.removeAllViews();

        TableRow headerRow=new TableRow(requireContext());
        addHeadingCell(headerRow,"ShipmentMonth", Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"ShipmentNo",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"GcNo",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"ShipmentDate",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"VehNo",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"Qty",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"Product",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"PartyName",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"From",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"To",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"TransportName",Color.parseColor("#FFD700"));



        ResponseTable.addView(headerRow);

        for (ShipmentAPIResponse response : apiResponseList) {
            TableRow dataRow = new TableRow(requireContext());
            addTableCell(dataRow, response.getShipmentMonth());
            addTableCell(dataRow, response.getShipmentNo());
            addTableCell(dataRow,response.getGcNo());
            addTableCell(dataRow,response.getShipmentDate());
            addTableCell(dataRow,response.getVehNo());
            addTableCell(dataRow,response.getQty());
            addTableCell(dataRow,response.getProduct());
            addTableCell(dataRow,response.getPartyName());
            addTableCell(dataRow,response.getFrom());
            addTableCell(dataRow,response.getTo());
            addTableCell(dataRow,response.getTransportName());

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
        TextView headingTextView = new TextView(getContext());
        headingTextView.setText(text);
        headingTextView.setPadding(16, 16, 16, 16);
        headingTextView.setTextSize(18); // Set the text size as needed for the heading
        headingTextView.setBackgroundColor(backgroundColor);
        tableRow.addView(headingTextView);
    }
    public void showFromDatePicker() {
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

                        showFromTimePicker();
                    }
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void showFromTimePicker() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (TimePicker view, int selectedHour, int selectedMinute) -> {
                    Calendar fromCalendar = (Calendar) calendar.clone(); // Clone the Calendar
                    fromCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    fromCalendar.set(Calendar.MINUTE, selectedMinute);

                    updateFromDateEditText(fromCalendar);
                },
                hour, minute, false);

        timePickerDialog.show();
    }

    public void showToDatePicker() {
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

                        showToTimePicker();
                    }
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }


    private void showToTimePicker() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (TimePicker view, int selectedHour, int selectedMinute) -> {
                    Calendar toCalendar = (Calendar) calendar.clone(); // Clone the Calendar
                    toCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    toCalendar.set(Calendar.MINUTE, selectedMinute);

                    updateToDateEditText(toCalendar);
                },
                hour, minute, false);

        timePickerDialog.show();
    }

    // Helper method to update the date TextInputEditText with the selected date
    private void updateFromDateEditText(Calendar fromCalendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        dateEditText                                .setText(sdf.format(fromCalendar.getTime()));
    }

    private void updateToDateEditText(Calendar toCalendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        toDateEditText.setText(sdf.format(toCalendar.getTime()));
    }


}