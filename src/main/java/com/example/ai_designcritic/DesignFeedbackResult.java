package com.example.ai_designcritic;

import java.util.ArrayList;
import java.util.List;

public class DesignFeedbackResult {

    // ===== SCORES =====
    public double visual;
    public double usability;
    public double creativity;
    public String summary;
    public String confidence;

    public String aiInsight;





    // ===== FINAL SCORE =====
    //public int score;
    public int score;


    // ===== FEEDBACK =====
    public String feedbackText;




    // ===== SUGGESTIONS =====
    public List<String> suggestions = new ArrayList<>();
}
