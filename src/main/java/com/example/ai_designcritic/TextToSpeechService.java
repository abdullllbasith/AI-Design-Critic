package com.example.ai_designcritic;

import com.google.cloud.texttospeech.v1.*;

import java.io.File;
import java.io.FileOutputStream;

public class TextToSpeechService {

    public static File speak(String text) throws Exception {

        // Output file
        File outputFile = new File("ai_feedback.mp3");

        try (TextToSpeechClient client = TextToSpeechClient.create()) {

            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            VoiceSelectionParams voice =
                    VoiceSelectionParams.newBuilder()
                            .setLanguageCode("en-US")
                            .setSsmlGender(SsmlVoiceGender.FEMALE)
                            .build();


            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            SynthesizeSpeechResponse response =
                    client.synthesizeSpeech(input, voice, audioConfig);

            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                out.write(response.getAudioContent().toByteArray());
            }
        }

        // 🔴 THIS IS THE KEY FIX
        return outputFile;
    }
}
