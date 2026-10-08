package com.example.flag_game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.graphics.Color;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import android.content.res.ColorStateList;
import android.view.View;

public class GuessHintsPage extends AppCompatActivity {

    private ImageView flagImage;
    private EditText letterInput;
    private Button submitButton;
    private TextView hiddenNameText;
    private TextView attemptsText;
    private TextView resultText;
    private TextView answerText;
    private View[] mistakeDots;

    private final Random random = new Random();
    private final Set<Character> guessedLetters = new HashSet<>();

    private String currentCountry;
    private char[] hiddenName;
    private int currentFlagIndex = -1;
    private int incorrectGuesses;
    private boolean roundEnded;

    private static final int MAX_INCORRECT_GUESSES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_hints_page);

        flagImage = findViewById(R.id.guess_hint_flag_image_view);
        letterInput = findViewById(R.id.guess_hint_letter_input);
        submitButton = findViewById(R.id.guess_hint_submit_button);
        hiddenNameText = findViewById(R.id.guess_hint_hidden_name);
        attemptsText = findViewById(R.id.guess_hint_attempts);
        resultText = findViewById(R.id.guess_hint_result);
        answerText = findViewById(R.id.guess_hint_answer);
        mistakeDots = new View[] {
                findViewById(R.id.mistake_dot_1),
                findViewById(R.id.mistake_dot_2),
                findViewById(R.id.mistake_dot_3)
        };

        submitButton.setOnClickListener(view -> {
            if (roundEnded) {
                startNewRound();
            } else {
                checkAnswer();
            }
        });

        startNewRound();
    }

    private void startNewRound() {

        // Avoid displaying the same flag in consecutive rounds.
        int nextIndex;

        do {
            nextIndex = random.nextInt(FlagData.countries.length);
        } while (FlagData.countries.length > 1
                && nextIndex == currentFlagIndex);

        currentFlagIndex = nextIndex;

        currentCountry = FlagData.countries[currentFlagIndex]
                .toUpperCase(Locale.ROOT);

        // Keep the drawable naming convention from your original code.
        String imageName = FlagData.countries[currentFlagIndex]
                .toLowerCase(Locale.ROOT);

        int imageResource = getResources().getIdentifier(
                imageName,
                "drawable",
                getPackageName()
        );

        flagImage.setImageResource(imageResource);

        // Hide letters, but keep spaces and punctuation visible.
        hiddenName = new char[currentCountry.length()];

        for (int i = 0; i < currentCountry.length(); i++) {
            char character = currentCountry.charAt(i);

            hiddenName[i] = Character.isLetter(character)
                    ? '-'
                    : character;
        }

        incorrectGuesses = 0;
        roundEnded = false;
        guessedLetters.clear();

        hiddenNameText.setText(new String(hiddenName));
        updateAttemptsText();

        resultText.setText("");
        answerText.setText("");

        letterInput.setText("");
        letterInput.setError(null);
        letterInput.setEnabled(true);

        submitButton.setText("Submit");
    }

    private void checkAnswer() {

        String input = letterInput.getText().toString().trim();

        // Validate before processing the guess.
        if (!input.matches("[a-zA-Z]")) {
            letterInput.setError("Enter exactly one letter from A to Z");
            return;
        }

        char guessedLetter = input.toUpperCase(Locale.ROOT).charAt(0);

        // Repeated letters do not use another attempt.
        if (guessedLetters.contains(guessedLetter)) {
            letterInput.setError("You have already guessed this letter");
            return;
        }

        guessedLetters.add(guessedLetter);

        letterInput.setError(null);
        letterInput.setText("");

        boolean letterFound = false;

        // Reveal every occurrence of the guessed letter.
        for (int i = 0; i < currentCountry.length(); i++) {
            if (currentCountry.charAt(i) == guessedLetter) {
                hiddenName[i] = guessedLetter;
                letterFound = true;
            }
        }

        if (!letterFound) {
            incorrectGuesses++;
        }

        hiddenNameText.setText(new String(hiddenName));
        updateAttemptsText();

        if (currentCountry.equals(new String(hiddenName))) {
            endRound(true);
        } else if (incorrectGuesses >= MAX_INCORRECT_GUESSES) {
            endRound(false);
        }
    }

    private void updateAttemptsText() {

        for (int i = 0; i < mistakeDots.length; i++) {
            int colour = i < incorrectGuesses
                    ? Color.RED
                    : Color.BLACK;

            mistakeDots[i].setBackgroundTintList(
                    ColorStateList.valueOf(colour)
            );
        }
    }

    private void endRound(boolean won) {

        roundEnded = true;
        letterInput.setEnabled(false);
        submitButton.setText("Next");

        if (won) {
            resultText.setText("CORRECT!");
            resultText.setTextColor(Color.GREEN);
        } else {
            resultText.setText("WRONG!");
            resultText.setTextColor(Color.RED);

            answerText.setText("Correct country: " + currentCountry);
            answerText.setTextColor(Color.BLUE);
        }
    }
}