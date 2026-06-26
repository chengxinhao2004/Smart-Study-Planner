package controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class UrgencyCalculator {

    public static double calculateScore(String urgencyLevel, LocalDateTime dueDate) {
        double baseScore = 0.0;
        double timeMultiplier = 0.0;

        // 1. Determine Base Score from the User's Dropdown Selection
        switch (urgencyLevel) {
            case "High (Due Tomorrow!)":
                baseScore = 60.0;
                break;
            case "Medium (3-5 Days)":
                baseScore = 40.0;
                break;
            case "Low (1-2 Weeks)":
                baseScore = 20.0;
                break;
            default:
                baseScore = 10.0;
        }

        // 2. Calculate the Time Multiplier
        LocalDateTime now = LocalDateTime.now();
        long daysRemaining = ChronoUnit.DAYS.between(now, dueDate);

        if (daysRemaining <= 0) {
            timeMultiplier = 40.0; // Overdue or due today is maximum panic
        } else if (daysRemaining <= 3) {
            timeMultiplier = 30.0;
        } else if (daysRemaining <= 7) {
            timeMultiplier = 15.0;
        } else {
            timeMultiplier = 5.0; // Plenty of time
        }

        // 3. Calculate Final Score (Capped at 100)
        double finalScore = baseScore + timeMultiplier;
        if (finalScore > 100.0) {
            finalScore = 100.0;
        }

        return finalScore;
    }
}