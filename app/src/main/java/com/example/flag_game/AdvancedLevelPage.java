package com.example.flag_game;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;

public class AdvancedLevelPage extends AppCompatActivity {

    private static final int MAX_ATTEMPTS = 3;
    private TextView scoreText;
    private final RoundScore roundScore = new RoundScore(3);
    private final ImageView[] flagImages = new ImageView[3];
    private final EditText[] answerInputs = new EditText[3];
    private final TextView[] correctAnswerTexts = new TextView[3];

    private final String[] roundCountries = new String[3];
    private final boolean[] correctAnswers = new boolean[3];

    private TextView attemptsText;
    private TextView resultText;
    private Button submitButton;

    private int unsuccessfulSubmissions = 0;
    private boolean roundEnded = false;

    private int defaultInputTextColour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_level_page);

        scoreText = findViewById(R.id.round_score_text);
        updateScoreDisplay();

        flagImages[0] = findViewById(R.id.advanced_flag_1);
        flagImages[1] = findViewById(R.id.advanced_flag_2);
        flagImages[2] = findViewById(R.id.advanced_flag_3);

        answerInputs[0] = findViewById(R.id.advanced_answer_1);
        answerInputs[1] = findViewById(R.id.advanced_answer_2);
        answerInputs[2] = findViewById(R.id.advanced_answer_3);

        correctAnswerTexts[0] =
                findViewById(R.id.advanced_correct_answer_1);
        correctAnswerTexts[1] =
                findViewById(R.id.advanced_correct_answer_2);
        correctAnswerTexts[2] =
                findViewById(R.id.advanced_correct_answer_3);

        attemptsText = findViewById(R.id.advanced_attempts_text);
        resultText = findViewById(R.id.advanced_result_text);
        submitButton = findViewById(R.id.advanced_submit_button);

        Button exitButton =
                findViewById(R.id.advanced_exit_button);

        defaultInputTextColour =
                answerInputs[0].getCurrentTextColor();

        submitButton.setOnClickListener(view -> {
            if (roundEnded) {
                startNewRound();
            } else {
                checkAnswers();
            }
        });

        exitButton.setOnClickListener(view -> finish());

        if (savedInstanceState == null) {
            startNewRound();
        } else {
            restoreRound(savedInstanceState);
        }
    }

    // AI was used for syntax guidance and debugging of the startNewRound method
    private void startNewRound() {

        roundScore.reset();
        updateScoreDisplay();
        ArrayList<String> countries = new ArrayList<>(
                new LinkedHashSet<>(
                        Arrays.asList(FlagData.countries)
                )
        );

        if (countries.size() < 3) {
            throw new IllegalStateException(
                    "FlagData.countries must contain at least "
                            + "three different countries."
            );
        }

        Collections.shuffle(countries);

        unsuccessfulSubmissions = 0;
        roundEnded = false;

        submitButton.setText("Submit");
        resultText.setText("");
        resultText.setVisibility(View.GONE);

        for (int i = 0; i < 3; i++) {
            roundCountries[i] = countries.get(i);
            correctAnswers[i] = false;

            showFlag(i);

            answerInputs[i].setEnabled(true);
            answerInputs[i].setText("");
            answerInputs[i].setTextColor(defaultInputTextColour);
            answerInputs[i].setError(null);
            answerInputs[i].clearFocus();

            correctAnswerTexts[i].setText("");
            correctAnswerTexts[i].setVisibility(View.GONE);
        }

        updateAttemptsText();
    }

    private void updateScoreDisplay() {
        scoreText.setText("Score: " + roundScore.getScore());
    }

    private void awardCountryPoint(int countryIndex) {
        roundScore.awardPoint(countryIndex);
        updateScoreDisplay();
    }

    private void showFlag(int index) {
        String imageName = roundCountries[index]
                .toLowerCase(Locale.ROOT)
                .replace(" ", "_");

        int imageResource = getResources().getIdentifier(
                imageName,
                "drawable",
                getPackageName()
        );

        if (imageResource == 0) {
            throw new IllegalStateException(
                    "Missing flag drawable: " + imageName
                            + ". Check the filename in res/drawable."
            );
        }

        flagImages[index].setImageResource(imageResource);

        flagImages[index].setContentDescription(
                "Flag " + (index + 1)
        );
    }

    private void checkAnswers() {
        boolean allCorrect = true;

        for (int i = 0; i < 3; i++) {

            if (correctAnswers[i]) {
                continue;
            }

            String enteredAnswer = normalise(
                    answerInputs[i].getText().toString()
            );

            String expectedAnswer = normalise(roundCountries[i]);

            answerInputs[i].setError(null);

            if (enteredAnswer.isEmpty()) {
                answerInputs[i].setError("Enter a country name");
                answerInputs[i].setTextColor(Color.RED);
                allCorrect = false;

            } else if (enteredAnswer.equals(expectedAnswer)) {
                correctAnswers[i] = true;
                awardCountryPoint(i);

                answerInputs[i].setTextColor(
                        Color.rgb(0, 128, 0)
                );

                answerInputs[i].setEnabled(false);

            } else {
                answerInputs[i].setTextColor(Color.RED);
                allCorrect = false;
            }
        }

        if (allCorrect) {
            endRound(true);
            return;
        }

        unsuccessfulSubmissions++;
        updateAttemptsText();

        if (unsuccessfulSubmissions >= MAX_ATTEMPTS) {
            endRound(false);
        }
    }

    private String normalise(String text) {
        return text.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }

    private void updateAttemptsText() {
        attemptsText.setText(
                "Unsuccessful submissions: "
                        + unsuccessfulSubmissions
                        + " / "
                        + MAX_ATTEMPTS
        );
    }

    private void endRound(boolean won) {
        roundEnded = true;
        submitButton.setText("Next");

        resultText.setVisibility(View.VISIBLE);
        resultText.setText(won ? "CORRECT!" : "WRONG!");
        resultText.setTextColor(
                won ? Color.rgb(0, 128, 0) : Color.RED
        );

        for (int i = 0; i < 3; i++) {
            answerInputs[i].setEnabled(false);
            answerInputs[i].setError(null);

            if (!won && !correctAnswers[i]) {
                correctAnswerTexts[i].setText(
                        "Correct answer: " + roundCountries[i]
                );

                correctAnswerTexts[i].setTextColor(Color.BLUE);
                correctAnswerTexts[i].setVisibility(View.VISIBLE);
            }
        }
    }

    // AI was used for syntax guidance of the onSaveInstanceState method
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArray("countries", roundCountries);
        outState.putBooleanArray("correct", correctAnswers);
        outState.putInt("unsuccessful", unsuccessfulSubmissions);
        outState.putBoolean("ended", roundEnded);

        String[] enteredAnswers = new String[3];

        for (int i = 0; i < 3; i++) {
            enteredAnswers[i] =
                    answerInputs[i].getText().toString();
        }

        outState.putStringArray("enteredAnswers", enteredAnswers);

        super.onSaveInstanceState(outState);
    }

    // AI was used for syntax guidance and debugging of the restoreRound method
    private void restoreRound(Bundle state) {

        roundScore.reset();
        updateScoreDisplay();

        String[] countries = state.getStringArray("countries");
        boolean[] correct = state.getBooleanArray("correct");
        String[] entered = state.getStringArray("enteredAnswers");

        if (countries == null || correct == null || entered == null
                || countries.length != 3
                || correct.length != 3
                || entered.length != 3) {
            startNewRound();
            return;
        }

        unsuccessfulSubmissions = state.getInt("unsuccessful");
        roundEnded = state.getBoolean("ended");

        boolean allCorrect = true;

        for (int i = 0; i < 3; i++) {
            roundCountries[i] = countries[i];
            correctAnswers[i] = correct[i];

            showFlag(i);

            answerInputs[i].setText(entered[i]);
            answerInputs[i].setEnabled(
                    !correctAnswers[i] && !roundEnded
            );

            if (correctAnswers[i]) {
                awardCountryPoint(i);
                answerInputs[i].setTextColor(
                        Color.rgb(0, 128, 0)
                );
            } else {
                allCorrect = false;

                answerInputs[i].setTextColor(
                        unsuccessfulSubmissions > 0
                                ? Color.RED
                                : defaultInputTextColour
                );
            }

            correctAnswerTexts[i].setVisibility(View.GONE);
        }

        updateAttemptsText();

        if (roundEnded) {
            endRound(allCorrect);
        } else {
            submitButton.setText("Submit");
            resultText.setVisibility(View.GONE);
        }
    }
}