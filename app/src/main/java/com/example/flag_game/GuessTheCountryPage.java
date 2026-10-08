package com.example.flag_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.Color;
import android.view.View;

import java.util.Random;

public class GuessTheCountryPage extends AppCompatActivity {

    ImageView flagImage;
    Spinner countrySpinner;
    Button submitButton;
    TextView resultText;
    TextView correctAnswerText;

    int currentFlagIndex;

    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guess_the_country_page);

        flagImage = findViewById(R.id.guess_the_country_image_view);
        countrySpinner = findViewById(R.id.guess_the_country_spinner);
        submitButton = findViewById(R.id.submit_btn);
        resultText = findViewById(R.id.result_text_view);
        correctAnswerText = findViewById(R.id.answer_text_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                FlagData.countries
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        countrySpinner.setAdapter(adapter);

        displayRandomFlag();

        submitButton.setOnClickListener(v -> {

            if (submitButton.getText().toString().equals("Submit")) {
                checkAnswer();
            } else {
                startNewRound();
            }

        });

        Button newExitButton = findViewById(R.id.exit_guess_the_country_btn);

        newExitButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    GuessTheCountryPage.this,
                    MainActivity.class
            );

            startActivity(intent);
        });
    }

    private void displayRandomFlag() {

        currentFlagIndex = random.nextInt(FlagData.countries.length);

        String country = FlagData.countries[currentFlagIndex];

        String imageName = country.toLowerCase();

        int imageResource = getResources().getIdentifier(
                imageName,
                "drawable",
                getPackageName()
        );

        flagImage.setImageResource(imageResource);

    }

    private void checkAnswer() {

        String selectedCountry = countrySpinner.getSelectedItem().toString();
        String correctCountry = FlagData.countries[currentFlagIndex];

        if (selectedCountry.equals(correctCountry)) {

            resultText.setText("CORRECT!");
            resultText.setTextColor(Color.GREEN);

            correctAnswerText.setVisibility(View.GONE);

        } else {

            resultText.setText("WRONG!");
            resultText.setTextColor(Color.RED);

            correctAnswerText.setText("Correct country: " + correctCountry);
            correctAnswerText.setTextColor(Color.BLUE);
            correctAnswerText.setVisibility(View.VISIBLE);
        }

        submitButton.setText("Next");
    }

    private void startNewRound() {

        int newIndex;

        do {
            newIndex = random.nextInt(FlagData.countries.length);
        } while (newIndex == currentFlagIndex);

        currentFlagIndex = newIndex;

        String country = FlagData.countries[currentFlagIndex];
        String imageName = country.toLowerCase();

        int imageResource = getResources().getIdentifier(
                imageName,
                "drawable",
                getPackageName()
        );

        flagImage.setImageResource(imageResource);

        resultText.setText("");
        correctAnswerText.setText("");
        correctAnswerText.setVisibility(View.GONE);

        submitButton.setText("Submit");

        countrySpinner.setSelection(0);
    }
}