package com.example.ai_designcritic;

import java.util.ArrayList;
import java.util.List;

public class VisionAnalysisResult {

    // =========================
    // TEXT DATA
    // =========================
    public int textBlockCount;
    public boolean hasText;

    // =========================
    // COLOR DATA
    // =========================
    public List<String> dominantHexColors = new ArrayList<>();

    // =========================
    // OBJECT DATA
    // =========================
    public int objectCount;

    // =========================
    // IMAGE META
    // =========================
    public int imageWidth;
    public int imageHeight;

}
