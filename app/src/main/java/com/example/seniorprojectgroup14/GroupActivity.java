package com.example.seniorprojectgroup14;

import com.example.backend.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GroupActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_select);
    }

    public void buttonClicked(View view) {

        if (view.getId() == R.id.button3) {
            User testUser = new User("Test User");
            Group testGroup = new Group("Test Group", testUser);
            VirtualQueue testQueue = new VirtualQueue();
            Ride testRide = new Ride(
                    "0", "Test Ride", testQueue, 100, 5);
            testQueue.joinQueue(testGroup);
            CharSequence text = "Group added to queue!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this , text, duration);
            toast.show();
            testQueue.viewQueue();
        }
        else if (view.getId() == R.id.button4) {
            User testUser = new User("Test User");
            Group testGroup = new Group("Test Group", testUser);
            VirtualQueue testQueue = new VirtualQueue();
            Ride testRide = new Ride(
                    "0", "Test Ride", testQueue, 100, 5);
            testGroup.addMember(new User("Test User 2"));
            testQueue.joinQueue(testGroup);
            CharSequence text = "Group added to queue!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this , text, duration);
            toast.show();
            testQueue.viewQueue();
        }

    }

}
