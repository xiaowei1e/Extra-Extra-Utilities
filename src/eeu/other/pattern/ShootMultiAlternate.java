package eeu.other.pattern;

import arc.util.Nullable;
import mindustry.entities.pattern.ShootPattern;

public class ShootMultiAlternate extends ShootPattern {
    public ShootPattern source;
    public ShootPattern[] dest;
    public int[] order;

    public ShootMultiAlternate(ShootPattern source, ShootPattern... shoots) {
        int[] tmp = new int[shoots.length];
        for (int i = 0; i < tmp.length; i++) tmp[i] = i;
        this.source = source;
        this.order = tmp;
        dest = shoots;
    }

    public ShootMultiAlternate(ShootPattern source, int[] order, ShootPattern... shoots) {
        this.source = source;
        this.order = order;
        dest = shoots;
    }

    @Override
    public void shoot(int totalShots, BulletHandler handler, @Nullable Runnable barrelIncrementer) {
        int mod = totalShots % order.length;
        int mode = -totalShots % order.length;
        int index = ((mod == order.length) || (mod > order.length - 1)) ? mode : mod;
        source.shoot(totalShots, (x, y, rotation, delay, move) -> {
            ShootPattern p = dest[order[index]];
            p.shoot(totalShots, (x2, y2, rot2, delay2, mover) -> {
                handler.shoot(x + x2, y + y2, rotation + rot2, delay + delay2, move == null && mover == null ? null : b -> {
                    if (move != null) move.move(b);
                    if (mover != null) mover.move(b);
                });
            }, null);
        }, null);
        if (barrelIncrementer != null) barrelIncrementer.run();
    }

    @Override
    public void flip() {
        source = source.copy();
        source.flip();
        dest = dest.clone();
        for (int i = 0; i < dest.length; i++) {
            dest[i] = dest[i].copy();
            dest[i].flip();
        }
    }
}
