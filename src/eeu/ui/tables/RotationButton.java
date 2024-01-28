package eeu.ui.tables;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.style.Style;
import arc.scene.ui.layout.Table;
import arc.util.Tmp;
import eeu.ui.EEUStyles;
import mindustry.graphics.Pal;

public class RotationButton extends Table {
    public Color circleColor = Pal.accentBack;
    public Color pointColor = Pal.accent;
    public float pointLength = 20;
    public float angle = 0f;
    public boolean isTouched = false;
    public Vec2 center = new Vec2();
    public Vec2 touchPoint = new Vec2();
    private RotationButtonStyle style;

    public RotationButton() {
        this(EEUStyles.defaultrb);
    }

    public RotationButton(RotationButtonStyle style) {
        touchable = Touchable.enabled;
        this.style = style;
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
                if (isTouched || !checkValid(x + RotationButton.this.x, y + RotationButton.this.y)) return false;
                setTouchPoint(x + RotationButton.this.x, y + RotationButton.this.y);
                setAngle(touchPoint.angle());
                isTouched = true;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                setTouchPoint(x + RotationButton.this.x, y + RotationButton.this.y);
                setAngle(touchPoint.angle());
                isTouched = true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {
                isTouched = false;
            }
        });
    }

    public void setCenter(Vec2 v) {
        setCenter(v.x, v.y);
    }

    public void setCenter(float x, float y) {
        this.center.set(x, y);
    }

    void setTouchPoint(float x, float y) {
        touchPoint.set(x, y).sub(center);
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getCircleSize() {
        return Math.min(width, height);
    }

    public boolean checkValid(float x, float y) {
        return Mathf.within(x - center.x, y - center.y, getCircleSize());
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
    }

    public RotationButtonStyle getStyle() {
        return style;
    }

    @Override
    public void draw() {
        super.draw();
        setCenter(x + width / 2f, y + height / 2f);
        Draw.color(style.backgroundColor);
        Fill.circle(center.x, center.y, getCircleSize() / 2f);
        Draw.color(style.ringColor);
        Lines.stroke(style.ringStroke);
        Lines.circle(center.x, center.y, getCircleSize() / 2f + style.ringStroke / 2f);
        Draw.color(circleColor);
        //Fill.circle(touchPoint.x + center.x, touchPoint.y + center.y, 8);
        //Lines.circle(center.x, center.y, getCircleSize() / 2);
        Vec2 v1 = Tmp.v1.trns(angle, (getCircleSize() + pointLength) / 2f);
        Draw.color(pointColor);
        Lines.stroke(isTouched ? 5 : 3);
        Lines.line(v1.x + center.x, v1.y + center.y, center.x, center.y);
    }

    public static class RotationButtonStyle extends Style {
        public Color ringColor, backgroundColor;
        public float ringStroke;

        public RotationButtonStyle(Color ring, Color background, float stroke) {
            ringColor = ring;
            backgroundColor = background;
            ringStroke = stroke;
        }
    }
}
