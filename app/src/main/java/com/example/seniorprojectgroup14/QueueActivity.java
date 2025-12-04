package com.example.seniorprojectgroup14;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class QueueActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.queue_layout);
    }

    public void buttonClicked(View view) {
        if (view.getId() == R.id.LeaveQueue) {
            Intent intent = new Intent(QueueActivity.this, QRScannerActivity.class);
            startActivity(intent);
        }
    }
}
