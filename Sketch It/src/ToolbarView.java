// Skeleton was based off sample code in 7.MVC/1.MVC/hellomvc3/HelloMVC3.java

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

class ToolbarView extends Pane implements IView {
    int TOOLBAR_WIDTH = Settings.TOOLBAR_WIDTH;
    int MENUBAR_HEIGHT = Settings.MENUBAR_HEIGHT;
    int TOOLBAR_HEIGHT = Settings.TOOLBOX_HEIGHT;
    int STYLESBAR_HEIGHT = Settings.STYLESBAR_HEIGHT;

    int TOOLBAR_PADDING = Settings.TOOLBAR_PADDING;
    int TOOLBAR_BORDER_WIDTH = Settings.TOOLBAR_BORDER_WIDTH;

    private Model model; // reference to the model
    Group group = new Group();
    Rectangle menu_rect = new Rectangle();
    Rectangle toolbox_rect = new Rectangle();
    Rectangle styles_rect = new Rectangle();

    // Menu bar
    MenuBar menu_bar = new MenuBar();
    Menu file_menu = new Menu("File");
    MenuItem file_new = new MenuItem("New");
    MenuItem file_load = new MenuItem("Load");
    MenuItem file_save = new MenuItem("Save");
    MenuItem file_quit = new MenuItem("Quit");

    Menu edit_menu = new Menu("Edit");
    MenuItem edit_cut = new MenuItem("Cut");
    MenuItem edit_copy = new MenuItem("Copy");
    MenuItem edit_paste = new MenuItem("Paste");

    // Tool palette
    List<Rectangle> tool_palette_rects = new ArrayList<>();

    // Line colour, fill colour, thickness palette, style palette
    Text line_colour_text = new Text("Line Colour");
    Text fill_colour_text = new Text("Fill Colour");
    List<Line> thickness_lines = new ArrayList<>();
    List<Line> style_lines = new ArrayList<>();
    List<Rectangle> thickness_line_rects = new ArrayList<>();
    List<Rectangle> style_line_rects = new ArrayList<>();
    ColorPicker color_picker_line = new ColorPicker();
    ColorPicker color_picker_fill = new ColorPicker();

    // ImagesViews
    ImageView select_image_view;
    ImageView eraser_image_view;
    ImageView line_image_view;
    ImageView circle_image_view;
    ImageView bucket_image_view;
    ImageView rectangle_image_view;

    ToolbarView(Model model) {
        // keep track of the model
        this.model = model;

        // setup the view
        toolbarRectsSetup();

        // menu setup
        menuBarSetup();
        menuBarHandlersSetup();

        // toolbar setup
        toolboxImageSetup();
        toolboxRectsSetup();
        toolboxMouseEventsSetup();

        // styles setup
        colorPickersSetup();
        colorPickerMouseEventsSetup();
        thicknessStyleLinesSetup();
        lineRectanglesSetup();
        lineMouseEventsSetup();

        this.getChildren().add(group);
        // register with the model when we're ready to start receiving data
        model.addView(this);
    }

    // VISUALS SETUP **************************************************************************************************

