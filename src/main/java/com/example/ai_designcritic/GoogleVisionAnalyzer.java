package com.example.ai_designcritic;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;

import java.io.File;
import java.nio.file.Files;

public class GoogleVisionAnalyzer {

    public static VisionCleanData analyze(File imageFile) throws Exception {

        VisionCleanData cleanData = new VisionCleanData();

        ByteString imgBytes = ByteString.readFrom(
                Files.newInputStream(imageFile.toPath())
        );

        Image image = Image.newBuilder().setContent(imgBytes).build();

        Feature labelFeature = Feature.newBuilder()
                .setType(Feature.Type.LABEL_DETECTION)
                .build();

        Feature textFeature = Feature.newBuilder()
                .setType(Feature.Type.TEXT_DETECTION)
                .build();

        Feature objectFeature = Feature.newBuilder()
                .setType(Feature.Type.OBJECT_LOCALIZATION)
                .build();

        Feature colorFeature = Feature.newBuilder()
                .setType(Feature.Type.IMAGE_PROPERTIES)
                .build();

        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder()
                        .addFeatures(labelFeature)
                        .addFeatures(textFeature)
                        .addFeatures(objectFeature)
                        .addFeatures(colorFeature)
                        .setImage(image)
                        .build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {

            BatchAnnotateImagesResponse response =
                    client.batchAnnotateImages(
                            java.util.List.of(request)
                    );

            AnnotateImageResponse res = response.getResponses(0);

            // -------- COLORS --------
            if (res.hasImagePropertiesAnnotation()) {
                res.getImagePropertiesAnnotation()
                        .getDominantColors()
                        .getColorsList()
                        .stream()
                        .limit(5)
                        .forEach(color -> {
                            int r = (int) (color.getColor().getRed());
                            int g = (int) (color.getColor().getGreen());
                            int b = (int) (color.getColor().getBlue());

                            String hex = String.format("#%02X%02X%02X", r, g, b);
                            cleanData.dominantHexColors.add(hex);
                        });
            }

            // -------- TEXT --------
            if (!res.getTextAnnotationsList().isEmpty()) {
                String fullText = res.getTextAnnotations(0).getDescription();
                cleanData.detectedWordCount = fullText.split("\\s+").length;
            }

            // -------- OBJECTS --------
            cleanData.objectCount =
                    res.getLocalizedObjectAnnotationsCount();
        }

        return cleanData;
    }
}
