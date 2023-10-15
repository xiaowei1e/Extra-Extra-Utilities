package eeu.override;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.Draw;
import eeu.graphics.FrameBufferSeq;
import mindustry.game.EventType;

public class DrawOverride implements Overridable {
    public FrameBufferSeq overriders = new FrameBufferSeq();

    @Override
    public void override() {
        Events.run(EventType.Trigger.preDraw, () -> {
            overriders.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
            overriders.begin();
        });
        Events.run(EventType.Trigger.postDraw, () -> {
            overriders.end();
            overriders.blit();
            Draw.flush();
        });
    }
}
