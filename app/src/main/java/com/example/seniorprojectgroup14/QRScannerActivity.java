package com.example.seniorprojectgroup14;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.*;
import com.example.seniorprojectgroup14.plainOldJavaObjects.ScanToJoinResponse;
import com.example.seniorprojectgroup14.retrofitAPICommunication.DataRepository;
import com.google.zxing.Result;

public class QRScannerActivity extends Activity {
    private CodeScanner mCodeScanner;
    private SessionManager sessionManager;

    DataRepository dataRepository = DataRepository.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        sessionManager = SessionManager.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scan_layout);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String rideID = result.getText().substring(result.getText().lastIndexOf('/'));
                        Toast.makeText(QRScannerActivity.this, rideID, Toast.LENGTH_SHORT).show();
                        dataRepository.scanToJoin(rideID, sessionManager.getUserId(), result.getText(), new DataRepository.RepoCallback<ScanToJoinResponse>(){
                            @Override
                            public void onSuccess(ScanToJoinResponse result) {
                                Intent intent = new Intent(QRScannerActivity.this, QueueActivity.class);
                                intent.putExtra("rideId", rideID);
                                startActivity(intent);
                            }
                            @Override
                            public void onError(String message) {
                                Toast.makeText(QRScannerActivity.this, "SERVER DENIED: " + message, Toast.LENGTH_LONG).show();
                            }

                        });


                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) 
            {
                mCodeScanner.setZoom(progress);
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) 
            {
                
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            
            }
        });

        checkPermission(android.Manifest.permission.CAMERA, 130);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }
}
