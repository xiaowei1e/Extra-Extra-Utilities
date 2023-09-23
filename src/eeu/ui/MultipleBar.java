package eeu.ui;

import arc.Core;
import arc.func.Floatp;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.graphics.g2d.ScissorStack;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.scene.style.Drawable;
import arc.scene.ui.layout.Scl;
import arc.util.pooling.Pools;
import mindustry.gen.Tex;
import mindustry.ui.Bar;
import mindustry.ui.Fonts;

import java.util.Arrays;

public class MultipleBar extends Bar {
    private static Rect scissor = new Rect();

    private Floatp[] fraction;
    private CharSequence name = "";
    private float[] value, lastValue, sortValue;
    private float[] computed;
    private float outlineRadius, blink;
    private Color blinkColor = new Color(), outlineColor = new Color();
    private Color[] colors, sortColors;

    public MultipleBar(String name, Color[] color, Floatp[] fraction){
        this.fraction = fraction;
        this.name = Core.bundle.get(name, name);
        this.blinkColor.set(color[0]);
        value = new float[fraction.length];
        lastValue = new float[fraction.length];
        sortValue = new float[fraction.length];
        for(int i = 0; i < fraction.length; i++){
            lastValue[i] = value[i] = fraction[i].get();
        }
        setColor(color[0]);
        colors = color;
        sortColors = colors.clone();
    }

    public MultipleBar(Prov<CharSequence> name, Prov<Color[]> color, Floatp[] fraction){
        this.fraction = fraction;
        value = new float[fraction.length];
        lastValue = new float[fraction.length];
        sortValue = new float[fraction.length];
        computed = new float[fraction.length];
        for(int i = 0; i < fraction.length; i++){
            lastValue[i] = value[i] = Mathf.clamp(fraction[i].get());
        }
        update(() -> {
            this.name = name.get();
            this.blinkColor.set(color.get()[0]);
            setColor(color.get()[0]);
            colors = color.get();
            sortColors = colors.clone();
        });
    }

    public MultipleBar(){
        super();
    }

    public void reset(float[] value){
        blink = value[0];
        this.value = lastValue = value;
    }

    public void set(Prov<String> name, Floatp[] fraction, Color[] color){
        this.fraction = fraction;
        value = new float[fraction.length];
        lastValue = new float[fraction.length];
        sortValue = new float[fraction.length];
        for(int i = 0; i < fraction.length; i++){
            lastValue[i] = value[i] = fraction[i].get();
        }
        this.blinkColor.set(color[0]);
        setColor(color[0]);
        colors = color;
        sortColors = colors.clone();
        update(() -> this.name = name.get());
    }

    public void snap(){
        value = new float[fraction.length];
        lastValue = new float[fraction.length];
        sortValue = new float[fraction.length];
        for(int i = 0; i < fraction.length; i++){
            lastValue[i] = value[i] = fraction[i].get();
        }
    }

    public Bar outline(Color color, float stroke){
        outlineColor.set(color);
        outlineRadius = Scl.scl(stroke);
        return this;
    }

    public void flash(){
        blink = 1f;
    }

    public Bar blink(Color color){
        blinkColor.set(color);
        return this;
    }

    @Override
    public void draw(){
        if(fraction == null) return;

        for(int i = 0; i < fraction.length; i++){
            computed[i] = Mathf.clamp(fraction[i].get());
        }


        for(int i = 0; i < computed.length; i++) {
            blink = 1f;
            if (lastValue[i] > computed[i]) {
                lastValue = computed;
            }
            if (Float.isNaN(lastValue[i])) lastValue[i] = 0;
            if (Float.isInfinite(lastValue[i])) lastValue[i] = 1f;
            if (Float.isNaN(value[i])) value[i] = 0;
            if (Float.isInfinite(value[i])) value[i] = 1f;
            if (Float.isNaN(computed[i])) computed[i] = 0;
            if (Float.isInfinite(computed[i])) computed[i] = 1f;
            blink = Mathf.lerpDelta(blink, 0f, 0.2f);
            value[i] = Mathf.lerpDelta(value[i], computed[i], 0.15f);
            Drawable bar = Tex.bar;

            if (outlineRadius > 0) {
                Draw.color(outlineColor);
                bar.draw(x - outlineRadius, y - outlineRadius, width + outlineRadius * 2, height + outlineRadius * 2);
            }
            Draw.colorl(0.1f);
            Draw.alpha(parentAlpha);
            bar.draw(x, y, width, height);
        }
        for(int i = 0; i < value.length; i++){
            sortValue[i] = value[i];
        }
        Arrays.sort(sortValue);
        Arrays.sort(sortColors, (a, b)->{
            float va = fraction[Arrays.asList(colors).indexOf(a)].get();
            float vb = fraction[Arrays.asList(colors).indexOf(b)].get();
            return Float.compare(va, vb);
        });
        for(int i = sortValue.length - 1; i >= 0; i--){
            Draw.color(sortColors[i], blinkColor, blink);
            Draw.alpha(parentAlpha);

            Drawable top = Tex.barTop;
            float topWidth = width * sortValue[i];

            if(topWidth > Core.atlas.find("bar-top").width){
                top.draw(x, y, topWidth, height);
            }else{
                if(ScissorStack.push(scissor.set(x, y, topWidth, height))){
                    top.draw(x, y, Core.atlas.find("bar-top").width, height);
                    ScissorStack.pop();
                }
            }

            Draw.color();
        }

        Font font = Fonts.outline;
        GlyphLayout lay = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        lay.setText(font, name);

        font.setColor(1f, 1f, 1f, 1f);
        font.getCache().clear();
        font.getCache().addText(name, x + width / 2f - lay.width / 2f, y + height / 2f + lay.height / 2f + 1);
        font.getCache().draw(parentAlpha);

        Pools.free(lay);
    }
}
