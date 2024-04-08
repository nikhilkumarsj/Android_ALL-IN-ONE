package com.example.all.ui.generator;


import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.camera.core.ImageCapture;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.all.R;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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


public class GeneratorFragment extends Fragment {

    // Declare lists and maps
    private final List<String> driverNames = new ArrayList<>();
    private final List<String> VehicleNo = new ArrayList<>();
    private final List<String> Location=new ArrayList<>();

    // Dynamically created field lists
    private final List<TextInputEditText> coilIdEditTextList = new ArrayList<>();
    private final List<TextInputEditText> tonnageEditTextList = new ArrayList<>();
    private final List<TextInputEditText> remarksEditTextList = new ArrayList<>();
    private final List<String> coilIdValues = new ArrayList<>();
    private final List<String> tonnageValues = new ArrayList<>();


    private final Map<String, Integer> driverIds = new HashMap<>();
    private final Map<String, Integer> vehicleIds = new HashMap<>();

    private final Map<String, Integer> locationFromIds = new HashMap<>();
    private final Map<String, Integer> locationToIds = new HashMap<>();

    // Declare UI components
    private TextInputEditText date, coilID, tonnage, pageRefNo, remarks;
    private MaterialAutoCompleteTextView from, to, driverNameAutoCompleteTextView, VehicleNumAutoComplete;
    private Calendar calendar;
    private LinearLayout containerLayout;
    private Button submitButton;

    // Camera variables
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private Button takePictureButton;
    private TextureView textureView;
    private ImageCapture imageCapture;
    private TextureView.SurfaceTextureListener textureListener;

    // Other variables and constants
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    private static final String TAG = "AndroidCameraApi";
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generator, container, false);

        // Initialize UI components
        driverNameAutoCompleteTextView = view.findViewById(R.id.DriverName);
        VehicleNumAutoComplete = view.findViewById(R.id.VehicleNo);
        date = view.findViewById(R.id.date);
        calendar = Calendar.getInstance();
        from = view.findViewById(R.id.from);
        to = view.findViewById(R.id.to);
        coilID = view.findViewById(R.id.CoilID);
        tonnage = view.findViewById(R.id.tonnage);
        pageRefNo = view.findViewById(R.id.PageRefNo);
        remarks = view.findViewById(R.id.Remarks);

        submitButton = view.findViewById(R.id.Submit);
        Button rowAdd = view.findViewById(R.id.Add);
        Button captureImage = view.findViewById(R.id.Capture);
        containerLayout = view.findViewById(R.id.containerLayout);

        updateDateEditText();
        fetchDriverNames();
        fetchVehicleNo();
        fetchLocations();

        // Set listeners for buttons
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitButton.setEnabled(false);
                displayInputValues();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });

        rowAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewSetOfFields();
            }
        });

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        // Initialize camera related components and set texture listener

        textureView = view.findViewById(R.id.texture);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);

        textureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                //open your camera here
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                // Transform you image captured size according to the surface width and height
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            }
        };

        return view;
    }

    public void showDatePicker(View view) {
        // Get the current date
        Calendar currentDate = Calendar.getInstance();

        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);

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
    //    public void showDatePicker(View view) {
//        // Get the current date
//        Calendar currentDate = Calendar.getInstance();
//
//        int year = currentDate.get(Calendar.YEAR);
//        int month = currentDate.get(Calendar.MONTH);
//        int dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);
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
//                        updateDateEditText();
//                    }
//                },
//                year, month, dayOfMonth);
//
//        datePickerDialog.show();
//    }

    // Helper method to update the date TextInputEditText with the selected date
    private void updateDateEditText() {
        String myFormat = "yyyy/MM/dd"; // Choose the desired date format
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date.setText(sdf.format(calendar.getTime()));
    }
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }
        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }
        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };
