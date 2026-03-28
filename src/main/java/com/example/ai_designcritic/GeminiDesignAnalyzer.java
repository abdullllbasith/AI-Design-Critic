package com.example.ai_designcritic;

import com.google.genai.Client;
import com.google.genai.types.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class GeminiDesignAnalyzer {

    private static Client client;

    private static Client getClient() {
        if (client == null) {
            client = new Client();
        }
        return client;
    }

    public static String analyzeDesign(File imageFile, VisionCleanData visionData) throws Exception {

        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

        String prompt = """
You are a professional UI/UX design reviewer.

Rules:
- Use clear section headings
- Use bullet points only (no long paragraphs)
- Do NOT use markdown symbols (*, #, **)
- Keep sentences short and professional
- Do NOT include scores or percentages
- Do NOT include system analysis

Analyze the design under these sections:

Design Intent:
- What the design is trying to communicate

Color Harmony:
- Palette effectiveness
- Contrast and readability

Typography:
- Font choice
- Hierarchy and readability

Layout & Spacing:
- Structure and alignment
- Visual balance

UX Strengths:
- 3 bullet points

UX Weaknesses:
- 3 bullet points

Improvement Suggestions:
- 3 clear actionable bullets
""";


        Content content = Content.builder()
                .role("user")
                .parts(List.of(
                        Part.builder().text(prompt).build(),
                        Part.builder()
                                .inlineData(
                                        Blob.builder()
                                                .mimeType("image/png")
                                                .data(imageBytes)
                                                .build()
                                )
                                .build()
                ))
                .build();

        GenerateContentResponse response =
                getClient()
                        .models
                        .generateContent(
                                "gemini-3-pro-preview",
                                List.of(content),
                                null
                        );

        return response.text();
    }
}
