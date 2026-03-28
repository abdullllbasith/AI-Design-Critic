package com.example.ai_designcritic;


import java.io.File;

public class DesignAnalysisService {

    public static DesignFeedbackResult analyze(File imageFile) throws Exception {

        // 1️⃣ Run Google Vision
        VisionCleanData data = GoogleVisionAnalyzer.analyze(imageFile);

        // 2️⃣ Convert raw vision → design feedback
        DesignFeedbackResult result = new DesignFeedbackResult();

        StringBuilder feedback = new StringBuilder();


        /* ---------- VISUAL SCORE ---------- */
        int colorCount = data.dominantHexColors.size();

        if (colorCount >= 5) {
            result.visual = 0.80;
            feedback.append
                    ("Strong color palette with good variety.\n\n");
        } else if (colorCount >= 3) {
            result.visual = 0.65;
            feedback.append
                    ("Moderate color variety detected.\n\n");
        } else {
            result.visual = 0.45;
            feedback.append
                    ("Limited color usage — design may feel flat.\n\n");
        }

        /* ---------- USABILITY SCORE ---------- */
        if (data.detectedWordCount > 15) {
            result.usability = 0.78;
            feedback.append
                    ("Text elements are clearly present and readable.\n\n");
        } else if (data.detectedWordCount > 5) {
            result.usability = 0.60;
            feedback.append
                    ("Some text detected — hierarchy can be improved.\n\n");
        } else {
            result.usability = 0.42;
            feedback.append
                    ("Very little readable text detected.\n\n");
        }

        /* ---------- CREATIVITY SCORE ---------- */
        if (data.objectCount >= 4) {
            result.creativity = 0.82;
            feedback.append
                    ("Multiple visual elements indicate creative composition.\n\n");
        } else if (data.objectCount >= 2) {
            result.creativity = 0.62;
            feedback.append
                    ("Basic creative elements detected.\n\n");
        } else {
            result.creativity = 0.40;
            feedback.append
                    ("Design appears visually simple.\n\n");
        }

        /* ---------- FINAL SCORE ---------- */
        double avg = (result.visual + result.usability + result.creativity) / 3.0;
        result.score = (int) (avg * 100);


        /* ---------- KEY SUGGESTIONS ---------- */
        if (colorCount < 4) {
            result.suggestions.add("Add more supporting colors for visual depth");
        }

        if (data.detectedWordCount < 10) {
            result.suggestions.add("Improve text hierarchy and readability");
        }

        if (data.objectCount < 3) {
            result.suggestions.add("Introduce more visual elements for creativity");
        }

        if (result.suggestions.isEmpty()) {
            result.suggestions.add("Design is well-balanced — consider minor refinements");
        }

        return result;
    }
}
