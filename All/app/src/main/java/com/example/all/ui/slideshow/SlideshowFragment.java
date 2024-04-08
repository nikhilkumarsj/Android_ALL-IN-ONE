package com.example.all.ui.slideshow;

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

public class SlideshowFragment extends Fragment {

    private Calendar calendar;
    private TableLayout responseTable;
    private Button submitButton;
    private TextView fromDateTextView;
    private TextView toDateTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_slideshow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendar = Calendar.getInstance();
        responseTable = view.findViewById(R.id.ResponseTab);
        submitButton = view.findViewById(R.id.Submit);
        fromDateTextView = view.findViewById(R.id.date);
        toDateTextView = view.findViewById(R.id.toDate);

        fromDateTextView.setOnClickListener(v -> showFromDatePicker());
        toDateTextView.setOnClickListener(v -> showToDatePicker());

        submitButton.setOnClickListener(v -> {
            displayInputValues();
            GetTableData();
            // Call your method for getting table data here
        });
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

    private void displayInputValues() {
        String fromDateValue = fromDateTextView.getText().toString();
        String toDateValue = toDateTextView.getText().toString();
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

    private void updateFromDateEditText(Calendar fromCalendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        fromDateTextView.setText(sdf.format(fromCalendar.getTime()));
    }

    private void updateToDateEditText(Calendar toCalendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        toDateTextView.setText(sdf.format(toCalendar.getTime()));
    }

    private void GetTableData(){
        String dateValue= fromDateTextView.getText().toString();
        String toDateValue = toDateTextView.getText().toString();

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
            GCPostModel GcPostModel = new GCPostModel(dateValue,toDateValue);

            Call<List<GCAPIResponse>> call=apiService.postGCData(GcPostModel);

            call.enqueue(new Callback<List<GCAPIResponse>>() {
                @Override
                public void onResponse(Call<List<GCAPIResponse>> call, Response<List<GCAPIResponse>> response) {
                    if (response.isSuccessful()) {
                        List<GCAPIResponse> apiResponseList = response.body();
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
                public void onFailure(Call<List<GCAPIResponse>> call, Throwable t) {
                    Log.e("API Failure", t.getMessage());
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayResponseInTable(List<GCAPIResponse> apiResponseList){

        responseTable.removeAllViews();

        TableRow headerRow=new TableRow(requireContext());
        addHeadingCell(headerRow,"RakeShipmentID", Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"Material",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"Customer",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"NetWt",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"InvoiceNo",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"Rake",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"ShipmentMonth",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"From",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"To",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"ShipmentDate",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"TransportName",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"ProductName",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"WagonNo",Color.parseColor("#FFD700"));


        responseTable.addView(headerRow);

        for (GCAPIResponse response : apiResponseList) {
            TableRow dataRow = new TableRow(requireContext());
            addTableCell(dataRow, response.getRakeShipmentID());
            addTableCell(dataRow, response.getMaterial());
            addTableCell(dataRow,response.getCustomer());
            addTableCell(dataRow,response.getNetWt());
            addTableCell(dataRow,response.getInvoiceNo());
            addTableCell(dataRow,response.getRake());
            addTableCell(dataRow,response.getShipmentMonth());
            addTableCell(dataRow,response.getFrom());
            addTableCell(dataRow,response.getTo());
            addTableCell(dataRow,response.getShipmentDate());
            addTableCell(dataRow,response.getTransportName());
            addTableCell(dataRow,response.getProductName());
            addTableCell(dataRow,response.getWagonNo());


            // Add more data fields as needed
            responseTable.addView(dataRow);
        }
    }

    private void addHeadingCell(TableRow tableRow, String text, int backgroundColor) {
        TextView headingTextView = new TextView(getContext());
        headingTextView.setText(text);
        headingTextView.setPadding(16, 16, 16, 16);
        headingTextView.setTextSize(18); // Set the text size as needed for the heading
        headingTextView.setBackgroundColor(backgroundColor);
        tableRow.addView(headingTextView);
    }

    private void addTableCell(TableRow tableRow, String text) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        tableRow.addView(textView);
    }

    // Helper method to update the date TextInputEditText with the selected date


    // Other methods for handling UI interactions and logic can be defined here
}




//
//        import android.app.DatePickerDialog;
//        import android.app.TimePickerDialog;
//        import android.graphics.Color;
//        import android.os.Bundle;
//        import android.util.Log;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.widget.Button;
//        import android.widget.DatePicker;
//        import android.widget.TableLayout;
//        import android.widget.TableRow;
//        import android.widget.TextView;
//        import android.widget.TimePicker;
//        import android.widget.Toast;
//
//        import androidx.annotation.NonNull;
//        import androidx.fragment.app.Fragment;
//
//        import com.example.all.APIService;
//        import com.example.all.R;
//        import com.google.android.material.textfield.TextInputEditText;
//
//        import java.io.IOException;
//        import java.security.SecureRandom;
//        import java.security.cert.CertificateException;
//        import java.security.cert.X509Certificate;
//        import java.text.SimpleDateFormat;
//        import java.util.Calendar;
//        import java.util.List;
//        import java.util.Locale;
//
//        import javax.net.ssl.SSLContext;
//        import javax.net.ssl.SSLSocketFactory;
//        import javax.net.ssl.TrustManager;
//        import javax.net.ssl.X509TrustManager;
//
//        import okhttp3.OkHttpClient;
//        import retrofit2.Call;
//        import retrofit2.Callback;
//        import retrofit2.Response;
//        import retrofit2.Retrofit;
//        import retrofit2.converter.gson.GsonConverterFactory;
//
//public class SlideshowFragment extends Fragment {
//
//    private Calendar calendar;
//    private TableLayout ResponseTable;
//    private TextInputEditText dateEditText;
//    private TextInputEditText toDateEditText;
//    private Button submitButton;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);
//        return view;
//    }
//
//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        calendar = Calendar.getInstance();
//        ResponseTable = view.findViewById(R.id.ResponseTab);
//        dateEditText = view.findViewById(R.id.date);
//        toDateEditText = view.findViewById(R.id.toDate);
//        submitButton = view.findViewById(R.id.submit);
//
//        submitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                displayInputValues();
//                GetTableData();
//            }
//        });
//        dateEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle date field click
//                showFromDatePicker();
//            }
//        });
//
//        toDateEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle toDate field click
//                showToDatePicker();
//            }
//        });
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//    }
//
//    public  void displayInputValues() {
//        String dateValue = dateEditText.getText().toString();
//        String todateValue = toDateEditText.getText().toString();
//
//    }
//
//    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//        @Override
//        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//        }
//
//        @Override
//        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//        }
//
//        @Override
//        public X509Certificate[] getAcceptedIssuers() {
//            return new X509Certificate[]{};
//        }
//    }};
//
//    private void GetTableData(){
//        String dateValue= dateEditText.getText().toString();
//        String toDateValue = toDateEditText.getText().toString();
//
//        Log.d("MainActivity", "Date Value: " + dateValue);
//        Log.d("MainActivity", "ToDate Value: " + toDateValue);
//
//        try {
//            SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new SecureRandom());
//            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//            OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
//                    .hostnameVerifier((hostname, session) -> true)
//                    .build();
//
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl("https://external.balajitransports.in/api/")
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(okHttpClient)
//                    .build();
//
//            APIService apiService= retrofit.create(APIService.class);
//            GCPostModel GcPostModel = new GCPostModel(dateValue,toDateValue);
//
//            Call<List<GCAPIResponse>> call=apiService.postGCData(GcPostModel);
//
//            call.enqueue(new Callback<List<GCAPIResponse>>() {
//                @Override
//                public void onResponse(Call<List<GCAPIResponse>> call, Response<List<GCAPIResponse>> response) {
//                    if (response.isSuccessful()) {
//                        List<GCAPIResponse> apiResponseList = response.body();
//                        displayResponseInTable(apiResponseList);
//                    } else {
//
//                        try {
//                            Log.e("API Error", response.errorBody().string());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<List<GCAPIResponse>> call, Throwable t) {
//                    Log.e("API Failure", t.getMessage());
//                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
//
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void displayResponseInTable(List<GCAPIResponse> apiResponseList){
//
//        ResponseTable.removeAllViews();
//
//        TableRow headerRow=new TableRow(requireContext());
//        addHeadingCell(headerRow,"RakeShipmentID", Color.parseColor("#FFD700"));
//        addHeadingCell(headerRow,"Material",Color.parseColor("#FFD700"));
//        addHeadingCell(headerRow,"Customer",Color.parseColor("#FFD700"));
//        addHeadingCell(headerRow,"NetWt",Color.parseColor("#FFD700"));
//        addHeadingCell(headerRow,"InvoiceNo",Color.parseColor("#FFD700"));
//        addHeadingCell(headerRow,"Rake",Color.parseColor("#FFD700"));
//        addHeadingCell(headerRow,"ShipmentMonth",Color.parseColor("#FFD700"));
//        addHeadingCell(headerRow,"From",Color.parseColor("#FFD700"));
//        addHeadingCell(headerRow,"To",Color.parseColor("#FFD700"));
//        addHeadingCell(headerRow,"ShipmentDate",Color.parseColor("#FFD700"));
//        addHeadingCell(headerRow,"TransportName",Color.parseColor("#FFD700"));
//        addHeadingCell(headerRow,"ProductName",Color.parseColor("#FFD700"));
//        addHeadingCell(headerRow,"WagonNo",Color.parseColor("#FFD700"));
//
//
//        ResponseTable.addView(headerRow);
//
//        for (GCAPIResponse response : apiResponseList) {
//            TableRow dataRow = new TableRow(requireContext());
//            addTableCell(dataRow, response.getRakeShipmentID());
//            addTableCell(dataRow, response.getMaterial());
//            addTableCell(dataRow,response.getCustomer());
//            addTableCell(dataRow,response.getNetWt());
//            addTableCell(dataRow,response.getInvoiceNo());
//            addTableCell(dataRow,response.getRake());
//            addTableCell(dataRow,response.getShipmentMonth());
//            addTableCell(dataRow,response.getFrom());
//            addTableCell(dataRow,response.getTo());
//            addTableCell(dataRow,response.getShipmentDate());
//            addTableCell(dataRow,response.getTransportName());
//            addTableCell(dataRow,response.getProductName());
//            addTableCell(dataRow,response.getWagonNo());
//
//
//            // Add more data fields as needed
//            ResponseTable.addView(dataRow);
//        }
//    }
//
//    private void addHeadingCell(TableRow tableRow, String text, int backgroundColor) {
//        TextView headingTextView = new TextView(getContext());
//        headingTextView.setText(text);
//        headingTextView.setPadding(16, 16, 16, 16);
//        headingTextView.setTextSize(18); // Set the text size as needed for the heading
//        headingTextView.setBackgroundColor(backgroundColor);
//        tableRow.addView(headingTextView);
//    }
//
//    private void addTableCell(TableRow tableRow, String text) {
//        TextView textView = new TextView(requireContext());
//        textView.setText(text);
//        textView.setPadding(16, 16, 16, 16);
//        tableRow.addView(textView);
//    }
//    public void showFromDatePicker() {
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//                requireContext(),
//                new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
//                        calendar.set(Calendar.YEAR, selectedYear);
//                        calendar.set(Calendar.MONTH, selectedMonth);
//                        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
//
//                        showFromTimePicker();
//                    }
//                },
//                year, month, dayOfMonth);
//
//        datePickerDialog.show();
//    }
//
//    private void showFromTimePicker() {
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//
//        TimePickerDialog timePickerDialog = new TimePickerDialog(
//                requireContext(),
//                (TimePicker view, int selectedHour, int selectedMinute) -> {
//                    Calendar fromCalendar = (Calendar) calendar.clone(); // Clone the Calendar
//                    fromCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
//                    fromCalendar.set(Calendar.MINUTE, selectedMinute);
//
//                    updateFromDateEditText(fromCalendar);
//                },
//                hour, minute, false);
//
//        timePickerDialog.show();
//    }
//    public void showToDatePicker() {
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//                requireContext(),
//                new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
//                        calendar.set(Calendar.YEAR, selectedYear);
//                        calendar.set(Calendar.MONTH, selectedMonth);
//                        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
//
//                        showToTimePicker();
//                    }
//                },
//                year, month, dayOfMonth);
//
//        datePickerDialog.show();
//    }
//
//
//    private void showToTimePicker() {
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//
//        TimePickerDialog timePickerDialog = new TimePickerDialog(
//                requireContext(),
//                (TimePicker view, int selectedHour, int selectedMinute) -> {
//                    Calendar toCalendar = (Calendar) calendar.clone(); // Clone the Calendar
//                    toCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
//                    toCalendar.set(Calendar.MINUTE, selectedMinute);
//
//                    updateToDateEditText(toCalendar);
//                },
//                hour, minute, false);
//
//        timePickerDialog.show();
//    }
//
//
//    // Helper method to update the date TextInputEditText with the selected date
//    private void updateFromDateEditText(Calendar fromCalendar) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//        dateEditText.setText(sdf.format(fromCalendar.getTime()));
//    }
//
//    private void updateToDateEditText(Calendar toCalendar) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//        toDateEditText.setText(sdf.format(toCalendar.getTime()));
//    }
//
//}