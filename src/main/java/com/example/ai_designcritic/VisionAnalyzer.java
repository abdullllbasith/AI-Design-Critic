package com.example.ai_designcritic;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class VisionAnalyzer {

    /**
     * Analyze design image using Google Cloud Vision
     */
    public static VisionResult analyzeDesign(File imageFile) throws Exception {

        // Read image bytes
        ByteString imgBytes;
        try (FileInputStream fis = new FileInputStream(imageFile)) {
            imgBytes = ByteString.readFrom(fis);
        }

        // Build image
        Image image = Image.newBuilder()
                .setContent(imgBytes)
                .build();

        // Features we want
        List<Feature> features = new ArrayList<>();
        features.add(Feature.newBuilder()
                .setType(Feature.Type.LABEL_DETECTION)
                .build());
        features.add(Feature.newBuilder()
                .setType(Feature.Type.TEXT_DETECTION)
                .build());
        features.add(Feature.newBuilder()
                .setType(Feature.Type.IMAGE_PROPERTIES)
                .build());

        // Build request
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .setImage(image)
                .addAllFeatures(features)
                .build();

        List<AnnotateImageRequest> requests = new ArrayList<>();
        requests.add(request);

        // Vision client (uses GOOGLE_APPLICATION_CREDENTIALS)
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

            BatchAnnotateImagesResponse response =
                    vision.batchAnnotateImages(requests);

            AnnotateImageResponse res = response.getResponses(0);

            if (res.hasError()) {
                throw new RuntimeException("Vision API Error: "
                        + res.getError().getMessage());
            }

            return parseResult(res);
        }
    }

    /**
     * Convert Vision response into our custom result
     */
    private static VisionResult parseResult(AnnotateImageResponse res) {

        VisionResult result = new VisionResult();

        // Labels
        res.getLabelAnnotationsList()
                .forEach(label ->
                        result.labels.add(label.getDescription())
                );

        // Detected text
        if (!res.getTextAnnotationsList().isEmpty()) {
            result.detectedText =
                    res.getTextAnnotationsList().get(0).getDescription();
        }

        // Dominant colors
        if (res.hasImagePropertiesAnnotation()) {
            res.getImagePropertiesAnnotation()
                    .getDominantColors()
                    .getColorsList()
                    .forEach(colorInfo -> {
                        float r = colorInfo.getColor().getRed();
                        float g = colorInfo.getColor().getGreen();
                        float b = colorInfo.getColor().getBlue();
                        result.colors.add(String.format("RGB(%.0f, %.0f, %.0f)", r, g, b));
                    });
        }

        return result;
    }
}
