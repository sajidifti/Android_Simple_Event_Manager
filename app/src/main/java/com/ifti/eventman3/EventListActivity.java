package com.ifti.eventman3;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class EventListActivity extends AppCompatActivity {
    private ListView lvEvents;
    private ArrayList<Event> events;
    private CustomEventAdapter adapter;

    private Button btnCreate, btnHistory, btnExit, btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        events = new ArrayList<>();
        lvEvents = findViewById(R.id.listEvents);
        loadData();

        btnCreate = findViewById(R.id.btnCreate);
        btnHistory = findViewById(R.id.btnHistory);
        btnExit = findViewById(R.id.btnExit);
        btnLogOut = findViewById(R.id.btnLogOut);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EventListActivity.this, CreateEventActivity.class);
                startActivity(i);
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("signup_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("remember_me", false);
                editor.apply();

                Intent i = new Intent(EventListActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void loadData() {
        events.clear();
        KeyValueDB db = new KeyValueDB(EventListActivity.this);
        Cursor rows = db.execute("SELECT * FROM key_value_pairs");

        if (rows.getCount() == 0) {
            return;
        }

        while (rows.moveToNext()) {
            String key = rows.getString(0);
            String eventData = rows.getString(1);
            String[] fieldValues = eventData.split("---");

            String name = fieldValues[0];
            String place = fieldValues[1];
            String eventType = fieldValues[2];
            String dateTime = fieldValues[3];
            String capacity = fieldValues[4];
            String budget = fieldValues[5];
            String email = fieldValues[6];
            String phone = fieldValues[7];
            String description = fieldValues[8];

            Event e = new Event(key, name, place, dateTime, eventType, capacity, budget, email, phone, description);
            events.add(e);
        }
        db.close();

        adapter = new CustomEventAdapter(this, events);
        lvEvents.setAdapter(adapter);

        lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //Position = Real Position
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Intent i = new Intent(EventListActivity.this, CreateEventActivity.class);
                i.putExtra("EVENT_KEY", events.get(position).key);
                startActivity(i);
            }
        });
        // handle the long-click on an event-list item
        lvEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //String message = "Do you want to delete event - "+events[position].name +" ?";
                String message = "Do you want to delete event - " + events.get(position).name + " ?";
                //System.out.println(message);
                showDialog(message, "Delete Event", events.get(position).key);
                return true;
            }
        });
    }

    private void showDialog(String message, String title, String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                KeyValueDB db = new KeyValueDB(getApplicationContext());
                db.deleteDataByKey(key);
                dialog.cancel();
//                loadData();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }

        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onRestart() {
        super.onRestart();
        //adapter.notifyDataSetChanged();
        loadData();
    }
}