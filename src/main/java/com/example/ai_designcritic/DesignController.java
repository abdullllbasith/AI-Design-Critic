package com.example.ai_designcritic;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;



import java.io.File;
import java.text.DecimalFormat;

public class DesignController {

    // ===== IMAGE SECTION =====
    @FXML private ImageView imageView;
    @FXML private VBox placeholderOverlay;
    @FXML private Label fileNameLabel;
    @FXML private Label fileSizeLabel;
    @FXML private Label dimensionsLabel;
    @FXML private Label formatLabel;
    @FXML private Button analyzeBtn;

    // ===== FEEDBACK SECTION =====
    @FXML private TextArea resultArea;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label scoreLabel;

    // ===== SCORE BARS =====
    @FXML private ProgressBar visualScoreBar;
    @FXML private ProgressBar usabilityScoreBar;
    @FXML private ProgressBar creativityScoreBar;

    @FXML private Label visualScoreLabel;
    @FXML private Label usabilityScoreLabel;
    @FXML private Label creativityScoreLabel;

    // ===== SUGGESTIONS =====
    @FXML private ListView<String> suggestionsList;

    // ===== STATUS =====
    @FXML private Label statusLabel;

    private File selectedImage;
    private String lastAiFeedback = "";

    // ==========================
    // IMAGE UPLOAD
    // ==========================
    @FXML
    private void uploadImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Design Image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files", "*.png", "*.jpg", "*.jpeg", "*.svg")
        );

        File file = chooser.showOpenDialog(null);
        if (file == null) return;

        selectedImage = file;

        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);
        placeholderOverlay.setVisible(false);

        analyzeBtn.setDisable(false);
        statusLabel.setText("Image loaded successfully");

        updateFileStats(file, image);
        clearPreviousResults();
    }

    // ==========================
    // FILE STATS
    // ==========================
    private void updateFileStats(File file, Image image) {
        fileNameLabel.setText(file.getName());

        double sizeKB = file.length() / 1024.0;
        fileSizeLabel.setText(new DecimalFormat("#.##").format(sizeKB) + " KB");

        dimensionsLabel.setText(
                (int) image.getWidth() + " x " + (int) image.getHeight()
        );

        String name = file.getName().toLowerCase();
        formatLabel.setText(name.substring(name.lastIndexOf('.') + 1).toUpperCase());
    }

    // ==========================
    // ANALYZE DESIGN (MOCK DATA)
    // ==========================
    @FXML
    private void analyzeDesign() {

        if (selectedImage == null) return;

        loadingIndicator.setVisible(true);
        statusLabel.setText("Analyzing design with AI...");

        new Thread(() -> {
            try {
                VisionCleanData visionData =
                        GoogleVisionAnalyzer.analyze(selectedImage);

                DesignFeedbackResult result =
                        DesignFeedbackEngine.generate(visionData);

                String rawGemini =
                        GeminiDesignAnalyzer.analyzeDesign(selectedImage, visionData);

                String geminiText = cleanGeminiText(rawGemini);
                lastAiFeedback = geminiText;


                Platform.runLater(() -> {

                    // ===== CLEAN AI OUTPUT =====
                    result.aiInsight = geminiText;
                    result.summary = geminiText.split("\n")[0];

                    typeWriterEffect(resultArea,
                            "=== AI DESIGN REVIEW ===\n\n" +
                                    geminiText + "\n\n"
//                                    "=== SYSTEM ANALYSIS ===\n" +
//                                    "Visual: " + (int)(result.visual * 100) + "%\n" +
//                                    "Usability: " + (int)(result.usability * 100) + "%\n" +
//                                    "Creativity: " + (int)(result.creativity * 100) + "%\n" +
//                                    "Overall: " + result.score + "%\n"
                    );

                    // ===== UPDATE SCORES =====
                    scoreLabel.setText(result.score + "%");   // ✅ FIXED
                    setScores(result.visual, result.usability, result.creativity);

                    suggestionsList.getItems().setAll(result.suggestions);

                    statusLabel.setText("Analysis completed");
                    loadingIndicator.setVisible(false);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    resultArea.setText("AI analysis failed:\n" + e.getMessage());
                    loadingIndicator.setVisible(false);
                });
            }
        }).start();
    }




    // ==========================
    // SCORE HANDLING
    // ==========================
    private void setScores(double visual, double usability, double creativity) {
        visualScoreBar.setProgress(visual);
        usabilityScoreBar.setProgress(usability);
        creativityScoreBar.setProgress(creativity);

        visualScoreLabel.setText((int)(visual * 100) + "%");
        usabilityScoreLabel.setText((int)(usability * 100) + "%");
        creativityScoreLabel.setText((int)(creativity * 100) + "%");
    }

    // ==========================
    // CLEAR
    // ==========================
    @FXML
    private void clearAll() {
        imageView.setImage(null);
        placeholderOverlay.setVisible(true);
        selectedImage = null;

        fileNameLabel.setText("No file selected");
        fileSizeLabel.setText("0 KB");
        dimensionsLabel.setText("0 x 0");
        formatLabel.setText("N/A");

        analyzeBtn.setDisable(true);
        clearPreviousResults();

        statusLabel.setText("Cleared");
    }

    private void clearPreviousResults() {
        resultArea.clear();
        suggestionsList.getItems().clear();
        scoreLabel.setText("--");

        setScores(0, 0, 0);
    }

    // ==========================
    // FEEDBACK ACTIONS
    // ==========================
    @FXML
    private void copyFeedback() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(resultArea.getText());
        clipboard.setContent(content);

        statusLabel.setText("Feedback copied to clipboard");
    }

    @FXML
    private void saveReport() {
        statusLabel.setText("Save report feature coming soon");
    }

    @FXML
    private void readAloud() {
        statusLabel.setText("Text-to-speech feature coming soon");
    }

    @FXML
    private void showExampleFeedback() {
        resultArea.setText("""
                Example Feedback:
                
                • Strong visual hierarchy
                • Excellent color balance
                • Typography is modern and readable
                • Layout feels professional
                
                Overall Rating: 9.2 / 10
                """);
        statusLabel.setText("Example feedback loaded");
    }

    @FXML
    private void clearFeedback() {
        resultArea.clear();
        suggestionsList.getItems().clear();
        scoreLabel.setText("--");
        setScores(0, 0, 0);
        statusLabel.setText("Feedback cleared");
    }

    @FXML
    private BorderPane rootPane;


    @FXML
    private ToggleGroup modeGroup;

    @FXML
    private ToggleButton themeToggle;


    @FXML
    private void testAudio() {
        try {
            String url = getClass()
                    .getResource("/com/example/ai_designcritic/test1.mp3")
                    .toExternalForm();

            Media media = new Media(url);
            MediaPlayer player = new MediaPlayer(media);
            player.play();

            statusLabel.setText("Playing test audio...");
        } catch (Exception e) {
            statusLabel.setText("Audio error: " + e.getMessage());
            e.printStackTrace();
        }
    }

