package com.ifti.eventman3;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    //    declaration
//    Edit Text
    private EditText enteredname, enteredplace, enteredcapacity, enteredbudget, enteredemail, enteredphone, entereddescription, enteredDateTime;
    //    Text View
    private TextView message;
    //    Buttons
    private Button btn_cancel, btn_save, btn_share;

    //    CheckBox
    private CheckBox indore, outdoor, online;

    private String existingKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

//        getSupportActionBar().hide();

        enteredname = findViewById(R.id.name);
        enteredplace = findViewById(R.id.place);
        enteredDateTime = findViewById(R.id.date_time);
        enteredcapacity = findViewById(R.id.capacity);
        enteredbudget = findViewById(R.id.budget);
        enteredemail = findViewById(R.id.email);
        enteredphone = findViewById(R.id.phone);
        entereddescription = findViewById(R.id.description);

        indore = findViewById(R.id.indore);
        outdoor = findViewById(R.id.outDoor);
        online = findViewById(R.id.online);

        message = findViewById(R.id.message);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);
        btn_share = findViewById(R.id.btn_share);

        Intent i = getIntent();
        if (i.hasExtra("EVENT_KEY")) {
            existingKey = i.getStringExtra("EVENT_KEY");
            KeyValueDB db = new KeyValueDB(CreateEventActivity.this);
            String value = db.getValueByKey(existingKey);
            String values[] = value.split("---");

            enteredname.setText(values[0]);
            enteredplace.setText(values[1]);
            enteredDateTime.setText(values[3]);
            enteredcapacity.setText(values[4]);
            enteredbudget.setText(values[5]);
            enteredemail.setText(values[6]);
            enteredphone.setText(values[7]);
            entereddescription.setText(values[8]);


            if (values[2].equals("outdoor")) {
                outdoor.setChecked(true);
            } else if (values[2].equals("indoor")) {
                indore.setChecked(true);
            } else if (values[2].equals("online")) {
                online.setChecked(true);
            }

            db.close();
            message.setText("");
        }


//        SAVE BUTTON
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = enteredname.getText().toString();
                String email = enteredemail.getText().toString();
                String phone = enteredphone.getText().toString();
                String place = enteredplace.getText().toString();
                String capacity = enteredcapacity.getText().toString();
                String budget = enteredbudget.getText().toString();
                String description = entereddescription.getText().toString();
                String dateTime = enteredDateTime.getText().toString();
                boolean type1 = indore.isChecked();
                boolean type2 = outdoor.isChecked();
                boolean type3 = online.isChecked();
                String type = "";

                if (type1) {
                    type = "indoor";
                } else if (type2) {
                    type = "outdoor";
                } else {
                    type = "online";
                }


                if (TextUtils.isEmpty(name)) {
                    message.setText("Name is required");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    message.setText("Email is required");
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    message.setText("Invalid email address");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    message.setText("Phone is required");
                    return;
                }

                if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
                    message.setText("Invalid phone number");
                    return;
                }

                if (TextUtils.isEmpty(place)) {
                    message.setText("Place is required");
                    return;
                }
                if (TextUtils.isEmpty(budget)) {
                    message.setText("Budget is required");
                    return;
                }
                if (TextUtils.isEmpty(capacity)) {
                    message.setText("Capacity is required");
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    message.setText("Description is required");
                    return;
                }
                if (TextUtils.isEmpty(dateTime)) {
                    message.setText("Date and time is required");
                    return;
                }


                String value = name + "---" + place + "---" + type + "---" + dateTime + "---" + capacity + "---" + budget + "---" + email + "---" + phone + "---" + description;
                KeyValueDB db = new KeyValueDB(CreateEventActivity.this);
                // write code to generate a unique id
                if (existingKey.length() == 0) {
                    String key = name + System.currentTimeMillis();
                    existingKey = key;
                    System.out.println(key);
                    // write code to save the information
                    db.insertKeyValue(key, value);
                } else {
                    db.updateValueByKey(existingKey, value);
                }

                db.close();

                String[] keys = {"action", "id", "semester", "key", "event"};
                String[] values = {"backup", "2020160255", "20231", existingKey, value};
                httpRequest(keys, values);

                Toast.makeText(getApplicationContext(), "Event information saved to local database!!", Toast.LENGTH_SHORT).show();


                Intent i2 = new Intent(CreateEventActivity.this, EventListActivity.class);
                startActivity(i2);
            }
        });

//        Cancel Button
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                back to home
                Intent i = new Intent(CreateEventActivity.this, EventListActivity.class);
                startActivity(i);
                finish();
            }
        });

//        Share Button
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("event_prefs", MODE_PRIVATE);

                // Loop through the SharedPreferences contents and print each key-value pair
                Map<String, ?> allEntries = sharedPreferences.getAll();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue().toString());
                }

            }
        });
    }

    private void httpRequest(final String keys[], final String values[]) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                for (int i = 0; i < keys.length; i++) {
                    params.add(new BasicNameValuePair(keys[i], values[i]));
                }
//                URL url = https://muthosoft.com;
                String data = "";
                try {
                    data = JSONParser.getInstance().makeHttpRequest("https://muthosoft.com/univ/cse489/index.php", "POST", params);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String data) {
                if (data != null) {
                    Toast.makeText(getApplicationContext(), "Saved to remote database", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

}