package eeu.easterEgg;

import ExtraUtilities.worlds.blocks.turret.Prism;
import mindustry.Vars;

public class EggLoader {
    public static Prism prism;

    public static void load() {
        prism = (Prism) Vars.content.block("extra-utilities-prism");
        prism.description = "{rainbow}" + prism.description;
    }
}
