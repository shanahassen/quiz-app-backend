package com.example.quiz_app.model;

public enum RatingLevel {

    FOUNDATION("Foundation"),
    DEVELOPING("Developing"),
    PROFICIENT("Proficient"),
    ADVANCED("Advanced"),
    EXPERT("Expert");

    private final String displayName;

    RatingLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RatingLevel fromPercentage(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException(
                    "Percentage must be between 0 and 100.");
        }

        if (percentage < 50) {
            return FOUNDATION;
        } else if (percentage < 65) {
            return DEVELOPING;
        } else if (percentage < 80) {
            return PROFICIENT;
        } else if (percentage < 90) {
            return ADVANCED;
        }

        return EXPERT;
    }
}