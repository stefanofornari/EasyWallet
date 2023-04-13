package ste.w3.easywallet.ui;


import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

/**
 *
 */
public enum SortingIcons {
    ASCENDING(
        Constants.ICON_CARET_UP,
        "m7.247 4.86-4.796 5.481c-.566.647-.106 1.659.753 1.659h9.592a1 1 0 0 0 .753-1.659l-4.796-5.48a1 1 0 0 0-1.506 0z",
        12, 8
    ),
    DESCENDING(
        Constants.ICON_CARET_DOWN,
        "M98,190.06,237.78,353.18a24,24,0,0,0,36.44,0L414,190.06c13.34-15.57,2.28-39.62-18.22-39.62H116.18C95.68,150.44,84.62,174.49,98,190.06Z",
        12, 8
    ),
    NONE(
        Constants.ICON_HYPHEN, "M13.875 12.906v2.281h-8.563v-2.281h8.563z",
        12, 4
    );

    public final String key, svg;
    public final int width, height;

    SortingIcons(final String key, final String svg, final int width, final int height) {
        this.key = key;
        this.svg = svg;
        this.width = width;
        this.height = height;
    }

    public Label newIcon() {
        SVGPath path = new SVGPath();
        path.setContent(svg);
        final Region shape = new Region();
        shape.setShape(path);
        shape.setMinSize(width, height);
        shape.setPrefSize(width, height);
        shape.setMaxSize(width, height);
        shape.getStyleClass().add("icon-button");
        Label icon = new Label(key, shape);
        icon.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        return icon;
    }
}
