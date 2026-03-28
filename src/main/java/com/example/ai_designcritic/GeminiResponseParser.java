package com.example.ai_designcritic;

import org.json.JSONArray;
import org.json.JSONObject;

public class GeminiResponseParser {

    public static String extractText(String rawJson) {
        try {
            JSONObject root = new JSONObject(rawJson);

            JSONArray candidates = root.getJSONArray("candidates");
            JSONObject content = candidates.getJSONObject(0)
                    .getJSONObject("content");

            JSONArray parts = content.getJSONArray("parts");

            StringBuilder text = new StringBuilder();
            for (int i = 0; i < parts.length(); i++) {
                if (parts.getJSONObject(i).has("text")) {
                    text.append(parts.getJSONObject(i).getString("text"))
                            .append("\n");
                }
            }

            return text.toString().trim();

        } catch (Exception e) {
            return "AI feedback could not be parsed.";
        }
    }
}
