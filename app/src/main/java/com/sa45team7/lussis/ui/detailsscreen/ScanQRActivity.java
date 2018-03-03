package com.sa45team7.lussis.ui.detailsscreen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.sa45team7.lussis.R;
import com.sa45team7.lussis.helpers.UserManager;
import com.sa45team7.lussis.rest.LUSSISClient;
import com.sa45team7.lussis.rest.model.Disbursement;
import com.sa45team7.lussis.rest.model.LUSSISResponse;
import com.sa45team7.lussis.ui.adapters.ReqDetailAdapter;
import com.sa45team7.lussis.utils.ErrorUtil;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanQRActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 200;
    private SurfaceView cameraView;
    private CameraSource cameraSource;
    private TextView resultText;
    private String scanResult;
    private RecyclerView disDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            onStop();
        }

        initViews();
        initBarCodeDetector();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    private void initViews() {
        cameraView = findViewById(R.id.qr_scanner);
        cameraView.setZOrderMediaOverlay(true);

        Button confirmButton = findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scanResult != null) {
                    int disId = Integer.parseInt(scanResult);
                    acknowledge(disId);
                } else {
                    Toast.makeText(ScanQRActivity.this,
                            "No disbursment scanned", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button retryButton = findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBarCodeDetector();
            }
        });

        resultText = findViewById(R.id.result_text);

        disDetailList = findViewById(R.id.dis_detail_list);
    }

    private void initBarCodeDetector() {
        cameraView = findViewById(R.id.qr_scanner);
        cameraView.setZOrderMediaOverlay(true);

        BarcodeDetector barcode = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        if (!barcode.isOperational()) {
            Toast.makeText(getApplicationContext(), "Couldn't setup the detector", Toast.LENGTH_LONG).show();
            this.finish();
        }

        cameraSource = new CameraSource.Builder(this, barcode)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24)
                .setAutoFocusEnabled(true)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ContextCompat.checkSelfPermission(ScanQRActivity.this,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(cameraView.getHolder());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        barcode.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                if (scanResult != null) {
                    try {
                        int disId = Integer.parseInt(scanResult);
                        resultText.setText(scanResult);
                        getDisbursement(disId);
                    } catch (NumberFormatException e) {
                        Toast.makeText(ScanQRActivity.this,
                                "Error: invalid code", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scanResult = barcodes.valueAt(0).rawValue;

                            if (cameraSource != null) cameraSource.release();
                        }
                    });

                }
            }
        });
    }

    private void acknowledge(int disbursementId) {

        int empNum = UserManager.getInstance().getCurrentEmployee().getEmpNum();

        Call<LUSSISResponse> call = LUSSISClient.getApiService().acknowledge(disbursementId, empNum);
        call.enqueue(new Callback<LUSSISResponse>() {
            @Override
            public void onResponse(@NonNull Call<LUSSISResponse> call, @NonNull Response<LUSSISResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ScanQRActivity.this,
                            response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String error = ErrorUtil.parseError(response).getMessage();
                    Toast.makeText(ScanQRActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LUSSISResponse> call, @NonNull Throwable t) {
                Toast.makeText(ScanQRActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDisbursement(int id) {
        Call<Disbursement> call = LUSSISClient.getApiService().getDisbursementById(id);
        call.enqueue(new Callback<Disbursement>() {
            @Override
            public void onResponse(@NonNull Call<Disbursement> call, @NonNull Response<Disbursement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReqDetailAdapter adapter = new ReqDetailAdapter(response.body().getDisbursementDetails());
                    disDetailList.setAdapter(adapter);
                } else {
                    String error = ErrorUtil.parseError(response).getMessage();
                    Toast.makeText(ScanQRActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Disbursement> call, @NonNull Throwable t) {
                Toast.makeText(ScanQRActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
