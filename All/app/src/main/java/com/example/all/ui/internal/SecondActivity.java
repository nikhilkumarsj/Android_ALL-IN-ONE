package com.example.all.ui.internal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.all.R;
import com.example.all.UserDataSingleton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class SecondActivity extends AppCompatActivity {

    private  TextView selectedItemsTextView;

    private ProgressBar progressBar;

    private MaterialAutoCompleteTextView Unload, driverNameAutoCompleteTextView, VehicleNumAutoComplete;

    private final List<String> driverNames = new ArrayList<>();
    private final List<String> VehicleNo = new ArrayList<>();
    private final List<String> Location=new ArrayList<>();
    private final Map<String, Integer> driverIds = new HashMap<>();
    private final Map<String, Integer> vehicleIds = new HashMap<>();

    private final Map<String, Integer> GetWorklocationIds = new HashMap<>();

    private String RakeshipmentIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        driverNameAutoCompleteTextView = findViewById(R.id.DriverName);
        VehicleNumAutoComplete = findViewById(R.id.VehicleNo);
        Unload = findViewById(R.id.Unload);
        progressBar = findViewById(R.id.progressBar2);

        fetchDriverNames();


        fetchVehicleNo();

        fetchLocation();

        // Retrieve the selected items from the extras
        List<ShipmentAPIResponse> selectedResponses = getIntent().getParcelableArrayListExtra("selectedResponses");

        // Display the selected items in the TextView or handle as needed
        assert selectedResponses != null;
        displaySelectedItems(selectedResponses);
    }

    private void displaySelectedItems(List<ShipmentAPIResponse> selectedResponses) {
        int count = selectedResponses.size();
        String displayText = "Total selected Materials: " + count;
        StringBuilder builder = new StringBuilder();
        for (ShipmentAPIResponse response : selectedResponses) {
            builder.append(response.getRakeShipmentID()).append(",");

        }

        // Display the selected items in a TextView or another UI component
        selectedItemsTextView = findViewById(R.id.selectedItemsTextView);
        selectedItemsTextView.setText(displayText);
        RakeshipmentIDs=builder.toString();
        Button backButton = findViewById(R.id.Back);

        Button Submit = findViewById(R.id.submit);

        Submit.setOnClickListener(v -> validate());

        // Set an OnClickListener for the button
        backButton.setOnClickListener(v -> {
            // Create an intent to navigate back to MainActivity
            Intent intent = new Intent(SecondActivity.this, InternalFragment.class);

            // Start the MainActivity
            startActivity(intent);

            // Finish the current activity to remove it from the back stack
            finish();
        });
    }

    private boolean validateVehicle(String selectedVeh){
        TextInputLayout vehicleNoLayout = findViewById(R.id.VehicleNoLayout);
        if (selectedVeh == null || selectedVeh.isEmpty()) {
            // Display error message if no vehicle number is selected
            VehicleNumAutoComplete.setError("Please select a vehicle number");
            return false;
        } else {
            // Check if the selected vehicle number exists in the list of fetched vehicle numbers
            if (VehicleNo.contains(selectedVeh)) {
                // Clear error message if validation passes
                vehicleNoLayout.setError(null);
                return true;
            } else {
                // Display error message if the entered vehicle number is not in the list
                vehicleNoLayout.setError("Invalid vehicle number");
                return false;
            }
        }
    }

    private boolean validateDriver(String selectedDriver){
        TextInputLayout driverLayout = findViewById(R.id.DriverNameLayout);
        if (selectedDriver == null || selectedDriver.isEmpty()) {
            // Display error message if no vehicle number is selected
            VehicleNumAutoComplete.setError("Please select a vehicle number");
            return false;
        } else {
            // Check if the selected vehicle number exists in the list of fetched vehicle numbers
            if (driverNames.contains(selectedDriver)) {
                // Clear error message if validation passes
                driverLayout.setError(null);
                return true;
            } else {
                // Display error message if the entered vehicle number is not in the list
                driverLayout.setError("Invalid Driver Name");
                return false;
            }
        }
    }

    private boolean validateUnload(String selectedUnload){
        TextInputLayout unloadLayout = findViewById(R.id.UnloadLayout);
        if (selectedUnload == null || selectedUnload.isEmpty()) {
            // Display error message if no vehicle number is selected
            VehicleNumAutoComplete.setError("Please select a vehicle number");
            return false;
        } else {
            // Check if the selected vehicle number exists in the list of fetched vehicle numbers
            if (Location.contains(selectedUnload)) {
                // Clear error message if validation passes
                unloadLayout.setError(null);
                return true;
            } else {
                // Display error message if the entered vehicle number is not in the list
                unloadLayout.setError("Invalid Driver Name");
                return false;
            }
        }
    }


    private void validate()
    {

        String selectedDriverName = driverNameAutoCompleteTextView.getText().toString();
        String VehicleNum = VehicleNumAutoComplete.getText().toString();
        String Unload_place = Unload.getText().toString();

        String VehicleID = String.valueOf(vehicleIds.get(VehicleNum));
        String DriverID = String.valueOf(driverIds.get(selectedDriverName));
        String UnloadID = String.valueOf(GetWorklocationIds.get(Unload_place));

        // Validation
        boolean isUnloadValid = validateUnload(Unload_place);
        boolean isVehicleValid = validateVehicle(VehicleNum);
        boolean isDriverValid = validateDriver(selectedDriverName);

        // Proceed only if all fields are validated
        if (isUnloadValid && isVehicleValid && isDriverValid) {
            String vehicleID = String.valueOf(vehicleIds.get(VehicleNum));
            String driverID = String.valueOf(driverIds.get(selectedDriverName));
            String unloadID = String.valueOf(GetWorklocationIds.get(Unload_place));

            // Proceed to save data
            SaveData(VehicleID, DriverID, UnloadID);
        } else {
            // Handle case where validation fails
            // For example, you could display a message indicating validation errors
            // or prevent further action until all fields are correctly filled.
        }
    }

    private void SaveData(String VehicleID,String DriverID,String UnloadID){
        progressBar.setVisibility(View.VISIBLE);

        String issuedBy = UserDataSingleton.getInstance().getUsername();

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
            PostDataModel postDataModel = new PostDataModel(RakeshipmentIDs,VehicleID,DriverID,UnloadID, issuedBy);

            Call<ResponseBody> call=apiService.SendData(postDataModel);

            Log.d("API Request", "Method: " + call.request().method());
            Log.d("API Request", "URL: " + call.request().url());
            Log.d("API Request", "Issued By: " + issuedBy);
            Log.d("API Request", "Body: " + postDataModel.toString());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String successMessage = response.body().string();
                            Log.d("MainActivity", "Response: " + successMessage);
                            Toast.makeText(SecondActivity.this,successMessage,Toast.LENGTH_LONG).show();

                            driverNameAutoCompleteTextView.setText("");
                            VehicleNumAutoComplete.setText("");
                            Unload.setText("");
                            selectedItemsTextView.setText("");
                            Intent intent = new Intent(SecondActivity.this, InternalFragment.class);

                            // Start the MainActivity
                            startActivity(intent);

                            // Finish the current activity to remove it from the back stack
                            finish();
                            progressBar.setVisibility(View.GONE);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("MainActivity", "Failed to get a successful response");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.e("MainActivity", "Failed to make a POST request", t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Location List
    private void fetchLocation(){

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

            APIService apiService=retrofit.create(APIService.class);
            Call<List<Location>> call=apiService.getLocation();

            call.enqueue(new Callback<List<com.example.all.ui.internal.Location>>() {
                @Override
                public void onResponse(@NonNull Call<List<com.example.all.ui.internal.Location>> call, @NonNull Response<List<com.example.all.ui.internal.Location>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        for (Location location : response.body()) {
                            Location.add(location.getUnLoadName());

                            GetWorklocationIds.put(location.getUnLoadName(), location.getUnLoadID());
                        }

                        // Set up ArrayAdapter for the MaterialAutoCompleteTextView
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                SecondActivity.this, android.R.layout.simple_dropdown_item_1line, Location
                        );

                        // Set the adapter for the MaterialAutoCompleteTextView
                        Unload.setAdapter(adapter);
                    } else {
                        Log.e("MainActivity", "Failed to fetch driver names from API");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<com.example.all.ui.internal.Location>> call, @NonNull Throwable t) {
                    Log.e("MainActivity", "API request failed to Get Location", t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Vehicle Number List
    private void fetchVehicleNo(){

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
            Retrofit retrofit= new Retrofit.Builder()
                    .baseUrl("https://external.balajitransports.in/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            APIService apiService=retrofit.create(APIService.class);
            Call<List<Vehicle>>call=apiService.getVehicle();

            call.enqueue(new Callback<List<Vehicle>>() {
                @Override
                public void onResponse(@NonNull Call<List<Vehicle>> call, @NonNull Response<List<Vehicle>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        for (Vehicle vehicle : response.body()) {
                            VehicleNo.add(vehicle.getVehicle());

                            // Store the vehicle ID for later use in the POST request
                            vehicleIds.put(vehicle.getVehicle(), vehicle.getVehicleID());
                        }

                        // Set up ArrayAdapter for the MaterialAutoCompleteTextView
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                SecondActivity.this, android.R.layout.simple_dropdown_item_1line, VehicleNo
                        );

                        // Set the adapter for the MaterialAutoCompleteTextView
                        VehicleNumAutoComplete.setAdapter(adapter);
                    } else {
                        Log.e("MainActivity", "Failed to fetch driver names from API");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Vehicle>> call, @NonNull Throwable t) {
                    Log.e("MainActivity", "API request failed to get Vehicle", t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Driver Name List
    private void fetchDriverNames(){
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

            APIService apiService=retrofit.create(APIService.class);
            Call<List<Driver>>call=apiService.getDrivers();

            call.enqueue(new Callback<List<Driver>>() {
                @Override
                public void onResponse(@NonNull Call<List<Driver>> call, @NonNull Response<List<Driver>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        for (Driver driver : response.body()) {
                            String combinedName = driver.getName() + " - " + getLastFourDigits(driver.getDLNo());
                            driverNames.add(combinedName);

                            driverIds.put(combinedName, driver.getDriverID());
                        }

                        // Set up ArrayAdapter for the MaterialAutoCompleteTextView
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                SecondActivity.this, android.R.layout.simple_dropdown_item_1line, driverNames
                        );

                        // Set the adapter for the MaterialAutoCompleteTextView
                        driverNameAutoCompleteTextView.setAdapter(adapter);
                    } else {
                        Log.e("MainActivity", "Failed to fetch driver names from API"+response.code() + ", Message: " + response.message());

                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Driver>> call, @NonNull Throwable t) {
                    Log.e("MainActivity", "API request failed to get DriverName", t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getLastFourDigits(String input){
        if (input != null && input.length() >= 4) {
            return input.substring(input.length() - 4);
        }
        return input;
    }
}