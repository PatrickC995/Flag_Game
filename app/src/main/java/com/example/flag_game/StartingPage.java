package com.example.flag_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class StartingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);

        Button aboutBtn = findViewById(R.id.about_btn);
        TextView aboutTxt = findViewById(R.id.about_txt);
        aboutTxt.setVisibility(TextView.GONE);
        aboutBtn.setOnClickListener(v -> {
            if (aboutTxt.getVisibility() == TextView.GONE) {
                aboutTxt.setText("“I confirm that I understand what plagiarism is and have" +
                        "read the University’s policy on plagiarism and understand the definition of plagiarism." +
                        "The work that I have submitted is entirely my own. Any work from" +
                        "other authors is duly referenced and acknowledged. I understand that I may face" +
                        "sanctions in accordance with the policies and procedures of the University.”");
                aboutTxt.setVisibility(TextView.VISIBLE);
            } else {
                aboutTxt.setVisibility(TextView.GONE);
            }
        });

        Button newFlagGameBtn = findViewById(R.id.new_flag_game_btn);
        newFlagGameBtn.setOnClickListener(v -> {
            Intent intent = new Intent(StartingPage.this, MainActivity.class);
            startActivity(intent);
        });

    }
}