    // Set up menu bar (file, edit)
    // Most of code taken from 2.JavaFX/6.menubar/MenuDemo.java
    void menuBarSetup() {
        file_menu.getItems().addAll(file_new, file_load, file_save, file_quit);
        edit_menu.getItems().addAll(edit_cut, edit_copy, edit_paste);

        // Map accelerator keys to menu items
        file_new.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        file_load.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
        file_save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        file_quit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        edit_cut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        edit_copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        edit_paste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));

        // Put menus together
        menu_bar.getMenus().addAll(file_menu, edit_menu);
        menu_bar.setStyle("-fx-padding: 6 23 7 23;");
        menu_bar.setTranslateY(1);
        group.getChildren().addAll(menu_bar);
    }

    // Set up rects containing menu bar, tool bar, styles bar
    void toolbarRectsSetup() {
        menu_rect.setWidth(TOOLBAR_WIDTH);                                  // menu bar
        menu_rect.setHeight(MENUBAR_HEIGHT);
        menu_rect.setStroke(Color.BLACK);
        menu_rect.setFill(Color.WHITE);

        toolbox_rect.setWidth(TOOLBAR_WIDTH);                               // tool bar
        toolbox_rect.setHeight(TOOLBAR_HEIGHT);
        toolbox_rect.setTranslateY(MENUBAR_HEIGHT);
        toolbox_rect.setStroke(Color.BLACK);
        toolbox_rect.setFill(Color.WHITE);

        styles_rect.setWidth(TOOLBAR_WIDTH);                                // styles bar
        styles_rect.setHeight(STYLESBAR_HEIGHT);
        styles_rect.setTranslateY(MENUBAR_HEIGHT + TOOLBAR_HEIGHT);
        styles_rect.setStroke(Color.BLACK);
        styles_rect.setFill(Color.WHITE);

        group.getChildren().addAll(menu_rect, toolbox_rect, styles_rect);
    }

    // Set up toolbox images
    void toolboxImageSetup() {
        select_image_view = new ImageView(Settings.select_image);           // select
        select_image_view.setX(TOOLBAR_WIDTH/8.0);
        select_image_view.setY(TOOLBAR_WIDTH/8.0 + MENUBAR_HEIGHT);
        select_image_view.setFitWidth(TOOLBAR_WIDTH/4.0);
        select_image_view.setFitHeight(TOOLBAR_WIDTH/4.0);

        eraser_image_view = new ImageView(Settings.eraser_image);           // erase
        eraser_image_view.setX(5*TOOLBAR_WIDTH/8.0);
        eraser_image_view.setY(TOOLBAR_WIDTH/8.0 + MENUBAR_HEIGHT);
        eraser_image_view.setFitWidth(TOOLBAR_WIDTH/4.0);
        eraser_image_view.setFitHeight(TOOLBAR_WIDTH/4.0);

        line_image_view = new ImageView(Settings.line_image);               // line
        line_image_view.setX(TOOLBAR_WIDTH/8.0);
        line_image_view.setY(5*TOOLBAR_WIDTH/8.0 + MENUBAR_HEIGHT);
        line_image_view.setFitWidth(TOOLBAR_WIDTH/4.0);
        line_image_view.setFitHeight(TOOLBAR_WIDTH/4.0);

        circle_image_view = new ImageView(Settings.circle_image);           // circle
        circle_image_view.setX(5*TOOLBAR_WIDTH/8.0);
        circle_image_view.setY(5*TOOLBAR_WIDTH/8.0 + MENUBAR_HEIGHT);
        circle_image_view.setFitWidth(TOOLBAR_WIDTH/4.0);
        circle_image_view.setFitHeight(TOOLBAR_WIDTH/4.0);

        rectangle_image_view = new ImageView(Settings.rectangle_image);     // rectangle
        rectangle_image_view.setX(TOOLBAR_WIDTH/8.0);
        rectangle_image_view.setY(9*TOOLBAR_WIDTH/8.0 + MENUBAR_HEIGHT);
        rectangle_image_view.setFitWidth(TOOLBAR_WIDTH/4.0);
        rectangle_image_view.setFitHeight(TOOLBAR_WIDTH/4.0);

        bucket_image_view = new ImageView(Settings.bucket_image);           // bucket
        bucket_image_view.setX(5*TOOLBAR_WIDTH/8.0);
        bucket_image_view.setY(9*TOOLBAR_WIDTH/8.0 + MENUBAR_HEIGHT);
        bucket_image_view.setFitWidth(TOOLBAR_WIDTH/4.0);
        bucket_image_view.setFitHeight(TOOLBAR_WIDTH/4.0);

        group.getChildren().addAll(select_image_view, eraser_image_view,line_image_view,
                circle_image_view,rectangle_image_view,bucket_image_view);
    }

    // Set up toolbox rectangles
    void toolboxRectsSetup() {
        for (int i = 0; i < 6; i ++) {
            Rectangle rect = new Rectangle();
            rect.setId(String.valueOf(Model.Tool.values()[i]));
            rect.setTranslateX(TOOLBAR_BORDER_WIDTH/2.0);
            rect.setWidth(TOOLBAR_WIDTH/2.0 - TOOLBAR_BORDER_WIDTH);
            rect.setHeight(TOOLBAR_WIDTH/2.0 - TOOLBAR_BORDER_WIDTH);

            rect.setTranslateY(MENUBAR_HEIGHT + TOOLBAR_BORDER_WIDTH/2.0 +
                    (int)(i / 2) * TOOLBAR_WIDTH/2.0);
            if (i % 2 == 1) {
                rect.setTranslateX(TOOLBAR_BORDER_WIDTH/2.0 + TOOLBAR_WIDTH/2.0);
            }
            rect.setStrokeWidth(TOOLBAR_BORDER_WIDTH);
            rect.setStroke(Color.BLACK);
            rect.setFill(Color.TRANSPARENT);

            tool_palette_rects.add(rect);
            group.getChildren().add(rect);
        }
    }

    // Set up color pickers
    // Some code taken from 5.Widgets/5.colorpicker/ColorPickerSample.java
    void colorPickersSetup() {
        // line colour text and rect
        line_colour_text.setTranslateX(TOOLBAR_PADDING);
        line_colour_text.setTranslateY(MENUBAR_HEIGHT + TOOLBAR_HEIGHT + TOOLBAR_PADDING/2.0 +
                line_colour_text.getLayoutBounds().getHeight());

        color_picker_line.setValue(model.getLineColour());
        color_picker_line.setStyle("-fx-pref-width: 114px; -fx-pref-height: 30px;");
        color_picker_line.setTranslateX(TOOLBAR_PADDING);
        color_picker_line.setTranslateY(line_colour_text.getTranslateY() + 3);

        fill_colour_text.setTranslateX(TOOLBAR_PADDING);
        fill_colour_text.setTranslateY(color_picker_line.getTranslateY() + 30 + TOOLBAR_PADDING/2.0 +
                fill_colour_text.getLayoutBounds().getHeight());

        color_picker_fill.setValue(model.getFillColour());
        color_picker_fill.setStyle("-fx-pref-width: 114px; -fx-pref-height: 30px;");
        color_picker_fill.setTranslateX(TOOLBAR_PADDING);
        color_picker_fill.setTranslateY(fill_colour_text.getTranslateY() + 3);

        group.getChildren().addAll(color_picker_line, line_colour_text, fill_colour_text, color_picker_fill);
    }

    // Set up thickness/style line rectangles
    void lineRectanglesSetup() {
        for (int i = 0; i < 4; i ++){
            Rectangle rect = new Rectangle();
            rect.setId(String.valueOf(thickness_lines.get(i).getStrokeWidth()));
            rect.setTranslateX(TOOLBAR_PADDING + i*(TOOLBAR_WIDTH - 2 * TOOLBAR_PADDING)/4.0);
            rect.setTranslateY(color_picker_fill.getTranslateY() + 30 + TOOLBAR_PADDING);
            rect.setWidth((TOOLBAR_WIDTH - 2 * TOOLBAR_PADDING)/4.0);
            rect.setHeight(thickness_lines.get(0).getLayoutBounds().getHeight() + 1.5 * TOOLBAR_PADDING);
            rect.setStrokeWidth(1);
            rect.setStroke(Color.TRANSPARENT);
            rect.setFill(Color.TRANSPARENT);
            thickness_line_rects.add(rect);

            Rectangle rect2 = new Rectangle();
            rect2.setId(String.valueOf(i));
            rect2.setTranslateX(TOOLBAR_PADDING + i*(TOOLBAR_WIDTH - 2 * TOOLBAR_PADDING)/4.0);
            rect2.setTranslateY(rect.getTranslateY() + rect.getHeight());
            rect2.setWidth((TOOLBAR_WIDTH - 2 * TOOLBAR_PADDING)/4.0);
            rect2.setHeight(thickness_lines.get(0).getLayoutBounds().getHeight() + 1.5 * TOOLBAR_PADDING);
            rect2.setStrokeWidth(1);
            rect2.setStroke(Color.TRANSPARENT);
            rect2.setFill(Color.TRANSPARENT);
            style_line_rects.add(rect2);

            group.getChildren().addAll(rect,rect2);
        }
    }

    // Set up thickness/style lines
    void thicknessStyleLinesSetup() {
        for (int i = 0 ; i < 4; i ++){
            // thickness lines
            Line line = new Line();
            line.setId(String.valueOf(i));
            line.setStartX(TOOLBAR_PADDING * 2 - i*3);
            line.setStartY(color_picker_fill.getTranslateY() + 30 + TOOLBAR_PADDING * 2);
            line.setEndX(line.getStartX() + TOOLBAR_WIDTH/16.0);
            line.setEndY(line.getStartY() + TOOLBAR_WIDTH/4.0);
            line.setTranslateX(i * TOOLBAR_WIDTH/4.0);
            line.setStrokeWidth(Settings.line_thicknesses[i]);
            line.setStroke(Color.BLACK);
            thickness_lines.add(line);

            // style lines
            Line line2 = new Line();
            line2.setId(String.valueOf(i));
            line2.setStartX(TOOLBAR_PADDING * 2 - i*3);
            line2.setStartY(line.getEndY() + TOOLBAR_PADDING * 2);
            line2.setEndX(line2.getStartX() + TOOLBAR_WIDTH/16.0);
            line2.setEndY(line2.getStartY() + TOOLBAR_WIDTH/4.0);
            line2.setTranslateX(i * TOOLBAR_WIDTH/4.0);
            line2.setStrokeWidth(2);
            line2.getStrokeDashArray().setAll(Settings.line_styles[i*2] * 2, Settings.line_styles[i*2+1] * 2);
            line2.setStroke(Color.BLACK);
            style_lines.add(line2);

            group.getChildren().addAll(line,line2);
        }
    }


    // MOUSE EVENTS SETUP *********************************************************************************************

    // Set up handlers for menu bar
    void menuBarHandlersSetup() {
        // Setup handlers
        file_new.setOnAction(actionEvent -> {
            model.fileNewSelected();
        });
        file_load.setOnAction(actionEvent -> {
            model.fileLoadSelected();
        });
        file_save.setOnAction(actionEvent -> {
            model.fileSaveSelected();
        });
        file_quit.setOnAction(actionEvent -> {
            model.fileQuitSelected();
        });

        edit_cut.setOnAction(actionEvent -> {
            model.editCutSelected();
        });
        edit_copy.setOnAction(actionEvent -> {
            model.editCopySelected();
        });
        edit_paste.setOnAction(actionEvent -> {
            model.editPasteSelected();
        });
    }

    // Set up mouse listeners for toolbox rectangles
    void toolboxMouseEventsSetup() {
        for (Rectangle rect : tool_palette_rects) {
            rect.setOnMouseEntered(mouseEvent -> {              // Hovering = green
                rect.setStroke(Color.CHARTREUSE);
                if (model.getSelected()) {
                    if (rect.getId().equals("LINE") || rect.getId().equals("CIRCLE") || rect.getId().equals("RECTANGLE")) {
                        rect.setStroke(Color.LIGHTGRAY);
                    }
                }
            });
            rect.setOnMouseExited(mouseEvent -> {               // Not hovering
                if (!rect.getId().equals(model.getSelectedTool().toString())) {
                    rect.setStroke(Color.BLACK);
                }
                if (model.getSelected()) {
                    if (rect.getId().equals("LINE") || rect.getId().equals("CIRCLE") || rect.getId().equals("RECTANGLE")) {
                        rect.setStroke(Color.LIGHTGRAY);
                    }
                }
            });
            rect.setOnMouseClicked(mouseEvent -> {              // Select tool
                if (!model.getSelected()) {
                    model.selectTool(rect.getId());
                }
                else if (model.getSelected() && !rect.getId().equals("LINE") && !rect.getId().equals("CIRCLE") && !rect.getId().equals("RECTANGLE")) {
                    model.selectTool(rect.getId());
                }
            });
        }
    }

    // Set up mouse listeners for color pickers
    void colorPickerMouseEventsSetup() {
        color_picker_line.setOnAction(new EventHandler() {
            public void handle(Event t) {
                model.updateColours(color_picker_line.getValue(), model.getFillColour());
            }
        });
        color_picker_fill.setOnAction(new EventHandler() {
            public void handle(Event t) {
                model.updateColours(model.getLineColour(), color_picker_fill.getValue());
            }
        });
    }

    // Set up mouse listeners for line rectangles
    void lineMouseEventsSetup() {
        for (Rectangle rect : thickness_line_rects) {
            rect.setOnMouseEntered(mouseEvent -> {          // Hovering = green
                rect.setStroke(Color.CHARTREUSE);
                if (Double.parseDouble(rect.getId()) == model.getLineThickness()) {
                    if (model.getSelectedTool() == Model.Tool.ERASE || model.getSelectedTool() == Model.Tool.FILL) {
                        rect.setStroke(Color.LIGHTGRAY);
                    }
                }
                else if (model.getSelectedTool() == Model.Tool.ERASE || model.getSelectedTool() == Model.Tool.FILL) {
                    rect.setStroke(Color.TRANSPARENT);
                }
            });
            rect.setOnMouseExited(mouseEvent -> {           // Not hovering
                if (Double.parseDouble(rect.getId()) != model.getLineThickness()) {
                    if (Double.parseDouble(rect.getId()) == model.getLineThickness()){
                        if (model.getSelectedTool() == Model.Tool.ERASE || model.getSelectedTool() == Model.Tool.FILL) {
                            rect.setStroke(Color.LIGHTGRAY);
                        }
                    }
                    rect.setStroke(Color.TRANSPARENT);
                }
            });
            rect.setOnMouseClicked(mouseEvent -> {          // Select line thickness
                if (model.getSelectedTool() != Model.Tool.ERASE && model.getSelectedTool() != Model.Tool.FILL) {
                    model.updateLines(Double.parseDouble(rect.getId()), model.getLineStyle());
                }
            });
        }
        for (Rectangle rect : style_line_rects) {
            rect.setOnMouseEntered(mouseEvent -> {          // Hovering = green
                rect.setStroke(Color.CHARTREUSE);
                if (Double.parseDouble(rect.getId()) == model.getLineStyle()) {
                    if (model.getSelectedTool() == Model.Tool.ERASE || model.getSelectedTool() == Model.Tool.FILL) {
                        rect.setStroke(Color.LIGHTGRAY);
                    }
                }
                else if (model.getSelectedTool() == Model.Tool.ERASE || model.getSelectedTool() == Model.Tool.FILL) {
                    rect.setStroke(Color.TRANSPARENT);
                }
            });
            rect.setOnMouseExited(mouseEvent -> {           // Not hovering
                if (Double.parseDouble(rect.getId()) != model.getLineStyle()) {
                    if (Double.parseDouble(rect.getId()) == model.getLineThickness()){
                        if (model.getSelectedTool() == Model.Tool.ERASE || model.getSelectedTool() == Model.Tool.FILL) {
                            rect.setStroke(Color.LIGHTGRAY);
                        }
                    }
                    rect.setStroke(Color.TRANSPARENT);
                }
            });
            rect.setOnMouseClicked(mouseEvent -> {          // Select line style
                if (model.getSelectedTool() != Model.Tool.ERASE && model.getSelectedTool() != Model.Tool.FILL) {
                    model.updateLines(model.getLineThickness(), Integer.parseInt(rect.getId()));
                }
            });
        }
    }

    // When notified by the model that things have changed,
    // update to display the new value
    public void updateView() {
        // Disable menu items
        if (model.getSelected()) {
            edit_copy.setDisable(false);
            edit_cut.setDisable(false);
        }
        else {
            edit_copy.setDisable(true);
            edit_cut.setDisable(true);
        }
        if (model.getClipboard()) {
            edit_paste.setDisable(false);
        }
        else {
            edit_paste.setDisable(true);
        }
        if (edit_copy.isDisable() && edit_cut.isDisable() && edit_paste.isDisable()) {
            edit_menu.setDisable(true);
        }
        else {
            edit_menu.setDisable(false);
        }

        // Set toolbar rectangle colours
        for (Rectangle rect : tool_palette_rects) {
            rect.setStroke(Color.BLACK);
            if (rect.getId().equals(model.getSelectedTool().toString())) {
                rect.setStroke(Color.CHARTREUSE);
            }
            else if (model.getSelected()) {
                if (rect.getId().equals("LINE") || rect.getId().equals("CIRCLE") || rect.getId().equals("RECTANGLE")) {
                    rect.setStroke(Color.LIGHTGRAY);
                }
            }
        }
        // Set line thickness and style rectangle colours
        for (Rectangle rect : thickness_line_rects) {
            rect.setStroke(Color.TRANSPARENT);
            if (Double.parseDouble(rect.getId()) == model.getLineThickness()) {
                if (model.getSelectedTool() == Model.Tool.ERASE || model.getSelectedTool() == Model.Tool.FILL) {
                    rect.setStroke(Color.LIGHTGRAY);
                }
                else {
                    rect.setStroke(Color.CHARTREUSE);
                }
            }
        }
        for (int i = 0; i < style_line_rects.size(); i ++) {
            Rectangle rect = style_line_rects.get(i);
            rect.setStroke(Color.TRANSPARENT);
            if (i == model.getLineStyle()) {
                if (model.getSelectedTool() == Model.Tool.ERASE || model.getSelectedTool() == Model.Tool.FILL) {
                    rect.setStroke(Color.LIGHTGRAY);
                }
                else {
                    rect.setStroke(Color.CHARTREUSE);
                }
            }
        }

        // Update colours
        color_picker_line.setValue(model.getLineColour());
        color_picker_fill.setValue(model.getFillColour());

        // Disable fill colour picker conditions
        if (model.getSelectedTool() == Model.Tool.ERASE || model.getSelectedTool() == Model.Tool.LINE || model.getSelectedShape() == Model.Shape_Type.LINE) {
            color_picker_fill.setDisable(true);
        }
        else {
            color_picker_fill.setDisable(false);
        }

        // Disable line colour picker conditions
        if (model.getSelectedTool() == Model.Tool.ERASE || model.getSelectedTool() == Model.Tool.FILL) {
            color_picker_line.setDisable(true);
        }
        else {
            color_picker_line.setDisable(false);
        }
    }
}
