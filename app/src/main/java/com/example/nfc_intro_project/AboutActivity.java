package com.example.nfc_intro_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AboutActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.nfctest.MESSAGE";
    public static final String NUMBER ="com.example.nfctest.NUMBER";
    public static final String EMAIL ="com.example.nfctest.TITLE";
    public static final String LINKEDIN="com.example.nfctest.LINKEDIN";
    public static final String CITY= "com.example.nfctest.CITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.Title);
        EditText name = (EditText) findViewById(R.id.editText2);
        EditText phone = (EditText) findViewById(R.id.phone);
        EditText email = (EditText) findViewById(R.id.email);
        EditText location = (EditText) findViewById(R.id.location);
        String message = editText.getText().toString();
        String identification= name.getText().toString();
        String phone_number= phone.getText().toString();
        String electronicMail = email.getText().toString();
        String city = location.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(NUMBER, phone_number);
        intent.putExtra(CITY, city);


        startActivity(intent);
    }
}
