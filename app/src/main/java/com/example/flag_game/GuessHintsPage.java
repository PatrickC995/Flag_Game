package com.example.flag_game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

public class GuessHintsPage extends AppCompatActivity {

    ImageView flagImage;
    Button submitButton;

    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_hints_page);

        flagImage = findViewById(R.id.guess_hint_flag_image_view);

        displayRandomFlag();
    }

    private void displayRandomFlag() {

        int currentFlagIndex = random.nextInt(FlagData.countries.length);

        String country = FlagData.countries[currentFlagIndex];

        String imageName = country.toLowerCase();

        int imageResource = getResources().getIdentifier(
                imageName,
                "drawable",
                getPackageName()
        );

        flagImage.setImageResource(imageResource);

    }
}