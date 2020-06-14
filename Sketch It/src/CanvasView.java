// Skeleton was based off sample code in 7.MVC/1.MVC/hellomvc3/HelloMVC3.java

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

class CanvasView extends Pane implements IView {

    private Model model; // reference to the model
    Rectangle canvas_rectangle = new Rectangle();
    List<Line> drawn_lines = new ArrayList<>();
    List<Circle> drawn_circles = new ArrayList<>();
    List<Rectangle> drawn_rectangles = new ArrayList<>();

    CanvasView(Model model) {
        // keep track of the model
        this.model = model;

        // setup the view
        canvas_rectangle.setTranslateX(Settings.TOOLBAR_WIDTH);
        canvas_rectangle.setWidth(model.getCanvasWidth());
        canvas_rectangle.setHeight(model.getCanvasHeight());
        canvas_rectangle.setStroke(Color.BLACK);
        canvas_rectangle.setFill(Color.WHITE);

        // canvas mouse events
        canvasMouseEventsSetup();

        // add canvas rectangle to the pane
        this.getChildren().add(canvas_rectangle);

        // register with the model when we're ready to start receiving data
        model.addView(this);
    }

    // Set up mouse listeners for canvas
    void canvasMouseEventsSetup() {
        canvas_rectangle.setOnMouseClicked(mouseEvent -> {
            model.mousePressedEmptyRect(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        });
        this.setOnMousePressed(mouseEvent -> {
            model.mousePressed(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        });

        this.setOnMouseDragged(mouseEvent -> {
            model.mouseDragged(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        });

        this.setOnMouseReleased(mouseEvent -> {
            model.mouseReleased(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        });
    }

    // Set up mouse listeners for all shapes on canvas
    void shapeMouseEventsSetupLine(Line line) {
        line.setOnMouseClicked(mouseEvent -> {
            model.shapeClickedLine(line);
        });
        line.setOnMousePressed(mouseEvent -> {
            model.shapePressedLine(line, mouseEvent.getSceneX(), mouseEvent.getSceneY());
        });
        line.setOnMouseDragged(mouseEvent -> {
            model.shapeDraggedLine(line, mouseEvent.getSceneX(), mouseEvent.getSceneY());
        });
    }

    void shapeMouseEventsSetupCircle(Circle circle) {
        circle.setOnMouseClicked(mouseEvent -> {
            model.shapeClickedCircle(circle);
        });
        circle.setOnMousePressed(mouseEvent -> {
            model.shapePressedCircle(circle, mouseEvent.getSceneX(), mouseEvent.getSceneY());
        });
        circle.setOnMouseDragged(mouseEvent -> {
            model.shapeDraggedCircle(circle, mouseEvent.getSceneX(), mouseEvent.getSceneY());
        });
    }

    void shapeMouseEventsSetupRect(Rectangle rect) {
        rect.setOnMouseClicked(mouseEvent -> {
            model.shapeClickedRectangle(rect);
        });
        rect.setOnMousePressed(mouseEvent -> {
            model.shapePressedRectangle(rect, mouseEvent.getSceneX(), mouseEvent.getSceneY());
        });
        rect.setOnMouseDragged(mouseEvent -> {
            model.shapeDraggedRectangle(rect, mouseEvent.getSceneX(), mouseEvent.getSceneY());
        });
    }

    // When notified by the model that things have changed,
    // update to display the new value
    public void updateView() {
        // Draw the canvas rect
        canvas_rectangle.setWidth(model.getCanvasWidth());
        canvas_rectangle.setHeight(model.getCanvasHeight());

        // Draw lines
        for (Line line : model.getLines()) {
            if (!this.getChildren().contains(line)) {
                drawn_lines.add(line);
                shapeMouseEventsSetupLine(line);
                this.getChildren().add(line);
            }
            else if (line.getStartX() == line.getEndX() && line.getStartY() == line.getEndY()) {
                drawn_lines.remove((line));
                this.getChildren().remove(line);
            }
        }

        // Draw circles
        for (Circle circle : model.getCircles()) {
            if (!this.getChildren().contains(circle)) {
                drawn_circles.add(circle);
                shapeMouseEventsSetupCircle(circle);
                this.getChildren().add(circle);
            }
            else if (circle.getRadius() == 0) {
                drawn_circles.remove((circle));
                this.getChildren().remove(circle);
            }
        }

        // Draw rectangles
        for (Rectangle rectangle : model.getRectangles()) {
            if (!this.getChildren().contains(rectangle)) {
                drawn_rectangles.add(rectangle);
                shapeMouseEventsSetupRect(rectangle);
                this.getChildren().add(rectangle);
            }
            else if (rectangle.getWidth() * rectangle.getHeight() == 0) {
                drawn_rectangles.remove((rectangle));
                this.getChildren().remove(rectangle);
            }
        }
    }
}
