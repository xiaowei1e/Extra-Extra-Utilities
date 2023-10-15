package eeu;

import arc.Events;
import eeu.content.EEUShaders;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import mindustry.mod.Mods;


public class EEUMod extends Mod {
    public static String euVersion = "1.1.3.1-fixed";
    public Mods.LoadedMod mod;
    public Mods.LoadedMod eu;
    public EEU eeu;

    public EEUMod() {
        Events.on(EventType.ClientLoadEvent.class, e -> {
            eeu.loadAfter();
        });
    }

    @Override
    public void loadContent() {
        initInfo();
        eeu.load();
        EEUShaders.gravity.add(100 * 8, 100 * 8, 10 * 8, 1);
        //EggLoader.load();
    }

    public void initInfo() {
        mod = Vars.mods.getMod(EEUMod.class);
        eu = Vars.mods.getMod("extra-utilities");
        eeu = new EEU(mod);
        mod.meta.displayName = "更多实用设备拓展";
        mod.meta.description = """
                本mod为更多实用设备(EU)附属mod
                同时,本mod为更多实用设备扩展(EUE)的重置mod,也就是说,此mod前身为EUE
                本模组可能会稍微影响游戏平衡但不会破坏平衡
                计划会丰富EU内容,以及使游戏更人性化
                模组扩展方向:
                    一.群友想要,但可能会稍微影响平衡的东西
                    二.EU与原版建筑等东西的高T
                [gray]ps:以前总有人把扩展叫成拓展,虽然我现在改成拓展了,但我还是想骂之前那些人一句臭傻逼
                """.trim();
    }

    public boolean checkEUVersion(String version) {
        return version.equals(getEUVersion());
    }

    public String getEUVersion() {
        return eu.meta.version;
    }
}
