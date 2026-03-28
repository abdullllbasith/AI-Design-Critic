package com.example.ai_designcritic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DesignAnalyzer {

    public static class Result {
        public String feedback;
        public double visual;
        public double usability;
        public double creativity;
        public List<String> suggestions;
        public int score;
    }

    private String rgbToHex(int r, int g, int b) {
        return String.format("#%02X%02X%02X", r, g, b);
    }


    public Result analyze(File imageFile) {

        Result result = new Result();
        result.suggestions = new ArrayList<>();

        try {
            // 🔹 Call Google Vision
            VisionResult vision = VisionAnalyzer.analyzeDesign(imageFile);

            // 🔹 Build feedback
            StringBuilder feedback = new StringBuilder();

            // ===== COLORS =====
            if (!vision.colors.isEmpty()) {
                feedback.append("Color Analysis:\n");
                feedback.append("Dominant colors detected: ")
                        .append(String.join(", ", vision.colors))
                        .append("\n\n");

                result.visual = 0.75;
                result.suggestions.add("Ensure color contrast meets accessibility standards");
            }



            // ===== TEXT =====
            if (!vision.detectedText.isBlank()) {
                feedback.append("Typography Analysis:\n");
                feedback.append("Detected text content suggests readable typography.\n\n");

                result.usability = 0.70;
                result.suggestions.add("Improve text hierarchy using font size and weight");
            } else {
                result.usability = 0.55;
                result.suggestions.add("Consider adding clear text hierarchy");
            }

            // ===== LABELS =====
            if (!vision.labels.isEmpty()) {
                feedback.append("Layout & Design:\n");
                feedback.append("Design appears to be: ")
                        .append(String.join(", ", vision.labels))
                        .append("\n\n");

                result.creativity = 0.80;
                result.suggestions.add("Explore more creative spacing and visual flow");
            }

            // ===== FINAL SCORE =====
            result.score = (int) (
                    (result.visual + result.usability + result.creativity) / 3 * 100
            );

            result.feedback = feedback.toString();

        } catch (Exception e) {
            result.feedback = "AI analysis failed:\n" + e.getMessage();
            result.visual = result.usability = result.creativity = 0;
            result.score = 0;
        }

        return result;
    }
}