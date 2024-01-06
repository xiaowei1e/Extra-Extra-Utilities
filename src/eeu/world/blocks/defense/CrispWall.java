package eeu.world.blocks.defense;

import arc.Core;
import arc.func.Floatp;
import arc.graphics.Color;
import arc.math.Mathf;
import eeu.other.stats.EEUStats;
import eeu.ui.tables.MultipleBar;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.StatUnit;

public class CrispWall extends Wall {
    public float maxHandleDamage = 100;
    public float restoreSpeed = 0.1f;
    public float damageMultiple = 0.2f;
    public CrispWall(String name) {
        super(name);
        update = true;
    }
    @Override
    public void setBars() {
        super.setBars();
        addBar("health", b -> {
            CrispWallBuilding cb = (CrispWallBuilding) b;
            return new MultipleBar(() -> Core.bundle.get("stat.health"), () -> new Color[]{Pal.health, Pal.shield}, new Floatp[]{b::healthf, () -> 1 - cb.damaged / maxHandleDamage}).blink(Color.white);
        });
    }
    @Override
    public void setStats() {
        super.setStats();
        stats.add(EEUStats.maxHandleDamage, maxHandleDamage);
        stats.add(EEUStats.restoreSpeed, restoreSpeed * 60f, StatUnit.perSecond);
    }

    public class CrispWallBuilding extends WallBuild {
        public float damaged = 0;
        @Override
        public void updateTile() {
            super.updateTile();
            if (damaged > 0) {
                damaged = Mathf.maxZero(damaged - restoreSpeed);
            }
        }
        @Override
        public float handleDamage(float amount) {
            damaged += amount * (1 - damageMultiple);
            if (amount > maxHandleDamage || damaged >= maxHandleDamage) kill();
            return super.handleDamage(amount * damageMultiple);
        }
    }
}
