package com.example.nfc_intro_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.nfc_intro_project.AboutActivity;
import com.example.nfc_intro_project.R;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(AboutActivity.EXTRA_MESSAGE);
        String phoneNumber = intent.getStringExtra(AboutActivity.NUMBER);
        String name = intent.getStringExtra(AboutActivity.EMAIL);


        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(message);
    }
}
