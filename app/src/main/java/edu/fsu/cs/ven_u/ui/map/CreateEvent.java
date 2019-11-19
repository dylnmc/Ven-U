package edu.fsu.cs.ven_u.ui.map;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import edu.fsu.cs.ven_u.Database;

import edu.fsu.cs.ven_u.R;

public class CreateEvent extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        // Inflate the layout for this fragment
        Bundle extras = getIntent().getExtras();
        final double lat = extras.getDouble("LatID");
        final double lon = extras.getDouble("LonID");
        final String user = extras.getString("USER");

        final EditText eventName = findViewById(R.id.eventName);
        final EditText eventDesc = findViewById(R.id.eventDesc);
        final RadioGroup radioGroup = findViewById(R.id.eventType);

        Button createEvent = findViewById(R.id.eventCreate);
        Button cancel = findViewById(R.id.eventCancel);

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eventName.getText().toString().length() < 5) {
                    Toast.makeText(getApplicationContext(), "Please create an Event name at least 5 characters long",
                            Toast.LENGTH_SHORT).show();
                } else if (eventDesc.getText().toString().length() < 20) {
                    Toast.makeText(getApplicationContext(), "Please write a description at least 20 characters long",
                            Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton eventType = findViewById(radioGroup.getCheckedRadioButtonId());

                    final Database db = new Database();
                    Database.Event event = new Database.Event(eventName.getText().toString(), eventDesc.getText().toString(), user,
                            lat, lon, eventType.getText().toString());
                    db.addEvent(event);
                    finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}
