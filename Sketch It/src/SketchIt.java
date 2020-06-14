// Skeleton was based off sample code in 7.MVC/1.MVC/hellomvc3/HelloMVC3.java

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;


public class SketchIt extends Application {
    final int WINDOW_WIDTH = Settings.WINDOW_WIDTH;
    final int WINDOW_HEIGHT = Settings.WINDOW_HEIGHT;

    Pane sketchit_pane;
    Scene sketchit_scene;
    CanvasView canvasView;
    ToolbarView toolbarView;
    Model model;

    @Override
    public void start(Stage stage) throws IOException {
        // Set min/max window sizes
        stage.setMinWidth(Settings.WINDOW_WIDTH_MIN);
        stage.setMinHeight(Settings.WINDOW_HEIGHT_MIN);
        stage.setMaxWidth(Settings.WINDOW_WIDTH_MAX);
        stage.setMaxHeight(Settings.WINDOW_HEIGHT_MAX);

        // create and initialize the Model
        model = new Model(stage);

        // create each view, and tell them about the model
        // the views will register themselves with the model
        canvasView = new CanvasView(model);
        toolbarView = new ToolbarView(model);

        sketchit_pane = new Pane();
        sketchit_scene = new Scene(sketchit_pane, WINDOW_WIDTH, WINDOW_HEIGHT);

        sketchit_pane.getChildren().add(canvasView);
        sketchit_pane.getChildren().add(toolbarView);

        // Listeners for keyboard input
        sketchit_scene.addEventHandler(KeyEvent.KEY_RELEASED, (key) -> {
            if (key.getCode() == KeyCode.ESCAPE) {
                model.deselectShape();
            }
            else if (key.getCode() == KeyCode.DELETE || key.getCode() == KeyCode.BACK_SPACE) {
                model.deleteSelectedShape();
            }
        });

        // Listeners for window size changes
        sketchit_scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            model.resizeWindow(sketchit_scene.getWidth() - Settings.TOOLBAR_WIDTH, sketchit_scene.getHeight());
        });
        sketchit_scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            model.resizeWindow(sketchit_scene.getWidth() - Settings.TOOLBAR_WIDTH, sketchit_scene.getHeight());
        });

        // Attach the scene to the stage and show it
        stage.setScene(sketchit_scene);
        stage.setTitle(Settings.WINDOW_TITLE);
        stage.show();
    }
}