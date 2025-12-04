package com.example.seniorprojectgroup14;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RiderStatusActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_status_layout);
    }

    public void buttonClicked(View view) {
        if (view.getId() == R.id.rideSolo) {
            Intent intent = new Intent(RiderStatusActivity.this, QRScannerActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.createGroup) {
            Intent intent = new Intent(RiderStatusActivity.this, CreateGroupActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.joinGroup) {
            Intent intent = new Intent(RiderStatusActivity.this, JoinGroupActivity.class);
            startActivity(intent);
        }
    }
}
