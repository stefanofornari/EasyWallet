package ste.w3.easywallet.ui;


import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.mfxresources.font.FontResources;

/**
 *
 */
public enum SortingIcons {
    ASCENDING(FontResources.CARET_UP.getDescription()),
    DESCENDING(FontResources.CARET_DOWN.getDescription()),
    NONE(FontResources.HYPHEN.getDescription());

    public final String symbol;

    SortingIcons(final String symbol) {
        this.symbol = symbol;
    }

    public MFXFontIcon newIcon() {
        return new MFXFontIcon(symbol);
    }
}
