package eeu.ui;

import arc.graphics.Color;
import mindustry.graphics.Pal;

import static eeu.ui.tables.RotationButton.RotationButtonStyle;

public class EEUStyles {
    public static RotationButtonStyle defaultrb;

    public static void load() {
        defaultrb = new RotationButtonStyle(Pal.gray, Color.black.cpy().a(0.3f), Pal.accent);
    }
}
