package eeu;

import eeu.content.EEUContentLoader;
import eeu.content.EEUTechTree;
import eeu.override.UIOverride;
import mindustry.Vars;
import mindustry.mod.Mods;

public class EEU {
    public static UIOverride ui;
    public Mods.LoadedMod mod;
    public EEUMod main;

    public EEU(Mods.LoadedMod mod) {
        this.mod = mod;
        main = (EEUMod) mod.main;
    }

    public void load() {
        EEUContentLoader.load();
        EEUTechTree.load();
    }

    public void loadAfter() {
        ui = new UIOverride();
        if (!main.checkEUVersion(EEUMod.euVersion)) {
            String text = "注意,您当前使用的EU版本与此版本拓展开发所依赖的版本并不匹配(应为%s,但实际为%s),这可能会导致未知的错误.";
            Vars.ui.showInfoText("EU版本不匹配", String.format(text, EEUMod.euVersion, main.getEUVersion()));
        }
    }
}
