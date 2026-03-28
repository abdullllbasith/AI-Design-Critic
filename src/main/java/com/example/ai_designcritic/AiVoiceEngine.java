package com.example.ai_designcritic;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AiVoiceEngine {

    private static final String TTS_ENDPOINT =
            "https://texttospeech.googleapis.com/v1/text:synthesize?key="
                    + GeminiConfig.API_KEY;

    public static File synthesize(String text) throws Exception {

        String jsonBody = """
        {
          "input": { "text": "%s" },
          "voice": {
            "languageCode": "en-US",
            "name": "en-US-Studio-O"
          },
          "audioConfig": {
            "audioEncoding": "MP3"
          }
        }
        """.formatted(escape(text));

        URL url = new URL(TTS_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        conn.getOutputStream().write(jsonBody.getBytes());

        String response = new String(conn.getInputStream().readAllBytes());

        // Extract base64 audio
        String audioBase64 =
                response.split("\"audioContent\": \"")[1].split("\"")[0];

        byte[] audioBytes = java.util.Base64.getDecoder().decode(audioBase64);

        File audioFile = File.createTempFile("ai_voice_", ".mp3");
        try (FileOutputStream fos = new FileOutputStream(audioFile)) {
            fos.write(audioBytes);
        }

        return audioFile;
    }

    private static String escape(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ");
    }
}
