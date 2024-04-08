package com.example.all;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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

public class LoginActivity extends AppCompatActivity {

    TextInputEditText username, password;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.edUsername);
        password = findViewById(R.id.edPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {
                    Log.d("MainActivity", "Username / Password Required");
                } else {
                    // proceed to login
                    login();
                }
            }
        });
    }


    public void login() {

        String enteredUsername = username.getText().toString();
        String enteredPassword = password.getText().toString();

        Log.d("MainActivity", "Username Value: " + enteredUsername);
        Log.d("MainActivity", "Password Value: " + enteredPassword);

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

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://external.balajitransports.in/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            APIService userService = retrofit.create(APIService.class);
            LoginRequest loginRequest = new LoginRequest(enteredUsername,enteredPassword);
            Call<ResponseBody> call = userService.PassData(loginRequest);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String successMessage = response.body().string();
                            Log.d("MainActivity", "Response: " + successMessage);

                            // Parse the JSON response
                            JSONArray jsonArray = new JSONArray(successMessage);
                            if (jsonArray.length() > 0) {
                                JSONObject userObject = jsonArray.getJSONObject(0);
                                String apiUserId= userObject.getString("UserId");
                                String apiName = userObject.getString("Name");
                                String apiRoleId = userObject.getString("RoleId");
                                String apiPhone_No = userObject.getString("Phone_No");
                                String apiLocation = userObject.getString("Location");
                                String apiLocationId = userObject.getString("LocationId");
                                String apiBunkId = userObject.getString("BunkID");

                                Log.d("MainActivity", "User ID Value: " + apiUserId);
                                Log.d("MainActivity", "User Name Value: " + apiName);
                                Log.d("MainActivity", "Role ID Value: " + apiRoleId);
                                Log.d("MainActivity", "Phone Number Value: " + apiPhone_No);
                                Log.d("MainActivity", "Location Value: " + apiLocation);
                                Log.d("MainActivity", "Location ID Value: " + apiLocationId);
                                Log.d("MainActivity", "Bunk ID  Value: " + apiBunkId);

                                if (enteredUsername.equals(apiName)) {
                                    UserDataSingleton.getInstance().setUserId(apiUserId);
                                    UserDataSingleton.getInstance().setUsername(apiName);
                                    UserDataSingleton.getInstance().setRoleId(apiRoleId);
                                    UserDataSingleton.getInstance().setPhoneNo(apiPhone_No);
                                    UserDataSingleton.getInstance().setLocation(apiLocation);
                                    UserDataSingleton.getInstance().setLocationId(apiLocationId);
                                    UserDataSingleton.getInstance().setBunkID(apiBunkId);

                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                    gotoSecondActivity();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Username mismatch", Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_LONG).show();

                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.e("MainActivity", "Failed to get a successful response");
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
     public void gotoSecondActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
     }

}



//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        try {
//                            String successMessage = response.body().string();
//                            Log.d("MainActivity", "Response: " + successMessage);
//                            Toast.makeText(LoginActivity.this,"success",Toast.LENGTH_LONG).show();
//                            gotoSecondActivity();
//
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        Log.e("MainActivity", "Failed to get a successful response");
//                    }
//                }
