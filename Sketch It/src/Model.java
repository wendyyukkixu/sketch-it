// Skeleton was based off sample code in HelloMVC3

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Model {
    Stage stage;

    // The data in the model
    double canvas_width = Settings.CANVAS_WIDTH;
    double canvas_height = Settings.CANVAS_HEIGHT;
    List<Line> lines = new ArrayList<>();
    List<Circle> circles = new ArrayList<>();
    List<Rectangle> rectangles = new ArrayList<>();
    int shape_counter = 0;                  // For file i/o purposes
    Shape_Type selected_shape = null;
    Line selected_line = null;
    Circle selected_circle = null;
    Rectangle selected_rect = null;
    Tool tool = Tool.SELECT;                // Currently selected tool
    boolean selected = false;               // If a shape is selected
    boolean moved = false;                  // If shape has moved
    boolean edited = false;                 // If canvas was changed since last save

    // Copy paste stuff
    Shape_Type clipboard_shape = null;
    String clipboard_line = null;
    String clipboard_circle = null;
    String clipboard_rect = null;

    Scanner load_file_scanner;
    FileWriter file_writer;

    // Shape properties
    Color line_colour = Color.BLACK;
    Color fill_colour = Color.BLUE;
    double line_thickness = Settings.line_thicknesses[0];
    int line_style_index = 0;
    double x_start;                         // initial coordinates of mouse when it is pressed down
    double y_start;

    public Model(Stage stage) {
        this.stage = stage;
    }

    enum Tool {
        SELECT, ERASE, LINE, CIRCLE, RECTANGLE, FILL, NONE
    }

    enum Shape_Type {
        LINE, CIRCLE, RECTANGLE
    }

    // all views of this model
    private ArrayList<IView> views = new ArrayList<IView>();

    // method that the views can use to register themselves with the Model
    // once added, they are told to update and get state from the Model
    public void addView(IView view) {
        views.add(view);
        view.updateView();
    }

    // MENU VIEW ******************************************************************************************************

    // Resets to a blank drawing
    void newCanvas() {
        for (Line line : lines) {
            line.setEndX(line.getStartX());         // Lines with no length are removed from the CanvasView
            line.setEndY(line.getStartY());
        }
        for (Circle circle : circles) {
            circle.setRadius(0);
        }
        for (Rectangle rect : rectangles) {
            rect.setWidth(0);
        }
        notifyObservers();
        lines.clear();
        circles.clear();
        rectangles.clear();
        shape_counter = 0;
        selected_line = null;
        selected_circle = null;
        selected_rect = null;
        selected = false;
        moved = false;
        edited = false;
        notifyObservers();
    }

    // Writes canvas state to file
    void saveCanvas() throws IOException {
        int line_index = 0;
        int circle_index = 0 ;
        int rect_index = 0;

        for (int i = 0; i < shape_counter; i ++) {
            if (line_index < lines.size() && Integer.parseInt(lines.get(line_index).getId()) == i) {
                file_writer.write(lineToString(lines.get(line_index)) + "\n");
                line_index ++;
            }
            else if (circle_index < circles.size() && Integer.parseInt(circles.get(circle_index).getId()) == i) {
                file_writer.write(circleToString(circles.get(circle_index)) + "\n");
                circle_index ++;
            }
            else if (rect_index < rectangles.size() && Integer.parseInt(rectangles.get(rect_index).getId()) == i) {
                file_writer.write(rectToString(rectangles.get(rect_index)) + "\n");
                rect_index ++;
            }
        }
        edited = false;
        file_writer.close();
    }

    // Converts line properties to string
    // Ratios are with respect to the canvas dimensions
    // Line start_x_ratio start_y_ratio end_x_ratio end_y_ratio
    //      line_colour_red line_colour_green line_colour_blue
    //      line_width line_style
    String lineToString(Line line) {
        String str = "Line ";
        str += ((line.getStartX() - Settings.TOOLBAR_WIDTH)/canvas_width) + " ";
        str += (line.getStartY()/canvas_height) + " ";
        str += ((line.getEndX() - Settings.TOOLBAR_WIDTH)/canvas_width) + " ";
        str += (line.getEndY()/canvas_height) + " ";
        Color color = (Color)line.getStroke();
        if (selected_shape == Shape_Type.LINE && selected_line == line) {
            color = line_colour;
        }
        str += (int)(color.getRed() * 255) + " " +
                (int)(color.getGreen() * 255) + " " +
                (int)(color.getBlue() * 255) + " ";
        str += line.getStrokeWidth();
        if (!line.getStrokeDashArray().isEmpty()) {
            str += " " + calc_style_index(line.getStrokeWidth(), line.getStrokeDashArray());
        }
        return str;
    }

    // Converts circle properties to string
    // Ratios are with respect to the canvas dimensions
    // Circle center_x_ratio center_y_ratio radius_ratio
    //        line_colour_red line_colour_green line_colour_blue
    //        fill_colour_red fill_colour_green fill_colour_blue
    //        line_width line_style
    String circleToString(Circle circle) {
        String str = "Circle ";
        str += ((circle.getCenterX() - Settings.TOOLBAR_WIDTH)/canvas_width) + " ";
        str += (circle.getCenterY()/canvas_height) + " ";
        str += circle.getRadius() / Math.sqrt(Math.pow(canvas_width, 2) + Math.pow(canvas_height, 2)) + " ";
        Color color_stroke = (Color)circle.getStroke();
        if (selected_shape == Shape_Type.CIRCLE && selected_circle == circle) {
            color_stroke = line_colour;
        }
        str += (int)(color_stroke.getRed() * 255) + " " +
                (int)(color_stroke.getGreen() * 255) + " " +
                (int)(color_stroke.getBlue() * 255) + " ";
        Color color_fill = (Color)circle.getFill();
        str += (int)(color_fill.getRed() * 255) + " " +
                (int)(color_fill.getGreen() * 255) + " " +
                (int)(color_fill.getBlue() * 255) + " ";
        str += circle.getStrokeWidth();
        if (!circle.getStrokeDashArray().isEmpty()) {
            str += " " + calc_style_index(circle.getStrokeWidth(), circle.getStrokeDashArray());
        }
        return str;
    }

    // Converts rectangle properties to string
    // Ratios are with respect to the canvas dimensions
    // Rectangle x_ratio y_ratio width_ratio height_ratio
    //           line_colour_red line_colour_green line_colour_blue
    //           fill_colour_red fill_colour_green fill_colour_blue
    //           line_width line_style
    String rectToString(Rectangle rect) {
        String str = "Rectangle ";
        str += ((rect.getX() - Settings.TOOLBAR_WIDTH)/canvas_width)  + " ";
        str += (rect.getY()/canvas_height) + " ";
        str += (rect.getWidth()/canvas_width) + " ";
        str += (rect.getHeight()/canvas_height) + " ";
        Color color_stroke = (Color)rect.getStroke();
        if (selected_shape == Shape_Type.RECTANGLE && selected_rect == rect) {
            color_stroke = line_colour;
        }
        str += (int)(color_stroke.getRed() * 255) + " " +
                (int)(color_stroke.getGreen() * 255) + " " +
                (int)(color_stroke.getBlue() * 255) + " ";
        Color color_fill = (Color)rect.getFill();
        str += (int)(color_fill.getRed() * 255) + " " +
                (int)(color_fill.getGreen() * 255) + " " +
                (int)(color_fill.getBlue() * 255) + " ";
        str += rect.getStrokeWidth();
        if (!rect.getStrokeDashArray().isEmpty()) {
            str += " " + calc_style_index(rect.getStrokeWidth(), rect.getStrokeDashArray());
        }
        return str;
    }

    // Loads canvas state from file
    void loadCanvas() {
        // Clear the canvas before loading a new one
        newCanvas();
        while (load_file_scanner.hasNextLine()){
            String[] words = load_file_scanner.nextLine().split(" ");
            switch (words[0]) {
                case "Line":
                    loadLine(words);
                    break;
                case "Circle":
                    loadCircle(words);
                    break;
                case "Rectangle":
                    loadRectangle(words);
                    break;
            }
        }
    }

    // Converts line string to line on screen
    Line loadLine(String[] words) {
        // Line start_x_ratio start_y_ratio end_x_ratio end_y_ratio
        //      line_colour_red line_colour_green line_colour_blue
        //      line_width line_style
        double start_x_ratio = Double.parseDouble(words[1]);
        double start_y_ratio = Double.parseDouble(words[2]);
        double end_x_ratio = Double.parseDouble(words[3]);
        double end_y_ratio = Double.parseDouble(words[4]);
        Color color = Color.rgb(Integer.parseInt(words[5]),
                Integer.parseInt(words[6]),
                Integer.parseInt(words[7]));
        double thickness = Double.parseDouble(words[8]);

        Line line = new Line();
        line.setId(String.valueOf(shape_counter));
        line.setStartX(canvas_width * start_x_ratio + Settings.TOOLBAR_WIDTH);
        line.setStartY(canvas_height * start_y_ratio);
        line.setEndX(canvas_width * end_x_ratio + Settings.TOOLBAR_WIDTH);
        line.setEndY(canvas_height * end_y_ratio);
        line.setStroke(color);
        line.setStrokeWidth(thickness);
        if (words.length == 10) {
            int line_style_index = Integer.parseInt(words[9]);
            line.getStrokeDashArray().setAll(Settings.line_styles[line_style_index*2] * thickness,
                    Settings.line_styles[line_style_index*2+1] * thickness);
        }
        lines.add(line);
        shape_counter ++;
        notifyObservers();
        return line;
    }

    // Converts circle string line to circle on screen
    Circle loadCircle(String[] words) {
        // Circle center_x_ratio center_y_ratio radius_ratio
        //        line_colour_red line_colour_green line_colour_blue
        //        fill_colour_red fill_colour_green fill_colour_blue
        //        line_width line_style
        double center_x_ratio = Double.parseDouble(words[1]);
        double center_y_ratio = Double.parseDouble(words[2]);
        double radius_ratio = Double.parseDouble(words[3]);
        Color color_stroke = Color.rgb(Integer.parseInt(words[4]),
                Integer.parseInt(words[5]),
                Integer.parseInt(words[6]));
        Color color_fill = Color.rgb(Integer.parseInt(words[7]),
                Integer.parseInt(words[8]),
                Integer.parseInt(words[9]));
        double thickness = Double.parseDouble(words[10]);

        Circle circle = new Circle();
        circle.setId(String.valueOf(shape_counter));
        circle.setCenterX(canvas_width * center_x_ratio + Settings.TOOLBAR_WIDTH);
        circle.setCenterY(canvas_height * center_y_ratio);
        circle.setRadius(radius_ratio * Math.sqrt(Math.pow(canvas_width, 2) + Math.pow(canvas_height, 2)));
        circle.setStroke(color_stroke);
        circle.setFill(color_fill);
        circle.setStrokeWidth(thickness);
        if (words.length == 12) {
            int line_style_index = Integer.parseInt(words[11]);
            circle.getStrokeDashArray().setAll(Settings.line_styles[line_style_index*2] * thickness,
                    Settings.line_styles[line_style_index*2+1] * thickness);
        }
        circles.add(circle);
        shape_counter ++;
        notifyObservers();
        return circle;
    }

    // Converts rect string line to rect on screen
    Rectangle loadRectangle(String[] words){
        // Rectangle x_ratio y_ratio width_ratio height_ratio
        //           line_colour_red line_colour_green line_colour_blue
        //           fill_colour_red fill_colour_green fill_colour_blue
        //           line_width line_style
        double x_ratio = Double.parseDouble(words[1]);
        double y_ratio = Double.parseDouble(words[2]);
        double width_ratio = Double.parseDouble(words[3]);
        double height_ratio = Double.parseDouble(words[4]);
        Color color_stroke = Color.rgb(Integer.parseInt(words[5]),
                Integer.parseInt(words[6]),
                Integer.parseInt(words[7]));
        Color color_fill = Color.rgb(Integer.parseInt(words[8]),
                Integer.parseInt(words[9]),
                Integer.parseInt(words[10]));
        double thickness = Double.parseDouble(words[11]);

        Rectangle rect = new Rectangle();
        rect.setId(String.valueOf(shape_counter));
        rect.setX(canvas_width * x_ratio + Settings.TOOLBAR_WIDTH);
        rect.setY(canvas_height * y_ratio);
        rect.setWidth(canvas_width * width_ratio);
        rect.setHeight(canvas_height * height_ratio);
        rect.setStroke(color_stroke);
        rect.setFill(color_fill);
        rect.setStrokeWidth(thickness);
        if (words.length == 13) {
            int line_style_index = Integer.parseInt(words[12]);
            rect.getStrokeDashArray().setAll(Settings.line_styles[line_style_index*2] * thickness,
                    Settings.line_styles[line_style_index*2+1] * thickness);
        }
        rectangles.add(rect);
        shape_counter ++;
        notifyObservers();
        return rect;
    }

    // Copies currently selected shape properties into a shape string
    void copySelected() {
        if (selected) {
            clipboard_shape = selected_shape;
            switch (selected_shape) {
                case LINE:
                    selected_line.setStroke(line_colour);
                    clipboard_line = lineToString(selected_line);
                    selected_line.setStroke(Color.CHARTREUSE);
                    break;
                case CIRCLE:
                    selected_circle.setStroke(line_colour);
                    clipboard_circle = circleToString(selected_circle);
                    selected_circle.setStroke(Color.CHARTREUSE);
                    break;
                case RECTANGLE:
                    selected_rect.setStroke(line_colour);
                    clipboard_rect = rectToString(selected_rect);
                    selected_rect.setStroke(Color.CHARTREUSE);
                    break;
            }
        }
    }

    void pasteSelected() {
        if (clipboard_shape != null) {
            edited = true;
            switch (clipboard_shape) {
                case LINE:
                    deselectShape();
                    Line line = loadLine(clipboard_line.split(" "));
                    moved = false;
                    if (tool == Tool.SELECT) {
                        selected_shape = clipboard_shape;
                        selected_line = line;
                        selected = true;
                        updateColours((Color) selected_line.getStroke(), fill_colour);
                        updateLines(selected_line.getStrokeWidth(),
                                calc_style_index(selected_line.getStrokeWidth(),
                                        selected_line.getStrokeDashArray()));
                        selected_line.setStroke(Color.CHARTREUSE);
                    }
                    break;
                case CIRCLE:
                    deselectShape();
                    Circle circle = loadCircle(clipboard_circle.split(" "));
                    moved = false;
                    if (tool == Tool.SELECT) {
                        selected_shape = clipboard_shape;
                        selected_circle = circle;
                        selected = true;
                        updateColours((Color) selected_circle.getStroke(), (Color) selected_circle.getFill());
                        updateLines(selected_circle.getStrokeWidth(),
                                calc_style_index(selected_circle.getStrokeWidth(),
                                        selected_circle.getStrokeDashArray()));
                        selected_circle.setStroke(Color.CHARTREUSE);
                    }
                    break;
                case RECTANGLE:
                    deselectShape();
                    Rectangle rect = loadRectangle((clipboard_rect.split(" ")));
                    moved = false;
                    if (tool == Tool.SELECT) {
                        selected_shape = clipboard_shape;
                        selected_rect = rect;
                        selected = true;
                        updateColours((Color) selected_rect.getStroke(), (Color) selected_rect.getFill());
                        updateLines(selected_rect.getStrokeWidth(),
                                calc_style_index(selected_rect.getStrokeWidth(),
                                        selected_rect.getStrokeDashArray()));
                        selected_rect.setStroke(Color.CHARTREUSE);
                    }
                    break;
            }
        }
    }

    // Method that ToolbarView uses to tell the Model a menu bar item was selected
    public void fileNewSelected() {
        if (edited && (!lines.isEmpty() || !circles.isEmpty() || !rectangles.isEmpty())) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to save your changes before creating a new canvas?",
                    ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                fileSaveSelected();
            }
            else if (alert.getResult() == ButtonType.CANCEL) {
                return;
            }
        }
        newCanvas();
    }

    public void fileLoadSelected() {
        if (edited && (!lines.isEmpty() || !circles.isEmpty() || !rectangles.isEmpty())) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to save your changes before loading?",
                    ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                fileSaveSelected();
            }
            else if (alert.getResult() == ButtonType.CANCEL) {
                return;
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Canvas");
        fileChooser.setInitialFileName("Canvas.txt");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                load_file_scanner = new Scanner(file);
                loadCanvas();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void fileSaveSelected() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Canvas");
        fileChooser.setInitialFileName("Canvas.txt");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                file_writer = new FileWriter(file.getAbsolutePath());
                saveCanvas();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void fileQuitSelected() {
        if (edited && (!lines.isEmpty() || !circles.isEmpty() || !rectangles.isEmpty())) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to save your changes before exiting?",
                    ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                fileSaveSelected();
            }
            else if (alert.getResult() == ButtonType.CANCEL) {
                return;
            }
        }
        System.exit(0);
    }

    public void editCutSelected() {
        copySelected();
        deleteSelectedShape();
        notifyObservers();
    }

    public void editCopySelected() {
        copySelected();
        notifyObservers();
    }

    public void editPasteSelected() {
        pasteSelected();
    }

    // TOOLBAR VIEW ***************************************************************************************************

    // Method that ToolbarView uses to tell the Model the selected selected tool
    public void selectTool(String new_tool) {
        // Deselect any selected shapes when changing off the select tool
        if (tool == Tool.SELECT && Tool.valueOf(new_tool) != Tool.SELECT) {
            deselectShape();
        }
        tool = Tool.valueOf(new_tool);
        notifyObservers();
    }

    // Determines line style index based on thickness and stroke dash array
    int calc_style_index(double thickness, ObservableList<Double> stroke_dash) {
        if (stroke_dash.size() == 0){
            return 0;
        }
        for (int i = 1; i < 4; i ++) {
            if (Settings.line_styles[i*2] * thickness == stroke_dash.get(0) &&
                    Settings.line_styles[i*2+1] * thickness == stroke_dash.get(1)) {
                return i;
            }
        }
        return -1;
    }

    // Method that ToolbarView uses to tell the Model the selected colours
    void updateColours(Color line, Color fill) {
        line_colour = line;
        fill_colour = fill;
        updateSelectedFill();
        notifyObservers();
    }

    // Method that ToolbarView uses to tell the Model the selected line thickness and style
    void updateLines(double thickness, int style) {
        line_thickness = thickness;
        line_style_index = style;
        updateSelectedThicknessStyle();
        notifyObservers();
    }


    // CANVAS VIEW ****************************************************************************************************

    // ************************************* CANVAS SIZING *************************************
    public void resizeWindow(double width, double height) {
        resizeLines(width, height);
        resizeCircles(width, height);
        resizeRectangles(width, height);
        canvas_width = width;
        canvas_height = height;
        notifyObservers();
    }

    void resizeLines(double new_canvas_width, double new_canvas_height) {
        for (Line line : lines) {
            double new_start_x = new_canvas_width*((line.getStartX() - Settings.TOOLBAR_WIDTH)/canvas_width) +
                    Settings.TOOLBAR_WIDTH;
            double new_start_y = new_canvas_height*(line.getStartY()/canvas_height);
            double new_end_x = new_canvas_width*((line.getEndX() - Settings.TOOLBAR_WIDTH)/canvas_width) +
                    Settings.TOOLBAR_WIDTH;
            double new_end_y = new_canvas_height*(line.getEndY()/canvas_height);
            line.setStartX(new_start_x);
            line.setStartY(new_start_y);
            line.setEndX(new_end_x);
            line.setEndY(new_end_y);
        }
    }

    void resizeCircles(double new_canvas_width, double new_canvas_height) {
        for (Circle circle : circles) {
            double new_center_x = new_canvas_width*((circle.getCenterX() - Settings.TOOLBAR_WIDTH)/canvas_width) +
                    Settings.TOOLBAR_WIDTH;
            double new_center_y = new_canvas_height*(circle.getCenterY()/canvas_height);
            double ratio = circle.getRadius() / Math.sqrt(Math.pow(canvas_width, 2) + Math.pow(canvas_height, 2));
            double new_radius = ratio *  Math.sqrt(Math.pow(new_canvas_width, 2) + Math.pow(new_canvas_height, 2));

            circle.setCenterX(new_center_x);
            circle.setCenterY(new_center_y);
            circle.setRadius(new_radius);
        }
    }

    void resizeRectangles(double new_canvas_width, double new_canvas_height) {
        for (Rectangle rect : rectangles) {
            double new_x = new_canvas_width*((rect.getX() - Settings.TOOLBAR_WIDTH)/canvas_width) +
                    Settings.TOOLBAR_WIDTH;
            double new_y = new_canvas_height*(rect.getY()/canvas_height);
            double new_width = new_canvas_width*(rect.getWidth()/canvas_width);
            double new_height = new_canvas_height*(rect.getHeight()/canvas_height);

            rect.setX(new_x);
            rect.setY(new_y);
            rect.setWidth(new_width);
            rect.setHeight(new_height);
        }
    }

    // **************************************** DRAWING ****************************************

    // Methods that the CanvasView uses to tell the Model the canvas had mouse actions
    public void mousePressedEmptyRect(double x, double y) {
        if (tool == Tool.SELECT) {
            deselectShape();
        }
    }

    public void mousePressed(double x, double y) {
        if (tool == Tool.LINE || tool == Tool.CIRCLE || tool == Tool.RECTANGLE) {
            startShape(x, y);
        }
    }

    public void mouseDragged(double x, double y) {
        if (tool == Tool.LINE || tool == Tool.CIRCLE || tool == Tool.RECTANGLE) {
            drawShape(x, y);
        }
    }

    public void mouseReleased(double x, double y) {
        if (tool == Tool.LINE || tool == Tool.CIRCLE || tool == Tool.RECTANGLE) {
            finishShape();
        }
    }

    // ********** START DRAWING  **********
    public void startShape(double x, double y) {
        x_start = x;
        y_start = y;
        switch(tool) {
            case LINE:
                startShapeLine(x,y);
                break;
            case CIRCLE:
                startShapeCircle(x,y);
                break;
            case RECTANGLE:
                startShapeRectangle(x, y);
                break;
        }
    }

    // Starts drawing a line on the canvas
    void startShapeLine(double x, double y) {
        Line line = new Line();
        line.setId(String.valueOf(shape_counter));
        line.setStartX(x);
        line.setStartY(y);
        line.setEndX(x);
        line.setEndY(y);
        line.setStroke(line_colour);
        line.setStrokeWidth(line_thickness);
        if (line_style_index != 0){
            line.getStrokeDashArray().setAll(Settings.line_styles[line_style_index*2] * line_thickness,
                    Settings.line_styles[line_style_index*2+1] * line_thickness);
        }
        lines.add(line);
        shape_counter ++;
        notifyObservers();
    }

    // Starts drawing a circle shape on the canvas
    void startShapeCircle(double x, double y) {
        Circle circle = new Circle();
        circle.setId(String.valueOf(shape_counter));
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setStroke(line_colour);
        circle.setFill(fill_colour);
        circle.setStrokeWidth(line_thickness);
        if (line_style_index != 0){
            circle.getStrokeDashArray().setAll(Settings.line_styles[line_style_index*2] * line_thickness,
                    Settings.line_styles[line_style_index*2+1] * line_thickness);
        }
        circles.add(circle);
        shape_counter ++;
        notifyObservers();
    }

    // Starts drawing a rectangle shape on the canvas
    void startShapeRectangle(double x, double y) {
        Rectangle rect = new Rectangle();
        rect.setId(String.valueOf(shape_counter));
        rect.setX(x);
        rect.setY(y);
        rect.setStroke(line_colour);
        rect.setFill(fill_colour);
        rect.setStrokeWidth(line_thickness);
        if (line_style_index != 0){
            rect.getStrokeDashArray().setAll(Settings.line_styles[line_style_index*2] * line_thickness,
                    Settings.line_styles[line_style_index*2+1] * line_thickness);
        }
        rectangles.add(rect);
        shape_counter ++;
        notifyObservers();
    }

    // ********** CONTINUING DRAWING **********
    public void drawShape(double x, double y) {
        switch(tool) {
            case LINE:
                drawShapeLine(x,y);
                break;
            case CIRCLE:
                drawShapeCircle(x,y);
                break;
            case RECTANGLE:
                drawShapeRectangle(x, y);
                break;
        }
        edited = true;
    }

    // Continue drawing a line on the canvas
    void drawShapeLine(double x, double y) {
        Line line = lines.get(lines.size()-1);
        line.setEndX(x);
        line.setEndY(y);
    }

    // Continue drawing a circle on the canvas
    void drawShapeCircle(double x, double y) {
        Circle circle = circles.get(circles.size()-1);

        // Determine radius to set
        double radius = Math.sqrt((Math.pow(x - circle.getCenterX(), 2)) + Math.pow(y - circle.getCenterY(), 2));
        circle.setRadius(radius);
    }

    // Continue drawing a rectangle on the canvas
    void drawShapeRectangle(double x, double y) {
        Rectangle rect = rectangles.get(rectangles.size()-1);
        rect.setX(x_start);
        rect.setY(y_start);
        rect.setWidth(x - rect.getX());
        rect.setHeight(y - rect.getY());
        if (x < x_start && y < y_start) {
            rect.setX(x);
            rect.setY(y);
            rect.setWidth(x_start - x);
            rect.setHeight(y_start - y);
        }
        else if (x < x_start) {
            rect.setX(x);
            rect.setWidth(x_start - x);
        }
        else if (y < y_start) {
            rect.setY(y);
            rect.setHeight(y_start - y);
        }
    }

    // ********** FINISH DRAWING **********
    public void finishShape() {
        switch(tool) {
            case LINE:
                checkValidityLine();
                break;
            case CIRCLE:
                checkValidityCircle();
                break;
            case RECTANGLE:
                checkValidityRectangle();
                break;
        }
    }

    // Removes line shape if invalid
    void checkValidityLine() {
        Line line = lines.get(lines.size()-1);
        if (line.getStartX() == line.getEndX() && line.getStartY() == line.getEndY()) {
            notifyObservers();
            lines.remove(line);
        }
    }

    // Removes circle shape if invalid
    void checkValidityCircle() {
        Circle circle = circles.get(circles.size()-1);
        if (circle.getRadius() == 0) {
            notifyObservers();
            circles.remove(circle);
        }
    }

    // Removes rectangle shape if invalid
    void checkValidityRectangle() {
        Rectangle rect = rectangles.get(rectangles.size()-1);
        if (rect.getWidth() * rect.getHeight() == 0) {
            notifyObservers();
            rectangles.remove(rect);
        }
    }

    // **************************************** SHAPE MANIPULATION ****************************************

    // ********** LINE **********
    // Method that CanvasView uses to tell the Model that a line was selected
    void shapeClickedLine(Line line) {
        // Only deselect shape if originally selected and wasn't just dragged
        if (tool == Tool.SELECT) {
            if (!moved && selected && selected_line == line) {
                deselectShape();
            }
            // Shape was not originally selected, select it
            else {
                deselectShape();                    // Deselect previous shape
                selected_shape = Shape_Type.LINE;
                selected = true;
                moved = false;
                selected_line = line;
                updateColours((Color) selected_line.getStroke(), fill_colour);
                updateLines(selected_line.getStrokeWidth(),
                        calc_style_index(selected_line.getStrokeWidth(),
                                selected_line.getStrokeDashArray()));
                selected_line.setStroke(Color.CHARTREUSE);
            }
        }
        else if (tool == Tool.ERASE) {
            line.setEndX(line.getStartX());         // Lines with no length are removed from the CanvasView
            line.setEndY(line.getStartY());
            notifyObservers();
            lines.remove(line);
        }
    }

    // Method that CanvasView uses to tell the Model that a line might be dragged
    void shapePressedLine(Line line, double x, double y) {
        if (tool == Tool.SELECT && selected && selected_line == line) {
            x_start = x;
            y_start = y;
        }
    }

    // Method that CanvasView uses to tell the Model that a line was dragged
    void shapeDraggedLine(Line line, double x, double y) {
        if (tool == Tool.SELECT && selected && selected_line == line) {
            selected_line.setStartX(selected_line.getStartX() + (x - x_start));
            selected_line.setStartY(selected_line.getStartY() + (y - y_start));
            selected_line.setEndX(selected_line.getEndX() + (x - x_start));
            selected_line.setEndY(selected_line.getEndY() + (y - y_start));
            x_start = x;
            y_start = y;
            moved = true;
            edited = true;
        }
    }


    // ********** CIRCLE **********
    // Method that CanvasView uses to tell the Model that a circle was selected
    void shapeClickedCircle(Circle circle) {
        // Only deselect shape if originally selected and wasn't just dragged
        if (tool == Tool.SELECT) {
            if (!moved && selected && selected_circle == circle) {
                deselectShape();
            }
            // Shape was not originally selected, select it
            else {
                deselectShape();                    // Deselect previous shape
                selected_shape = Shape_Type.CIRCLE;
                selected = true;
                moved = false;
                selected_circle = circle;
                updateColours((Color) selected_circle.getStroke(), (Color) selected_circle.getFill());
                updateLines(selected_circle.getStrokeWidth(),
                        calc_style_index(selected_circle.getStrokeWidth(),
                                selected_circle.getStrokeDashArray()));
                selected_circle.setStroke(Color.CHARTREUSE);
            }
        }
        else if (tool == Tool.ERASE) {
            circle.setRadius(0);                    // Any 0 area shapes are removed from the CanvasView
            notifyObservers();
            circles.remove(circle);
        }
        else if (tool == Tool.FILL) {
            circle.setFill(fill_colour);
        }
    }

    // Method that CanvasView uses to tell the Model that a circle might be dragged
    void shapePressedCircle(Circle circle, double x, double y) {
        if (tool == Tool.SELECT && selected && selected_circle == circle) {
            x_start = x;
            y_start = y;
        }
    }

    // Method that CanvasView uses to tell the Model that a circle was dragged
    void shapeDraggedCircle(Circle circle, double x, double y) {
        if (tool == Tool.SELECT && selected && selected_circle == circle) {
            selected_circle.setCenterX(selected_circle.getCenterX() + (x - x_start));
            selected_circle.setCenterY(selected_circle.getCenterY() + (y - y_start));
            x_start = x;
            y_start = y;
            moved = true;
            edited = true;
        }
    }


    // ********** RECT **********
    // Method that CanvasView uses to tell the Model that a rectangle was selected
    void shapeClickedRectangle(Rectangle rect) {
        // Only deselect shape if originally selected and wasn't just dragged
        if (tool == Tool.SELECT) {
            if (!moved && selected && selected_rect == rect) {
                deselectShape();
            }
            // Shape was not originally selected, select it
            else {
                deselectShape();                    // Deselect previous shape
                selected_shape = Shape_Type.RECTANGLE;
                selected = true;
                moved = false;
                selected_rect = rect;
                updateColours((Color) selected_rect.getStroke(), (Color) selected_rect.getFill());
                updateLines(selected_rect.getStrokeWidth(),
                        calc_style_index(selected_rect.getStrokeWidth(),
                                selected_rect.getStrokeDashArray()));
                selected_rect.setStroke(Color.CHARTREUSE);
            }
        }
        else if (tool == Tool.ERASE) {
            rect.setWidth(0);                       // Any 0 area shapes are removed from the CanvasView
            notifyObservers();
            rectangles.remove(rect);
        }
        else if (tool == Tool.FILL) {
            rect.setFill(fill_colour);
        }
    }

    // Method that CanvasView uses to tell the Model that a rectangle might be dragged
    void shapePressedRectangle(Rectangle rect, double x, double y) {
        if (tool == Tool.SELECT && selected && selected_rect == rect) {
            x_start = x;
            y_start = y;
        }
    }

    // Method that CanvasView uses to tell the Model that a rectangle was dragged
    void shapeDraggedRectangle(Rectangle rect, double x, double y) {
        if (tool == Tool.SELECT && selected && selected_rect == rect) {
            selected_rect.setX(selected_rect.getX() + (x - x_start));
            selected_rect.setY(selected_rect.getY() + (y - y_start));
            x_start = x;
            y_start = y;
            moved = true;
            edited = true;
        }
    }


    // ********** CHANGING SHAPE PROPERTIES **********

    // Changes fill colour of currently selected shape
    void updateSelectedFill() {
        if (tool == Tool.SELECT) {
            if (selected_shape == Shape_Type.CIRCLE) {
                selected_circle.setFill(fill_colour);
                edited = true;
            } else if (selected_shape == Shape_Type.RECTANGLE) {
                selected_rect.setFill(fill_colour);
                edited = true;
            }
        }
    }

    // Changes line thickness and style of currently selected shape
    void updateSelectedThicknessStyle() {
        if (tool == Tool.SELECT) {
            if (selected_shape == Shape_Type.LINE) {
                selected_line.setStrokeWidth(line_thickness);
                selected_line.getStrokeDashArray().setAll();
                if (line_style_index != 0) {
                    selected_line.getStrokeDashArray().setAll(Settings.line_styles[line_style_index*2] * line_thickness,
                            Settings.line_styles[line_style_index*2+1] * line_thickness);
                }
                edited = true;
            }
            else if (selected_shape == Shape_Type.CIRCLE) {
                selected_circle.setStrokeWidth(line_thickness);
                selected_circle.getStrokeDashArray().setAll();
                if (line_style_index != 0) {
                    selected_circle.getStrokeDashArray().setAll(Settings.line_styles[line_style_index*2] * line_thickness,
                            Settings.line_styles[line_style_index*2+1] * line_thickness);
                }
                edited = true;
            } else if (selected_shape == Shape_Type.RECTANGLE) {
                selected_rect.setStrokeWidth(line_thickness);
                selected_rect.getStrokeDashArray().setAll();
                if (line_style_index != 0) {
                    selected_rect.getStrokeDashArray().setAll(Settings.line_styles[line_style_index*2] * line_thickness,
                            Settings.line_styles[line_style_index*2+1] * line_thickness);
                }
                edited = true;
            }
        }
    }

    // Deselect any selected shapes
    void deselectShape() {
        selected = false;
        // Deselect previous shape
        if (selected_line != null) {
            selected_line.setStroke(line_colour);
        }
        if (selected_circle != null) {
            selected_circle.setStroke(line_colour);
        }
        if (selected_rect != null) {
            selected_rect.setStroke(line_colour);
        }
        selected_line = null;
        selected_circle = null;
        selected_rect = null;
        selected_shape = null;
        notifyObservers();
    }

    void deleteSelectedShape() {
        selected = false;
        if (tool == Tool.SELECT) {
            if (selected_shape == Shape_Type.LINE) {
                selected_line.setEndX(selected_line.getStartX());   // Lines with no length are removed from the CanvasView
                selected_line.setEndY(selected_line.getStartY());
                notifyObservers();
                lines.remove(selected_line);
            } else if (selected_shape == Shape_Type.CIRCLE) {
                selected_circle.setRadius(0);                       // Any 0 area shapes are removed from the CanvasView
                notifyObservers();
                circles.remove(selected_circle);
            } else if (selected_shape == Shape_Type.RECTANGLE) {
                selected_rect.setWidth(0);                          // Any 0 area shapes are removed from the CanvasView
                notifyObservers();
                rectangles.remove(selected_rect);
            }
        }
        notifyObservers();
    }

    // **************************************** GETTERS ****************************************

    public double getCanvasWidth() {
        return canvas_width;
    }

    public double getCanvasHeight() {
        return canvas_height;
    }

    public Tool getSelectedTool() {
        return tool;
    }

    public Color getLineColour() {
        return line_colour;
    }

    public Color getFillColour() {
        return fill_colour;
    }

    public double getLineThickness() {
        return line_thickness;
    }

    public int getLineStyle() {
        return line_style_index;
    }

    public boolean getSelected() {
        return selected;
    }

    public Shape_Type getSelectedShape() {
        return selected_shape;
    }

    public boolean getClipboard() {
        if (clipboard_shape != null) {
            return true;
        }
        return false;
    }

    public List<Line> getLines() {
        return lines;
    }

    public List<Circle> getCircles() {
        return circles;
    }

    public List<Rectangle> getRectangles() {
        return rectangles;
    }

    // the model uses this method to notify all of the Views that the data has changed
    // the expectation is that the Views will refresh themselves to display new data when appropriate
    private void notifyObservers() {
        for (IView view : this.views) {
            view.updateView();
        }
    }
}