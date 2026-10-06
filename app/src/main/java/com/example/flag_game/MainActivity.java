package com.example.flag_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button newExitButton = findViewById(R.id.exit_btn);

        newExitButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    StartingPage.class
            );

            startActivity(intent);
        });


        Button newGuessTheCountryButton =
                findViewById(R.id.guess_the_country_btn);

        newGuessTheCountryButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    GuessTheCountryPage.class
            );

            startActivity(intent);
        });
    }
}