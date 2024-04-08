package com.example.all.ui.internal;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.all.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

public class InternalFragment extends Fragment {

    private TableLayout ResponseTable;
    private TextView selectValue;
    private Button submitButton;
    private ProgressBar progressBar;

    private TextInputEditText InvoiceNo, Material;

    private final List<String> selectedItems = new ArrayList<>();
    private List<ShipmentAPIResponse> apiResponseList = new ArrayList<>();
    ArrayList<ShipmentAPIResponse> selectedResponses = new ArrayList<>();

    ArrayList<ShipmentAPIResponse> allResponses = new ArrayList<>();

    ArrayList<String> selectedRakeShipmentIDs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_internal, container, false);

        selectValue = view.findViewById(R.id.selectedItemsTextView);
        ResponseTable = view.findViewById(R.id.ResponseTab);
        Button searchButton = view.findViewById(R.id.search);
        InvoiceNo = view.findViewById(R.id.InvoiceNo);
        Material = view.findViewById(R.id.Material);
        submitButton = view.findViewById(R.id.submit); // Update the reference to use the class member variable
        Button Add = view.findViewById(R.id.Add);
        Button Select = view.findViewById(R.id.Select);
        Button Clear = view.findViewById(R.id.Clear);
        progressBar = view.findViewById(R.id.progressBar); // Initialize progressBar here

        searchButton.setOnClickListener(v -> GetTableData());

        Add.setOnClickListener(this::onAddButtonClick);

        Select.setOnClickListener(v -> selectAllCheckboxes());

        Clear.setOnClickListener(v -> clearAllCheckboxes());

        submitButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SecondActivity.class);
            intent.putParcelableArrayListExtra("selectedResponses", selectedResponses);
            startActivity(intent);
            selectedItems.clear();
        });

        return view;
    }

    public void onAddButtonClick(View view) {
        allResponses.addAll(apiResponseList);

        Set<String> uniqueRakeShipmentIDs = new HashSet<>(selectedRakeShipmentIDs);

        // Use a Map to store selected items with their information
        Map<String, SelectedItemInfo> selectedItemsMap = new HashMap<>();


        for (ShipmentAPIResponse response : allResponses) {
            if (selectedItems.contains(response.getRakeShipmentID())) {
                if (!selectedResponses.contains(response) && allResponses.contains(response)) {
                    Log.d("Selected Row", "RakeShipmentID: " + response.getRakeShipmentID() +
                            ", WagonNo: " + response.getWagonNo() +
                            ", Material: " + response.getMaterial() +
                            ", ProductName: " + response.getProductName() +
                            ", NetWt: " + response.getNetWt() +
                            ", InvoiceNo: " + response.getInvoiceNo() +
                            ", PartyName: " + response.getPartyName());

                    SelectedItemInfo selectedItemInfo = new SelectedItemInfo(
                            response.getRakeShipmentID(),
                            response.getMaterial(),
                            response.getInvoiceNo()
                    );

                    selectedItemsMap.put(response.getRakeShipmentID(), selectedItemInfo);


                    if (uniqueRakeShipmentIDs.add(response.getRakeShipmentID())) {
                        selectedResponses.add(response);
                    }
                }
            }
        }
        // Update selectedRakeShipmentIDs with the contents of the Set
        selectedRakeShipmentIDs.clear();
        selectedRakeShipmentIDs.addAll(uniqueRakeShipmentIDs);

        // Display information using selectedItemsMap
        StringBuilder displayText = new StringBuilder();
        for (String rakeShipmentID : selectedRakeShipmentIDs) {
            SelectedItemInfo selectedItemInfo = selectedItemsMap.get(rakeShipmentID);
            if (selectedItemInfo != null) {
                displayText
                        .append("\n")
                        .append("RakeShipmentID: ").append(selectedItemInfo.getRakeShipmentID())

                        .append(", Material: ").append(selectedItemInfo.getMaterial())

                        .append(", InvoiceNo: ").append(selectedItemInfo.getInvoiceNo())
                        .append("\n");
            }
        }

        selectValue.setText(displayText.toString());
    }

    private void GetTableData(){
        String invoiceNoValue = InvoiceNo.getText().toString();
        String materialValue    = Material.getText().toString();

        Log.d("MainActivity", "Invoice Value: " + invoiceNoValue);
        Log.d("MainActivity", "Material Value: " + materialValue);

        progressBar.setVisibility(View.VISIBLE);


        try {

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .connectTimeout(120, TimeUnit.SECONDS) // Set connection timeout to 60 seconds
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://external.balajitransports.in/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            APIService apiService= retrofit.create(APIService.class);

            ShipmentPostModel shipmentPostModel=new ShipmentPostModel(materialValue,invoiceNoValue);

            Call<List<ShipmentAPIResponse>> call=apiService.postData(shipmentPostModel);

            Log.d("API Request", "Method: " + call.request().method());
            Log.d("API Request", "URL: " + call.request().url());
            Log.d("API Request", "Headers: " + call.request().headers());
            Log.d("API Request", "Body: " + shipmentPostModel.toString());

            call.enqueue(new Callback<List<ShipmentAPIResponse>>() {
                @Override
                public void onResponse(@NonNull Call<List<ShipmentAPIResponse>> call, @NonNull Response<List<ShipmentAPIResponse>> response) {
                    Log.d("API Response", "Raw Response: " + response.raw().toString());

                    if (response.isSuccessful()) {
                        List<ShipmentAPIResponse> apiResponseList = response.body();
                        assert apiResponseList != null;
                        displayResponseInTable(apiResponseList);
                        progressBar.setVisibility(View.GONE);

                    } else {

                        try {
                            progressBar.setVisibility(View.GONE);

                            assert response.errorBody() != null;
                            Log.e("API Error", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<ShipmentAPIResponse>> call, @NonNull Throwable t) {
                    Log.e("API Failure", Objects.requireNonNull(t.getMessage()));
                    Toast.makeText(requireActivity(), "Network error", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                }
            });

            // Continue with your retrofit setup...
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void displayResponseInTable(List<ShipmentAPIResponse> apiResponseList){
        this.apiResponseList = apiResponseList;

        ResponseTable.removeAllViews();

        TableRow headerRow=new TableRow(getContext());


        addHeadingCell(headerRow,"Select", Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"RakeShipmentID", Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"WagonNo",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"Material",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"ProductName",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"NetWt",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"InvoiceNo",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"PartyName",Color.parseColor("#FFD700"));
        addHeadingCell(headerRow,"Rake",Color.parseColor("#FFD700"));


        ResponseTable.addView(headerRow);

        for (ShipmentAPIResponse response : apiResponseList) {
            TableRow dataRow = new TableRow(getContext());

            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Handle the checkbox state change, add/remove the selected item to/from the list
                if (isChecked) {
                    selectedItems.add(response.getRakeShipmentID());
                    selectedItems.add(response.getInvoiceNo());
                    selectedItems.add(response.getMaterial());
                } else {
                    selectedItems.remove(response.getRakeShipmentID());
                    selectedItems.add(response.getInvoiceNo());
                    selectedItems.add(response.getMaterial());
                }
            });

            // Add checkbox to the current dataRow
            dataRow.addView(checkBox);

            addTableCell(dataRow, response.getRakeShipmentID());
            addTableCell(dataRow, response.getWagonNo());
            addTableCell(dataRow,response.getMaterial());
            addTableCell(dataRow,response.getProductName());
            addTableCell(dataRow,response.getNetWt());
            addTableCell(dataRow,response.getInvoiceNo());
            addTableCell(dataRow,response.getPartyName());
            addTableCell(dataRow,response.getRake());



            // Add more data fields as needed
            ResponseTable.addView(dataRow);

        }
    }

    private void addTableCell(TableRow tableRow, String text) {
        TextView textView = new TextView(getContext());
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


    private void selectAllCheckboxes() {
        for (int i = 0; i < ResponseTable.getChildCount(); i++) {
            View view = ResponseTable.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                for (int j = 0; j < row.getChildCount(); j++) {
                    View child = row.getChildAt(j);
                    if (child instanceof CheckBox) {
                        // Set the checkbox state to checked
                        ((CheckBox) child).setChecked(true);
                    }
                }
            }
        }
    }

    private void clearAllCheckboxes() {
        for (int i = 0; i < ResponseTable.getChildCount(); i++) {
            View view = ResponseTable.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                for (int j = 0; j < row.getChildCount(); j++) {
                    View child = row.getChildAt(j);
                    if (child instanceof CheckBox) {
                        // Set the checkbox state to unchecked
                        ((CheckBox) child).setChecked(false);
                    }
                }
            }
        }
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

}
