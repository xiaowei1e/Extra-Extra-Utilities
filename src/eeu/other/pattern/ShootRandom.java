package eeu.other.pattern;

import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.entities.pattern.ShootPattern;

public class ShootRandom extends ShootPattern {
    public float randX = 0;
    public float randY = 0;
    public float randRot = 0;

    public ShootRandom(float x, float y, float rot) {
        randX = x;
        randY = y;
        randRot = rot;
    }

    @Override
    public void shoot(int totalShots, BulletHandler handler, @Nullable Runnable barrelIncrementer) {
        for (int i = 0; i < shots; i++) {
            handler.shoot(Mathf.range(randX), Mathf.range(randY), Mathf.range(randRot), firstShotDelay + shotDelay * i);
        }
    }
}
