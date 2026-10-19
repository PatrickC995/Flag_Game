package com.example.flag_game;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GuessTheFlagPage extends AppCompatActivity {

    private TextView scoreText;
    private final RoundScore roundScore = new RoundScore(1);
    private ImageView[] flagImages;
    private TextView countryNameText;
    private TextView resultText;
    private Button nextButton;

    private final Random random = new Random();

    private int[] countryIndices;
    private int correctFlagPosition;
    private int selectedFlagPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_the_flag_page);

        scoreText = findViewById(R.id.round_score_text);
        updateScoreDisplay();

        countryNameText = findViewById(R.id.guess_flag_country_name);
        resultText = findViewById(R.id.guess_flag_result);
        nextButton = findViewById(R.id.guess_flag_next_button);

        flagImages = new ImageView[] {
                findViewById(R.id.guess_flag_image_1),
                findViewById(R.id.guess_flag_image_2),
                findViewById(R.id.guess_flag_image_3)
        };

        // Clicking a flag immediately submits the answer.
        for (int i = 0; i < flagImages.length; i++) {
            final int position = i;

            flagImages[i].setOnClickListener(
                    view -> checkAnswer(position)
            );
        }

        nextButton.setOnClickListener(view -> startNewRound());

        Button exitButton = findViewById(R.id.guess_flag_exit_button);
        exitButton.setOnClickListener(view -> finish());

        // Restore the round after screen rotation.
        if (savedInstanceState != null) {
            countryIndices =
                    savedInstanceState.getIntArray("countryIndices");

            correctFlagPosition =
                    savedInstanceState.getInt("correctFlagPosition");

            selectedFlagPosition =
                    savedInstanceState.getInt("selectedFlagPosition", -1);

            displayRound();
        } else {
            startNewRound();
        }
    }

    private void updateScoreDisplay() {
        scoreText.setText("Score: " + roundScore.getScore());
    }

    private void awardCountryPoint(int countryIndex) {
        roundScore.awardPoint(countryIndex);
        updateScoreDisplay();
    }

    private void startNewRound() {

        roundScore.reset();
        updateScoreDisplay();

        // Shuffle the country indices and take the first three.
        // This guarantees three different flags within each round.
        List<Integer> availableIndices = new ArrayList<>();

        for (int i = 0; i < FlagData.countries.length; i++) {
            availableIndices.add(i);
        }

        Collections.shuffle(availableIndices, random);

        countryIndices = new int[] {
                availableIndices.get(0),
                availableIndices.get(1),
                availableIndices.get(2)
        };

        // Choose one of the three flags as the correct answer.
        correctFlagPosition = random.nextInt(flagImages.length);

        // -1 means the user has not answered yet.
        selectedFlagPosition = -1;

        displayRound();
    }

    private void displayRound() {

        String targetCountry =
                FlagData.countries[countryIndices[correctFlagPosition]];

        countryNameText.setText(targetCountry);

        for (int i = 0; i < flagImages.length; i++) {

            String imageName =
                    FlagData.countries[countryIndices[i]]
                            .toLowerCase(Locale.ROOT);

            int imageResource = getResources().getIdentifier(
                    imageName,
                    "drawable",
                    getPackageName()
            );

            flagImages[i].setImageResource(imageResource);

            // Remove the previous round's highlight.
            flagImages[i].setBackgroundColor(Color.TRANSPARENT);

            // Enable flags only when the round has not been answered.
            flagImages[i].setEnabled(selectedFlagPosition == -1);

            // Avoid revealing the answer through accessibility labels.
            flagImages[i].setContentDescription(
                    getString(R.string.guess_flag_option, i + 1)
            );
        }

        resultText.setText("");
        nextButton.setVisibility(View.GONE);

        // Restore feedback if the user already answered before rotating.
        if (selectedFlagPosition != -1) {
            displayResult();
        }
    }

    private void checkAnswer(int position) {

        // Only one answer is permitted per round.
        if (selectedFlagPosition != -1) {
            return;
        }

        selectedFlagPosition = position;

        // Disable all three flags immediately.
        for (ImageView flagImage : flagImages) {
            flagImage.setEnabled(false);
        }

        displayResult();
    }

    private void displayResult() {

        boolean correct =
                selectedFlagPosition == correctFlagPosition;

        if (correct) {
            awardCountryPoint(0);
            resultText.setText(R.string.guess_flag_correct);
            resultText.setTextColor(Color.GREEN);
        } else {
            resultText.setText(R.string.guess_flag_wrong);
            resultText.setTextColor(Color.RED);
        }

        // Highlight the correct flag whether the selection was right or wrong.
        flagImages[correctFlagPosition]
                .setBackgroundColor(Color.BLUE);

        // Country names can be revealed after the answer is submitted.
        for (int i = 0; i < flagImages.length; i++) {
            flagImages[i].setContentDescription(
                    FlagData.countries[countryIndices[i]]
            );
        }

        nextButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putIntArray("countryIndices", countryIndices);
        outState.putInt("correctFlagPosition", correctFlagPosition);
        outState.putInt("selectedFlagPosition", selectedFlagPosition);
    }
}