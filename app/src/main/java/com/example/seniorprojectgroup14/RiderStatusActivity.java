package com.example.seniorprojectgroup14;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
public class RiderStatusActivity extends Activity {

    private TextView welcomeMessage;
    private SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_status_layout);

        welcomeMessage = findViewById(R.id.welcomeMessage);
        sessionManager = SessionManager.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWelcomeMessage();
    }

    private void updateWelcomeMessage() {
        String username = sessionManager.getUsername();
        if (username != null) {
            welcomeMessage.setText("Hi " + username + "!");
        } else {
            welcomeMessage.setText("Hi!");
        }
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
