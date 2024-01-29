package eeu.ui.tables;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.input.KeyCode;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.style.Style;
import arc.scene.ui.layout.Table;
import eeu.ui.EEUStyles;
import mindustry.Vars;
import mindustry.graphics.Drawf;

public class RotationButton extends Table {
    private float angle = 0f;
    public boolean isTouched = false;
    public Vec2 center = new Vec2();
    public Vec2 touchPoint = new Vec2();
    public RotationButtonStyle style;

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
    public void setCenter(float x, float y) {
        this.center.set(x, y);
    }

    void setTouchPoint(float x, float y) {
        touchPoint.set(x, y).sub(center);
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        if (Math.abs(angle * 2f - Mathf.floor(angle / 45f) * (45f * 2f) - 45f) < 45f - 1f / getCircleSize() * 16f * Mathf.radDeg) {
            this.angle = angle;
        } else {
            this.angle = Mathf.round(angle / 45f) * 45f;
        }
    }

    public float getCircleSize() {
        return Math.min(width, height);
    }

    public boolean checkValid(float x, float y) {
        return Mathf.within(x - center.x, y - center.y, getCircleSize() / 2f);
    }

    @Override
    public void layout() {
        super.layout();
    }
    @Override
    public void draw() {
        super.draw();
        setCenter(x + width / 2f, y + height / 2f);
        drawDial();
        drawPointer();
        style.drawTop(center.x, center.y, getCircleSize());
    }

    public void drawDial() {
        style.drawDial(center.x, center.y, getCircleSize());
    }

    public void drawPointer() {
        style.drawPointer(center.x, center.y, getCircleSize(), angle);
    }

    public static class RotationButtonStyle extends Style {
        public Color ringColor, backgroundColor, pointerColor;
        public float ringStroke = 0.5f, pointerWidth = 1f;

        public RotationButtonStyle(Color ring, Color background, Color pointer) {
            ringColor = ring;
            backgroundColor = background;
            pointerColor = pointer;
        }

        public void drawDial(float x, float y, float size) {
            Draw.color(backgroundColor);
            Fill.circle(x, y, size / 2f);
            Lines.stroke(ringStroke * Vars.renderer.getDisplayScale(), ringColor);
            Lines.circle(x, y, size / 2f + ringStroke / 2f);
        }

        public void drawPointer(float x, float y, float size, float angle) {
            Draw.color(pointerColor);
            Drawf.tri(x, y, pointerWidth * Vars.renderer.getDisplayScale(), size, angle);
            Drawf.tri(x, y, pointerWidth * Vars.renderer.getDisplayScale(), size * 0.1f, 180f + angle);
        }

        public void drawTop(float x, float y, float size) {
            Draw.color(ringColor);
            for (int i = 0; i < 360; i += 45) {
                Drawf.tri(x + Angles.trnsx(i, size / 2f), y + Angles.trnsy(i, size / 2f), pointerWidth * Vars.renderer.getDisplayScale(), size * 0.1f, 180f + i);
                Drawf.tri(x + Angles.trnsx(i, size / 2f), y + Angles.trnsy(i, size / 2f), pointerWidth * Vars.renderer.getDisplayScale(), size * 0.1f, i);
            }
        }
    }
}
