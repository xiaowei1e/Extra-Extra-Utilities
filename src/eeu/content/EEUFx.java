package eeu.content;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Rand;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;

public class EEUFx {
    public static final Rand rand = new Rand();
    public static final Vec2 v = new Vec2();
    public static final Effect pLine = new Effect(20f, 300f, e -> {
        if (!(e.data instanceof Position p)) return;
        Lines.stroke(1f);
        Draw.color(e.color);
        Lines.line(e.x, e.y, p.getX(), p.getY());
        Tmp.v1.set(e.x, e.y).add(p).scl(0.5f);
        Drawf.tri(Tmp.v1.x, Tmp.v1.y, 4f, 4f, Angles.angle(e.x, e.y, p.getX(), p.getY()));
        Lines.circle(e.x, e.y, 4f);
        Lines.circle(p.getX(), p.getY(), 4f);
    }).followParent(false).rotWithParent(false);
}
