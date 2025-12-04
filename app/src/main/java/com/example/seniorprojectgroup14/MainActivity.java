package com.example.seniorprojectgroup14;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.backend.*;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
    }

    public void buttonClicked(View view) {

        if (view.getId() == R.id.button) {
            Intent intent = new Intent(MainActivity.this, RiderStatusActivity.class);

            User testUser = new User("Test User", "password");
            VirtualQueue testQueue = new VirtualQueue();
            Ride testRide = new Ride(
                    "0", "Test Ride", testQueue, 100, 5);
            testQueue.joinQueue(testUser);
            testQueue.viewQueue();

            intent.putExtra("isGroup", false);
            intent.putExtra("testUser", testUser.getUsername());
            intent.putExtra("testRide", testRide.getRideName());
            CharSequence text = "You're in the queue!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this , text, duration);
            toast.show();
            startActivity(intent);
        }
        else if (view.getId() == R.id.button2) {
            Intent intent = new Intent(MainActivity.this, JoinGroupActivity.class);
            startActivity(intent);
        }
    }
}