//    private void openCamera() {
//        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        Log.e(TAG, "is camera open");
//        try {
//            cameraId = manager.getCameraIdList()[0];
//            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
//            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//            assert map != null;
//            imageDimension = ((StreamConfigurationMap) map).getOutputSizes(SurfaceTexture.class)[0];
//            // Add permission for camera and let user grant the permission
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(requireContext(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
//                return;
//            }
//            manager.openCamera(cameraId, stateCallback, null);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//        Log.e(TAG, "openCamera X");
//    }

    private void openCamera() {
        CameraManager manager = (CameraManager) requireContext().getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
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


    private void fetchLocationForField(final MaterialAutoCompleteTextView field, final Map<String, Integer> locationIdsMap) {
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

            ApiService apiService = retrofit.create(ApiService.class);
            Call<List<Location>> call = apiService.getLocation();

            call.enqueue(new Callback<List<Location>>() {
                @Override
                public void onResponse(@NonNull Call<List<Location>> call, @NonNull Response<List<Location>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<String> locations = new ArrayList<>();
                        for (Location location : response.body()) {
                            locations.add(location.getLocalLocation());
                            locationIdsMap.put(location.getLocalLocation(), location.getLocalLocationId());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                requireContext(), android.R.layout.simple_dropdown_item_1line, locations
                        );

                        field.setAdapter(adapter);
                    } else {
                        Log.e("MainActivity", "Failed to fetch locations from API");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Location>> call, @NonNull Throwable t) {
                    Log.e("MainActivity", "API request failed to fetch locations", t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(requireContext(), "Saved:" + file, Toast.LENGTH_SHORT).show();
            createCameraPreview();
        }
    };
    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void takePicture() {
        if (null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) requireContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            jpegSizes = Objects.requireNonNull(characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(ImageFormat.JPEG);
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Orientation
            int rotation = requireActivity().getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            String appName = getResources().getString(R.string.app_name); // Replace with your actual app name
            File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
            String imageFileName = "JPEG_" + timeStamp;
            File file = new File(picturesDirectory, appName + "/" + imageFileName + ".jpg");
            if (!Objects.requireNonNull(file.getParentFile()).exists()) {
                file.getParentFile().mkdirs();
            }
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes, file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }

                private void save(byte[] bytes, File file) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(requireContext(), "Saved:" + file, Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void updatePreview() {
        if(null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }

    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(requireContext(), "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // Notify the hosting Activity to finish itself
                requireActivity().finish();
                Toast.makeText(requireContext(), "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }
    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        //closeCamera(); // Commented out due to the unresolved closeCamera() method
        stopBackgroundThread();
        super.onPause();
    }

    // Call this method for both "from" and "to" fields
    private void fetchLocations() {
        fetchLocationForField(from, locationFromIds);
        fetchLocationForField(to, locationToIds);
    }


    private void fetchVehicleNo(){
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

            ApiService apiService = retrofit.create(ApiService.class);
            Call<List<Vehicle>> call = apiService.getVehicle();

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
                                requireContext(), android.R.layout.simple_dropdown_item_1line, VehicleNo
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void fetchDriverNames(){
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

            ApiService apiService = retrofit.create(ApiService.class);
            Call<List<Driver>> call = apiService.getDrivers();

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
                                requireContext(), android.R.layout.simple_dropdown_item_1line, driverNames
                        );
                        // Set the adapter for the MaterialAutoCompleteTextView
                        driverNameAutoCompleteTextView.setAdapter(adapter);
                    } else {
                        Log.e("MainActivity", "Failed to fetch driver names from API" + response.code() + ", Message: " + response.message());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<List<Driver>> call, @NonNull Throwable t) {
                    Log.e("MainActivity", "API request failed to get DriverName", t);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getLastFourDigits(String input){
        if (input != null && input.length() >= 4) {
            return input.substring(input.length() - 4);
        }
        return input;
    }



    public  void displayInputValues(){
        try {
            // Concatenate values as comma-separated strings
            String coilIdValuesStr = concatenateValues(coilIdEditTextList);
            String tonnageValuesStr = concatenateValues(tonnageEditTextList);


            String selectedDriverName = driverNameAutoCompleteTextView.getText().toString();
            String dateValue = Objects.requireNonNull(date.getText()).toString();
            String VehicleNo = VehicleNumAutoComplete.getText().toString();
            String fromValue = from.getText().toString();
            String toValue = to.getText().toString();
            String coilIDValue = Objects.requireNonNull(coilID.getText()).toString();
            String tonnageValue = Objects.requireNonNull(tonnage.getText()).toString();
            String pageRefNoValue = Objects.requireNonNull(pageRefNo.getText()).toString();
            String remarksValue = remarks.getText().toString();


            // Merge original values with dynamically created values
            coilIDValue += "," + coilIdValuesStr;
            tonnageValue += "," + tonnageValuesStr;

            String driverId = String.valueOf(driverIds.get(selectedDriverName));
            String vehicleId = String.valueOf(vehicleIds.get(VehicleNo));
            String FromlocationId = String.valueOf(locationFromIds.get(fromValue));
            String TolcoationId = String.valueOf(locationToIds.get(toValue));

            // Create Retrofit instance for the submit API
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


            ApiService apiService = retrofit.create(ApiService.class);
            PostDataModel postDataModel = new PostDataModel(driverId, dateValue, vehicleId, FromlocationId, TolcoationId, coilIDValue, tonnageValue, pageRefNoValue, remarksValue);
            // Log the PostDataModel object
            Gson gson = new Gson();
            String jsonData = gson.toJson(postDataModel);
            Log.d("MainActivity", "PostDataModel JSON: " + jsonData);

            // Make the POST request
            Call<ResponseBody> call = apiService.postData(postDataModel);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    submitButton.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String successMessage = response.body().string();
                            Log.d("MainActivity", "Response: " + successMessage);
                            Toast.makeText(requireContext(), successMessage, Toast.LENGTH_LONG).show();
                            clearLists();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Log the HTTP status code and any error message
                        int statusCode = response.code();
                        String errorMessage = response.message();
                        Log.e("MainActivity", "HTTP Status Code: " + statusCode);
                        Log.e("MainActivity", "Error Message: " + errorMessage);

                        clearLists();
                        Log.e("MainActivity", "Failed to get a successful response");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    submitButton.setEnabled(true);
                    clearLists();
                    Log.e("MainActivity", "Failed to make a POST request", t);

                }
            });

            // Display values in the log
            Log.d("MainActivity", "Driver Name: " + selectedDriverName);
            Log.d("MainActivity", "Date: " + dateValue);
            Log.d("MainActivity", "Date: " + VehicleNo);
            Log.d("MainActivity", "From: " + fromValue);
            Log.d("MainActivity", "To: " + toValue);
            Log.d("MainActivity", "Coil ID: " + coilIDValue);
            Log.d("MainActivity", "Tonnage: " + tonnageValue);
            Log.d("MainActivity", "Page Ref No: " + pageRefNoValue);
            Log.d("MainActivity", "Remarks: " + remarksValue);

            driverNameAutoCompleteTextView.setText("");
            date.setText("");
            VehicleNumAutoComplete.setText("");
            from.setText("");
            to.setText("");
            coilID.setText("");
            tonnage.setText("");
            pageRefNo.setText("");
            remarks.setText("");
            // Clear dynamically created EditTexts
            clearDynamicallyCreatedEditTexts();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to clear the lists
    private void clearLists() {
        // Clear the lists after successful submission
        coilIdValues.clear();
        tonnageValues.clear();

        // Clear the dynamically created EditText fields
        for (TextInputEditText coilIdEditText : coilIdEditTextList) {
            coilIdEditText.setText("");
        }

        for (TextInputEditText tonnageEditText : tonnageEditTextList) {
            tonnageEditText.setText("");
        }
    }

    // Helper method to concatenate values into comma-separated string
    private String concatenateValues(List<TextInputEditText> editTextList) {
        List<String> values = new ArrayList<>();
        for (TextInputEditText editText : editTextList) {
            String text = Objects.requireNonNull(editText.getText()).toString();
            if (!TextUtils.isEmpty(text)) {
                values.add(text);
            }
        }
        return TextUtils.join(",", values);
    }


    private void clearDynamicallyCreatedEditTexts() {
        // Remove all dynamically created fields
        containerLayout.removeAllViews();

        // Clear the lists of EditText values
        coilIdValues.clear();
        tonnageValues.clear();
    }

    private void addNewSetOfFields(){

        // Create a new TextInputLayout for CoilID
        TextInputLayout coilIdInputLayout = createTextInputLayout(R.string.CoilID);
        TextInputEditText coilIdEditText = createTextInputEditText(InputType.TYPE_CLASS_TEXT);
        coilIdInputLayout.addView(coilIdEditText);
        containerLayout.addView(coilIdInputLayout);

        // Create a new TextInputLayout for Tonnage
        TextInputLayout tonnageInputLayout = createTextInputLayout(R.string.Tonnage);
        TextInputEditText tonnageEditText = createTextInputEditText(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        tonnageInputLayout.addView(tonnageEditText);
        containerLayout.addView(tonnageInputLayout);



        // Add values to corresponding arrays
        coilIdValues.add(Objects.requireNonNull(coilIdEditText.getText()).toString());
        tonnageValues.add(Objects.requireNonNull(tonnageEditText.getText()).toString());


        // Add EditText fields to the corresponding lists
        coilIdEditTextList.add(coilIdEditText);
        tonnageEditTextList.add(tonnageEditText);


        // Create a "Remove" button
        Button removeButton = new Button(requireContext());
        removeButton.setText("Remove");
        removeButton.setOnClickListener(v -> {
            // Remove the dynamically created fields associated with this set
            containerLayout.removeView(coilIdInputLayout);
            containerLayout.removeView(tonnageInputLayout);
            containerLayout.removeView(removeButton);

            // Remove values from corresponding arrays
            coilIdValues.remove(Objects.requireNonNull(coilIdEditText.getText()).toString());
            tonnageValues.remove(Objects.requireNonNull(tonnageEditText.getText()).toString());

            // Remove EditText fields from the corresponding lists
            coilIdEditTextList.remove(coilIdEditText);
            tonnageEditTextList.remove(tonnageEditText);

        });

        // Add the "Remove" button to the container layout
        containerLayout.addView(removeButton);

    }
    private int dpToPx() {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(10 * density);
    }

    private TextInputLayout createTextInputLayout(int hintResId) {

        int margin = dpToPx();

        TextInputLayout textInputLayout = new TextInputLayout(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(margin, margin, margin, margin); // Adjusted margin
        textInputLayout.setLayoutParams(params);
        textInputLayout.setHint(getString(hintResId));
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE); // Optional: Set the box background mode

        return textInputLayout;
    }

    private TextInputEditText createTextInputEditText(int inputType) {
        TextInputEditText editText = new TextInputEditText(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 0); // Adjusted margin
        editText.setLayoutParams(params);
        editText.setInputType(inputType);
        editText.setBackground(null); // Remove the default underline

        return editText;
    }




}