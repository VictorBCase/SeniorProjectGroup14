package com.example.seniorprojectgroup14;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.backend.*;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
    }

    public void buttonClicked(View view) {

        if (view.getId() == R.id.button3) {
            User testUser = new User("Test User");
            VirtualQueue testQueue = new VirtualQueue();
            Ride testRide = new Ride(
                    "0", "Test Ride", testQueue, 100, 5);
            testQueue.joinQueue(testUser);
            testQueue.viewQueue();
            CharSequence text = "You're in the queue!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this , text, duration);
            toast.show();
        }
        else if (view.getId() == R.id.button4) {
            Intent intent = new Intent(MainActivity.this, GroupActivity.class);
            startActivity(intent);
        }
    }
}
