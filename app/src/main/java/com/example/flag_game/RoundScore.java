package com.example.flag_game;

public class RoundScore {

    private final boolean[] awarded;
    private int score;

    public RoundScore(int countriesInRound) {
        if (countriesInRound < 1 || countriesInRound > 3) {
            throw new IllegalArgumentException(
                    "A round must contain between 1 and 3 countries."
            );
        }

        awarded = new boolean[countriesInRound];
    }

    public boolean awardPoint(int countryIndex) {
        if (countryIndex < 0 || countryIndex >= awarded.length) {
            throw new IllegalArgumentException("Invalid country index.");
        }

        if (awarded[countryIndex]) {
            return false;
        }

        awarded[countryIndex] = true;
        score++;

        return true;
    }

    public int getScore() {
        return score;
    }

    public void reset() {
        score = 0;

        for (int i = 0; i < awarded.length; i++) {
            awarded[i] = false;
        }
    }
}