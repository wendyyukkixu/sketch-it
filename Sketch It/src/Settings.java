import javafx.scene.image.Image;

public class Settings {
    // Constants
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 750;
    public static final int WINDOW_WIDTH_MIN = 700;
    public static final int WINDOW_HEIGHT_MIN = 480;
    public static final int WINDOW_WIDTH_MAX = 1400;
    public static final int WINDOW_HEIGHT_MAX = 800;
    public static final String WINDOW_TITLE = "Sketch it";

    // TOOLBAR VIEW
    public static final int TOOLBAR_WIDTH = 130;
    public static final int TOOLBAR_PADDING = TOOLBAR_WIDTH/16;
    public static final int MENUBAR_HEIGHT = WINDOW_WIDTH_MIN/16;
    public static final int TOOLBOX_HEIGHT = 3 * TOOLBAR_WIDTH/2;
    public static final int STYLESBAR_HEIGHT = WINDOW_HEIGHT_MAX - TOOLBOX_HEIGHT - MENUBAR_HEIGHT;
    public static final int TOOLBAR_BORDER_WIDTH = 3;
    public static final double [] line_thicknesses = new double []{
            4, 7,
            10, 14
    };
    public static final double [] line_styles = new double[]{
            1d, 1d,
            4d, 8d,
            2d, 4d,
            1d, 2d
    };

    // CANVAS VIEW
    public static final int CANVAS_WIDTH = WINDOW_WIDTH - TOOLBAR_WIDTH;    // Offset for toolbar
    public static final int CANVAS_HEIGHT = WINDOW_HEIGHT;

    // Images
    public static Image select_image = new Image("images/cursor.png");
    public static Image eraser_image = new Image("images/eraser.png");
    public static Image line_image = new Image("images/minus.png");
    public static Image circle_image = new Image("images/circle.png");
    public static Image bucket_image = new Image("images/paint-bucket.png");
    public static Image rectangle_image = new Image("images/rectangular.png");
}
