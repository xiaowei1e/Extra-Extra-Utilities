package eeu.other.pattern;

import arc.math.Angles;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.entities.pattern.ShootPattern;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.world.blocks.defense.turrets.Turret;

public class ShootMissile extends ShootPattern {
    protected static Seq<Bullet> seq = new Seq<>();
    public float rotateSpeed = 1f;
    public float arriveRotSpeed = 0.5f;
    public float rotateDelay = 20f;
    public float range = 1f;
    public boolean arriveRemove = false;
    public boolean lock = false;

    public ShootMissile() {

    }

    public ShootMissile(float rotateSpeed) {
        this(rotateSpeed, 20f);
    }

    public ShootMissile(float rotateSpeed, float rotateDelay) {
        this.rotateSpeed = rotateSpeed;
        this.rotateDelay = rotateDelay;
    }

    public ShootMissile lock() {
        this.lock = true;
        return this;
    }

    @Override
    public void shoot(int totalShots, BulletHandler handler, @Nullable Runnable barrelIncrementer) {
        for (int i = 0; i < shots; i++) {
            handler.shoot(0, 0, 0, shotDelay, b -> {
                Vec2 aim = getAim(b);
                if (aim == null) return;
                boolean a = b.within(aim, range);
                if (a && !seq.contains(b)) seq.add(b);
                if (b.time() > rotateDelay)
                    b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(aim.x, aim.y), (seq.contains(b) ? arriveRotSpeed : rotateSpeed) * Time.delta));
                if (arriveRemove && a) b.remove();
                seq.remove(bullet -> !bullet.isAdded());
            });
        }
    }

    @Nullable
    protected Vec2 getAim(Bullet bullet) {
        if (lock) return new Vec2(bullet.aimX(), bullet.aimY());
        if (bullet.owner instanceof Turret.TurretBuild t) return t.targetPos;
        if (bullet.owner instanceof Unit u) return new Vec2(u.aimX(), u.aimY());
        return null;
    }
}