//    @FXML
//    private void testTTS() {
//        try {
//            File audio = TextToSpeechService.speak(
//                    "Hello AbdulBasit. This is your AI Design Critic speaking."
//            );
//
//            //AiAudioPlayer.play(audio);
//            System.out.println("READING: " + lastAiFeedback);
//            File audio1 = TextToSpeechService.speak(lastAiFeedback);
//            AiAudioPlayer.play(audio1);
//
//
//            statusLabel.setText("Playing AI voice...");
//        } catch (Exception e) {
//            e.printStackTrace();
//            statusLabel.setText("TTS Error");
//        }
//    }



    @FXML
    private void testTTS() {

        if (lastAiFeedback == null || lastAiFeedback.isBlank()) {
            statusLabel.setText("No AI feedback to read yet");
            return;
        }

        statusLabel.setText("Reading AI feedback...");

        new Thread(() -> {
            try {
                File audio = TextToSpeechService.speak(lastAiFeedback);
                AiAudioPlayer.play(audio);
            } catch (Exception e) {
                e.printStackTrace();

                Platform.runLater(() ->
                        statusLabel.setText("TTS error: " + e.getMessage())
                );
            }
        }).start();
    }

    @FXML
    private void stopTTS() {
        AiAudioPlayer.stop();
        statusLabel.setText("Audio stopped");
    }

    @FXML
    private void exportToPdf() {

        if (resultArea.getText().isBlank()) {
            statusLabel.setText("Nothing to export");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save AI Design Report");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        chooser.setInitialFileName("AI_Design_Report.pdf");

        File file = chooser.showSaveDialog(rootPane.getScene().getWindow());

        if (file == null) {
            statusLabel.setText("Export cancelled");
            return;
        }

        try {
            PdfExportService.export(file, resultArea.getText());
            statusLabel.setText("PDF saved successfully");
        } catch (Exception e) {
            statusLabel.setText("PDF export failed");
            e.printStackTrace();
        }
    }




    @FXML
    private void initialize() {
        // Theme toggle
        themeToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                rootPane.getStyleClass().add("dark-mode");
                themeToggle.setText("☀");
            } else {
                rootPane.getStyleClass().remove("dark-mode");
                themeToggle.setText("🌙");
            }
        });

        // Image view listener
        imageView.imageProperty().addListener((obs, oldImg, newImg) -> {
            if (newImg != null) {
                placeholderOverlay.setVisible(false);
                analyzeBtn.setDisable(false);
                updateImageInfo(newImg);
            } else {
                placeholderOverlay.setVisible(true);
                analyzeBtn.setDisable(true);
            }
        });

        // Initialize suggestions list
        suggestionsList.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText("• " + item);
                    setStyle("-fx-text-fill: -text; -fx-font-size: 13px;");
                }
            }
        });
    }

    private void updateImageInfo(Image image) {
        if (image != null) {
            dimensionsLabel.setText(String.format("%.0f x %.0f",
                    image.getWidth(), image.getHeight()));
            // Add file size and format logic here based on your upload implementation
        }




    }



    private void resetScores() {
        scoreLabel.setText("--");
        visualScoreBar.setProgress(0);
        visualScoreLabel.setText("0%");
        usabilityScoreBar.setProgress(0);
        usabilityScoreLabel.setText("0%");
        creativityScoreBar.setProgress(0);
        creativityScoreLabel.setText("0%");
    }

    private String cleanGeminiText(String text) {
        if (text == null) return "";

        return text
                .replace("*", "")
                .replace("#", "")
                .replace("•", "-")
                .replaceAll("(?m)^\\s+", "")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private void typeWriterEffect(TextArea textArea, String fullText) {
        textArea.clear();

        Timeline timeline = new Timeline();
        final int[] index = {0};

        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(15), // typing speed (lower = faster)
                event -> {
                    if (index[0] < fullText.length()) {
                        textArea.appendText(
                                String.valueOf(fullText.charAt(index[0]))
                        );
                        index[0]++;
                    }
                }
        );

        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(fullText.length());
        timeline.play();
    }





}
