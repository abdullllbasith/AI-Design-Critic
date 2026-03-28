package com.example.ai_designcritic;

import java.util.ArrayList;
import java.util.List;

public class DesignFeedbackEngine {

    public static DesignFeedbackResult generate(VisionCleanData data) {

        DesignFeedbackResult result = new DesignFeedbackResult();
        StringBuilder feedback = new StringBuilder();

        // ================= COLORS =================
        int colorCount = data.dominantHexColors.size();
        feedback.append("Detected Colors:\n");
        data.dominantHexColors.forEach(c ->
                feedback.append("• ").append(c).append("\n"));
        feedback.append("\n");

        result.visual = Math.max(0.4, 1.0 - (colorCount * 0.07));

        // ================= TEXT =================
        int words = data.detectedWordCount;
        if (words == 0) {
            result.usability = 0.45;
        } else {
            result.usability = Math.min(0.9, 0.5 + (words / 120.0));
        }

        // ================= OBJECTS =================
        int objects = data.objectCount;
        if (objects == 0) {
            result.creativity = 0.45;
        } else {
            result.creativity = Math.min(0.9, 0.5 + (objects * 0.08));
        }

        // ================= FEEDBACK TEXT =================
        feedback.append("Color Harmony:\n");
        feedback.append(
                tone(result.visual,
                        "The color palette feels refined and visually cohesive.\n\n",
                        "The colors work reasonably well but could benefit from better balance.\n\n",
                        "The palette feels busy and may overwhelm the viewer.\n\n"
                )
        );

        feedback.append("Typography:\n");
        feedback.append(
                tone(result.usability,
                        "Text is clear, readable, and supports the design purpose effectively.\n\n",
                        "Text is readable, though hierarchy and emphasis could be improved.\n\n",
                        "Text clarity is weak and may confuse users.\n\n"
                )
        );

        feedback.append("Layout & Structure:\n");
        feedback.append(
                tone(result.creativity,
                        "The layout feels engaging and visually balanced.\n\n",
                        "The structure is acceptable but lacks visual impact.\n\n",
                        "The layout feels sparse or underdeveloped.\n\n"
                )
        );

        // ================= SUGGESTIONS =================
        if (result.visual < 0.65)
            result.suggestions.add("Simplify the color palette for better visual clarity");

        if (result.usability < 0.65)
            result.suggestions.add("Improve text hierarchy and readability");

        if (result.creativity < 0.65)
            result.suggestions.add("Enhance layout structure for balance");

        // ================= DESIGN INTENT =================
        String intent;
        if (data.detectedWordCount > 80 && data.objectCount <= 3) {
            intent = "Informational or Presentation Design";
        } else if (data.detectedWordCount > 30 && data.objectCount >= 4) {
            intent = "Marketing or Promotional Design";
        } else if (data.detectedWordCount <= 15 && data.objectCount >= 5) {
            intent = "UI or App Interface Design";
        } else {
            intent = "Minimal or Decorative Visual Design";
        }

        // ================= FINAL SCORE =================
        double avg = (result.visual + result.usability + result.creativity) / 3.0;
        result.score = (int) (avg * 100);

        // ================= CONFIDENCE =================
        double confidenceScore = calculateConfidence(data);
        result.confidence = confidenceLabel(confidenceScore);

        // ================= SUMMARY =================
        result.summary =
                "This appears to be a " + intent + ". " +
                        "The design demonstrates " +
                        tone(avg,
                                "strong visual coherence and thoughtful structure.",
                                "a balanced foundation with room for refinement.",
                                "fundamental design issues that impact clarity."
                        ) +
                        " Overall usability is " +
                        tone(result.usability,
                                "high and user-friendly.",
                                "acceptable but could be improved.",
                                "likely to confuse users."
                        );

        result.feedbackText = feedback.toString();
        return result;
    }

    // ================= HELPERS =================

    private static String tone(double score, String good, String average, String poor) {
        if (score >= 0.75) return good;
        if (score >= 0.6) return average;
        return poor;
    }

    public static String confidenceLabel(double confidenceScore) {
        if (confidenceScore >= 0.85) return "Very High";
        if (confidenceScore >= 0.70) return "High";
        if (confidenceScore >= 0.55) return "Medium";
        return "Low";
    }

    private static double calculateConfidence(VisionCleanData data) {
        double colorFactor  = Math.min(1.0, data.dominantHexColors.size() / 6.0);
        double textFactor   = Math.min(1.0, data.detectedWordCount / 50.0);
        double objectFactor = Math.min(1.0, data.objectCount / 5.0);

        return (colorFactor * 0.4)
                + (textFactor * 0.35)
                + (objectFactor * 0.25);
    }


}
