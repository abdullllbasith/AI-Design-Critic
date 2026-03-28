package com.example.ai_designcritic;

import java.util.ArrayList;
import java.util.List;

public class DesignIntelligenceEngine {

    public static DesignAnalysisResult analyze(VisionCleanData data) {

        DesignAnalysisResult result = new DesignAnalysisResult();

        StringBuilder feedback = new StringBuilder();

        // =========================
        // 🎨 COLOR ANALYSIS
        // =========================
        int colorCount = data.dominantHexColors.size();
        result.dominantColors.addAll(data.dominantHexColors);

        if (colorCount >= 5) {
            feedback.append("Color Usage:\n");
            feedback.append("A rich color palette is present, suggesting strong visual diversity.\n\n");
            result.visualScore += 0.35;
        } else if (colorCount >= 3) {
            feedback.append("Color Usage:\n");
            feedback.append("A balanced color scheme is used with moderate variety.\n\n");
            result.visualScore += 0.28;
        } else {
            feedback.append("Color Usage:\n");
            feedback.append("Limited color usage detected. Design may feel flat.\n\n");
            result.visualScore += 0.18;
            result.keySuggestions.add("Introduce accent colors to improve visual interest");
        }

        // =========================
        // 🅰️ TEXT / TYPOGRAPHY
        // =========================
        if (data.detectedWordCount > 20) {
            feedback.append("Typography:\n");
            feedback.append("Text-heavy design detected. Ensure proper hierarchy and spacing.\n\n");
            result.usabilityScore += 0.32;
        } else if (data.detectedWordCount > 5) {
            feedback.append("Typography:\n");
            feedback.append("Moderate text presence. Readability is acceptable.\n\n");
            result.usabilityScore += 0.26;
        } else {
            feedback.append("Typography:\n");
            feedback.append("Minimal text detected. Design relies mainly on visuals.\n\n");
            result.usabilityScore += 0.20;
            result.keySuggestions.add("Consider adding guiding text for clarity");
        }

        // =========================
        // 📐 LAYOUT & STRUCTURE
        // =========================
        if (data.objectCount >= 6) {
            feedback.append("Layout & Structure:\n");
            feedback.append("Multiple visual elements detected, suggesting a complex layout.\n\n");
            result.creativityScore += 0.30;
        } else if (data.objectCount >= 3) {
            feedback.append("Layout & Structure:\n");
            feedback.append("Clean and minimal layout detected.\n\n");
            result.creativityScore += 0.24;
        } else {
            feedback.append("Layout & Structure:\n");
            feedback.append("Very few structural elements detected.\n\n");
            result.creativityScore += 0.18;
            result.keySuggestions.add("Add supporting visual elements to enrich composition");
        }

        // =========================
        // 🔢 NORMALIZE SCORES
        // =========================
        result.visualScore = clamp(result.visualScore);
        result.usabilityScore = clamp(result.usabilityScore);
        result.creativityScore = clamp(result.creativityScore);

        // =========================
        // 🧠 FINAL SUMMARY
        // =========================
        feedback.append("Overall Insight:\n");
        feedback.append("The design shows a ");
        feedback.append(result.visualScore > 0.7 ? "strong visual presence" : "moderate visual quality");
        feedback.append(" with ");
        feedback.append(result.usabilityScore > 0.7 ? "good usability." : "room for usability improvements.");
        feedback.append("\n");

        result.detailedFeedback = feedback.toString();

        // =========================
        // 💡 GLOBAL SUGGESTIONS
        // =========================
        if (result.keySuggestions.isEmpty()) {
            result.keySuggestions.add("Design is well-balanced. Minor refinements recommended");
        }

        return result;
    }

    private static double clamp(double v) {
        return Math.min(1.0, Math.max(0.0, v));
    }
}
