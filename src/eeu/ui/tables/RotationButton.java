package eeu.ui.tables;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.input.KeyCode;
import arc.math.geom.Vec2;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Table;
import arc.util.Tmp;
import mindustry.graphics.Pal;

public class RotationButton extends Table {
    public Color circleColor = Pal.accentBack;
    public Color pointColor = Pal.accent;
    public float pointLength = 20;
    public float angle = 0f;
    public boolean isTouched = false;
    public Vec2 center = new Vec2();
    public Vec2 touchPoint = new Vec2();

    public RotationButton() {
        touchable = Touchable.enabled;
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
                if (isTouched) return false;
                setTouchPoint(x, y - height / 2);
                setAngle(touchPoint.angle());
                isTouched = true;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                setTouchPoint(x, y - height / 2);
                setAngle(touchPoint.angle());
                isTouched = true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {
                isTouched = false;
            }
        });
    }

    void setTouchPoint(float x, float y) {
        touchPoint.set(x, y).sub(center).add(0, height / 2f);
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getCircleSize() {
        return Math.min(width, height);
    }

    public Table setCircleColor(Color color) {
        circleColor = color;
        return this;
    }

    public Table setPointColor(Color color) {
        pointColor = color;
        return this;
    }

    @Override
    public void layout() {
        super.layout();
        center.set(x + width / 2f, y + height / 2f);
    }

    @Override
    public void draw() {
        super.draw();
        Draw.color(circleColor);
        Lines.stroke(3);
        Lines.circle(center.x, center.y, getCircleSize() / 2);
        Vec2 v1 = Tmp.v1.trns(angle, (getCircleSize() + pointLength) / 2f);
        Draw.color(pointColor);
        Lines.stroke(isTouched ? 5 : 3);
        Lines.line(v1.x + center.x, v1.y + center.y, center.x, center.y);
    }
}
