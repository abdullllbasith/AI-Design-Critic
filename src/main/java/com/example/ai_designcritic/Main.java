package com.example.ai_designcritic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/ai_designcritic/main.fxml")
        );

        Image icon = new Image(getClass().getResourceAsStream("appicon.png"));
        stage.getIcons().add(icon);
        Scene scene = new Scene(loader.load(), 1200, 800);
        stage.setTitle("AI Design Critic");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
