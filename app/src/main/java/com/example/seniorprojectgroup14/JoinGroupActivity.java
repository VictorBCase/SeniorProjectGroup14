package com.example.seniorprojectgroup14;

import com.example.backend.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class JoinGroupActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_group_layout);
    }

    public void buttonClicked(View view) {

        if (view.getId() == R.id.button3) {
            Intent intent = new Intent(JoinGroupActivity.this, QueueActivity.class);

            User testUser = new User("Test User", "password");
            Group testGroup = new Group("Test Group", testUser);
            VirtualQueue testQueue = new VirtualQueue();
            Ride testRide = new Ride(
                    "0", "Test Ride", testQueue, 100, 5);
            testQueue.joinQueue(testGroup);

            intent.putExtra("isGroup", true);
            intent.putExtra("testGroup", testGroup.getGroupName());
            intent.putExtra("testRide", testRide.getRideName());


            CharSequence text = "Group added to queue!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this , text, duration);
            toast.show();
            testQueue.viewQueue();
            startActivity(intent);
        }
        else if (view.getId() == R.id.button4) {
            Intent intent = new Intent(JoinGroupActivity.this, QueueActivity.class);

            User testUser = new User("Test User", "password");
            Group testGroup = new Group("Test Group", testUser);
            VirtualQueue testQueue = new VirtualQueue();
            Ride testRide = new Ride(
                    "0", "Test Ride", testQueue, 100, 5);
            testGroup.addMember(new User("Test User 2", "password"));
            testQueue.joinQueue(testGroup);
            CharSequence text = "Group added to queue!";

            intent.putExtra("isGroup", true);
            intent.putExtra("testGroup", testGroup.getGroupName());
            intent.putExtra("testRide", testRide.getRideName());

            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this , text, duration);
            toast.show();
            testQueue.viewQueue();
            startActivity(intent);
        }

    }

}
