package com.example.ai_designcritic;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class AiAudioPlayer {

    private static MediaPlayer mediaPlayer;

    public static void play(File file) {

        stop(); // stop previous audio if any

        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }
}


//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
//
//import java.io.File;
//
//public class AiAudioPlayer {
//
//    private static MediaPlayer mediaPlayer;
//
//    public static void play(File file) {
//
//        stop(); // stop previous audio if any
//
//        Media media = new Media(file.toURI().toString());
//        mediaPlayer = new MediaPlayer(media);
//        mediaPlayer.play();
//    }
//
//    public static void stop() {
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.dispose();
//            mediaPlayer = null;
//        }
//    }
//}
