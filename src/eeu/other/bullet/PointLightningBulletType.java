package eeu.other.bullet;

import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Unit;

public class PointLightningBulletType extends BulletType {
    private static float cdist = 0f;
    private static Unit result;
    public Effect lightningEffect = Fx.chainLightning;
    public float findRange = 40f;
    public int findAmount = 2;
    public int findChainLength = 6;

    public PointLightningBulletType() {
        scaleLife = true;
        lifetime = 100f;
        collides = false;
        reflectable = false;
        keepVelocity = false;
        backMove = false;
        pierce = true;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);

        float px = b.x + b.lifetime * b.vel.x,
                py = b.y + b.lifetime * b.vel.y;
        lightningEffect.at(b.x, b.y, 0f, new Vec2(px, py));
        b.time = b.lifetime;
        b.set(px, py);

        //calculate hit entity

        cdist = 0f;
        result = null;
        float range = 1f;

        Units.nearbyEnemies(b.team, px - range, py - range, range * 2f, range * 2f, e -> {
            if (e.dead() || !e.checkTarget(collidesAir, collidesGround) || !e.hittable()) return;

            e.hitbox(Tmp.r1);
            if (!Tmp.r1.contains(px, py)) return;

            float dst = e.dst(px, py) - e.hitSize;
            if ((result == null || dst < cdist)) {
                result = e;
                cdist = dst;
            }
        });

        if (result != null) {
            b.collided.add(result.id);
            b.collision(result, px, py);
        } else if (collidesTiles) {
            Building build = Vars.world.buildWorld(px, py);
            if (build != null && build.team != b.team) {
                build.collision(b);
            }
        }

        b.remove();

        b.vel.setZero();
    }

    public Seq<Unit> nextChain(Bullet b, float x, float y) {
        Seq<Unit> s = Groups.unit.intersect(x - findRange, y - findRange, findRange * 2f, findRange * 2f)
                .filter(u -> u.within(x, y, findRange) && u.team() != b.team() && !(u.dead() || !u.hittable()) && !b.collided.contains(u.id))
                .sort(u -> Mathf.dst(u.x, u.y, x, y)).copy();
        if (s.isEmpty()) return s;
        if (s.size - 1 >= findAmount) s.removeRange(findAmount, s.size - 1);
        s.each(u -> b.collided.add(u.id));
        return s;
    }

    /*public void hitChain(Bullet b, float x, float y, int chainLength){
        if (chainLength > 0){
            Seq<Unit> s = Groups.unit.intersect(x - findRange, y - findRange, findRange * 2f, findRange * 2f)
                    .filter(u -> u.team() != b.team() && !(u.dead() || !u.hittable()))
                    .sort(u -> -Mathf.dst(u.x, u.y, x, y)).copy();
            s.remove(0);
            if (s.isEmpty()) return;
            for (int i = 0; i < findAmount; i++){
                s.filter(u -> u.within(x, y, findRange) && !b.collided.contains(u.id));
                if (s.isEmpty()) break;
                Unit u = s.pop();
                b.collided.add(u.id);
                lightningEffect.at(x, y, 0f, u);
                hitChain(b, u.x, u.y, chainLength - 1);
                hitEntity(b, u, u.health());
            }
        }
    }*/
    @Override
    public void hit(Bullet b, float x, float y) {
        super.hit(b, x, y);
        /*Seq<Unit> s = Groups.unit.intersect(x - findRange, y - findRange, findRange * 2f, findRange * 2f)
                .filter(u -> u.team() != b.team() && !(u.dead() || !u.hittable()))
                .sort(u -> -Mathf.dst(u.x, u.y, x, y)).copy();
        s.remove(0);
        if (s.isEmpty()) return;
        for (int i = 0; i < findAmount; i++){
            s.filter(u -> u.within(x, y, findRange) && !b.collided.contains(u.id));
            if (s.isEmpty()) break;
            Unit u = s.pop();
            b.collided.add(u.id);
            lightningEffect.at(x, y, 0f, u);
            hitChain(b, u.x, u.y, findChainLength - 1);
            hitEntity(b, u, u.health());
        }*/
        ObjectMap<Position, Seq<Unit>> map = new ObjectMap<>();
        map.put(b, nextChain(b, x, y));
        for (int i = 0; i < findChainLength; i++) {
            ObjectMap<Position, Seq<Unit>> tmp = new ObjectMap<>();
            map.each((k, v) -> {
                v.each(u -> {
                    lightningEffect.at(k.getX(), k.getY(), 0f, u);
                    hitEntity(b, u, u.health());
                });
                v.each(u -> tmp.put(u, nextChain(b, u.x, u.y)));
            });
            if (tmp.values().toSeq().count(Seq::isEmpty) == tmp.size) break;
            map = tmp;
        }
        b.remove();
    }
}
