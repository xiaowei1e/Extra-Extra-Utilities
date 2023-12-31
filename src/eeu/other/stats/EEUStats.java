package eeu.other.stats;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class EEUStats {
    public static final Stat
            maxHandleDamage = new Stat("eeu-max-handle-damage", StatCat.general),
            restoreSpeed = new Stat("eeu-restore-speed", StatCat.general),
            formula = new Stat("eeu-formula", StatCat.crafting),
            heatOutput = new Stat("eeu-heat-output", StatCat.crafting),
            heatInput = new Stat("eeu-heat-input", StatCat.crafting);

}
