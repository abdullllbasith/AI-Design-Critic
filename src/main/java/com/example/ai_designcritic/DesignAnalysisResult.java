package com.example.ai_designcritic;

import java.util.ArrayList;
import java.util.List;

public class DesignAnalysisResult {

    // ===== SCORES (0.0 - 1.0) =====
    public double visualScore;
    public double usabilityScore;
    public double creativityScore;

    // ===== FEEDBACK TEXT =====
    public String detailedFeedback;

    // ===== SUGGESTIONS =====
    public List<String> keySuggestions = new ArrayList<>();

    // ===== COLORS (HEX) =====
    public List<String> dominantColors = new ArrayList<>();
}
