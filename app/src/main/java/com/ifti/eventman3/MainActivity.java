package com.ifti.eventman3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //    LinearLayout
    private LinearLayout name_fld,email_fld, phone_fld,re_pass_fld;

    //    Text View
    private TextView title, question, login_signup;

    //    Edit Text Part
    private EditText name, email, phone,user_id,password,re_pass;

    //    Check Box
    private CheckBox rem_user, rem_pass, rem_me;

    //    Buttons
    private Button btn_exit, btn_go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        check for remember me
        SharedPreferences sharedPreferences = getSharedPreferences("signup_prefs", MODE_PRIVATE);

        boolean remem = sharedPreferences.getBoolean("remember_me",false);

        if(remem){
            Intent i = new Intent(MainActivity.this, EventListActivity.class);
            startActivity(i);
            finish();
        }

        setContentView(R.layout.activity_main);

//        LinearLayout
        name_fld = findViewById(R.id.namefld);
        email_fld = findViewById(R.id.emailfld);
        phone_fld = findViewById(R.id.phonefld);
        re_pass_fld = findViewById(R.id.re_passfld);

//        TextView
        title = findViewById(R.id.title);
        login_signup = findViewById(R.id.login_signup);
        question = findViewById(R.id.question);

//        Check Box
        rem_user = findViewById(R.id.rem_user);
        rem_pass=findViewById(R.id.rem_pass);
        rem_me=findViewById(R.id.rem_me);

//        Buttons
        btn_exit = findViewById(R.id.btn_exit);
        btn_go = findViewById(R.id.btn_go);

//        Edit Text Fields
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        user_id = findViewById(R.id.user_id);
        password = findViewById(R.id.password);
        re_pass = findViewById(R.id.re_pass);

        boolean rpass = sharedPreferences.getBoolean("remember_pass",false);
        boolean ruser = sharedPreferences.getBoolean("remember_user",false);

        if (ruser){
            String user = sharedPreferences.getString("userID","");

            user_id.setText(user);
            rem_user.setChecked(true);
        }

        if (rpass){
            String pass = sharedPreferences.getString("password","");

            password.setText(pass);
            rem_pass.setChecked(true);
        }

        //        For Login Signup click
        login_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String btn_text = login_signup.getText().toString();

                if (btn_text.equals("login")){
//                    Hide unnecessary fields and change content
                    name_fld.setVisibility(View.GONE);
                    email_fld.setVisibility(View.GONE);
                    phone_fld.setVisibility(View.GONE);
                    re_pass_fld.setVisibility(View.GONE);
                    question.setText("Have no account?");
                    title.setText("Login");

                    login_signup.setText("signup");
                }else{
//                    Show necessary fields and content
                    name_fld.setVisibility(View.VISIBLE );
                    email_fld.setVisibility(View.VISIBLE );
                    phone_fld.setVisibility(View.VISIBLE );
                    re_pass_fld.setVisibility(View.VISIBLE );
                    question.setText("Already have account?");
                    title.setText("SignUp");

                    login_signup.setText("login");
                }
            }
        });

//         GO BUTTON FUNCTIONALITY
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Check for login or signup
                String check = login_signup.getText().toString();

                if(check.equals("login")){
//                    Signup tasks
//                Getting all the value
                    String nameV = name.getText().toString();
                    String emailV = email.getText().toString();
                    String phoneV = phone.getText().toString();
                    String useridV = user_id.getText().toString();
                    String passV = password.getText().toString();
                    String re_passV = re_pass.getText().toString();
                    boolean checkrem_pass = rem_pass.isChecked();
                    boolean checkrem_user = rem_user.isChecked();
                    boolean checkrem_me = rem_me.isChecked();

//                    Check for validation

                    // Validate the user input
                    if (nameV.isEmpty() || emailV.isEmpty() || passV.isEmpty()) {

                        Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(emailV).matches()) {
                        Toast.makeText(getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    } else if (password.length() < 8) {

                        Toast.makeText(getApplicationContext(), "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                    } else if (!passV.equals(re_passV)) {
                        Toast.makeText(getApplicationContext(), "Password and re enter password does not match", Toast.LENGTH_SHORT).show();
                    } else{

                        Toast.makeText(getApplicationContext(), "Signup successful!", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name", nameV);
                        editor.putString("email", emailV);
                        editor.putString("phone", phoneV);
                        editor.putString("userID", useridV);
                        editor.putString("password", passV);
                        editor.putString("re_entered_password", re_passV);
                        editor.putBoolean("remember_user", checkrem_user);
                        editor.putBoolean("remember_pass", checkrem_pass);
                        editor.putBoolean("remember_me", checkrem_me);

                        System.out.println("Checking path");

                        editor.apply();

                        login_signup.callOnClick();
                    }
                }

                if(check.equals("signup")){
//                    Login tasks
                    String useridV = user_id.getText().toString();
                    String passV = password.getText().toString();

                    SharedPreferences sharedPreferences = getSharedPreferences("signup_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    String userID = sharedPreferences.getString("userID", "");
                    String password = sharedPreferences.getString("password", "");

                    if (userID.equals(useridV) && password.equals(passV)) {
                        // Login successful

                        boolean checkrem_pass = rem_pass.isChecked();
                        boolean checkrem_user = rem_user.isChecked();
                        boolean checkrem_me = rem_me.isChecked();

                        editor.putBoolean("remember_user", checkrem_user);
                        editor.putBoolean("remember_pass", checkrem_pass);
                        editor.putBoolean("remember_me", checkrem_me);
                        editor.apply();

                        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, EventListActivity.class);
                        startActivity(intent);
                    } else {
                        // Login failed
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

//        Exit button functionality
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });
    }
}