package com.example.seniorprojectgroup14;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.seniorprojectgroup14.plainOldJavaObjects.UserPositionResponse;
import com.example.seniorprojectgroup14.plainOldJavaObjects.ViewWaitTimeResponse;
import com.example.seniorprojectgroup14.retrofitAPICommunication.DataRepository;

public class QueueActivity extends Activity {

    private SessionManager sessionManager;
    private DataRepository dataRepository;
    private String rideID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.queue_layout);

        sessionManager = SessionManager.getInstance();
        dataRepository = DataRepository.getInstance();
        rideID = getIntent().getStringExtra("rideId");

        TextView rideName = findViewById(R.id.line);
        String nameOfRide = "You are\nin line for " + dataRepository.getRideName(rideID);
        rideName.setText(nameOfRide);

        final TextView pplInFront = findViewById(R.id.pplInFront);
        dataRepository.userPosition(rideID, sessionManager.getUserId(), new DataRepository.RepoCallback<UserPositionResponse>() {
            @Override
            public void onSuccess(UserPositionResponse response) {
                String pplinfrontString = "People in Front of You: " + response.getPosition();
                pplInFront.setText(pplinfrontString);
            }

            @Override
            public void onError(String error) {
                pplInFront.setText("People in Front of You: Error");
            }
        });

        final TextView estTime = findViewById(R.id.estTime);
        dataRepository.viewWaitTime(rideID, new DataRepository.RepoCallback<ViewWaitTimeResponse>() {
            @Override
            public void onSuccess(ViewWaitTimeResponse response) {
                String waitTimeText = "Estimated Time: " + response.getWaitTime();
                estTime.setText(waitTimeText);
            }

            @Override
            public void onError(String error) {
                estTime.setText("Estimated Time: Error");
            }
        });
    }

    public void buttonClicked(View view) {
        if (view.getId() == R.id.LeaveQueue) {
            Intent intent = new Intent(QueueActivity.this, QRScannerActivity.class);
            startActivity(intent);
        }
    }
}
