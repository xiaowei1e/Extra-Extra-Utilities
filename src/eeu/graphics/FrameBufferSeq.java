package eeu.graphics;

import arc.func.Boolp;
import arc.graphics.Color;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.graphics.Shaders;

public class FrameBufferSeq {
    private static final Boolp True = () -> true;
    public Seq<FrameBuffer> buffers = new Seq<>();
    public ObjectMap<FrameBuffer, BufferData> bufferDatas = new ObjectMap<>();

    public void begin() {
        buffers.each(b -> {
            if (bufferDatas.get(b).enable.get()) b.begin(Color.clear);
        });
    }

    public void end() {
        buffers.reverse();
        buffers.each(b -> {
            if (bufferDatas.get(b).enable.get()) b.end();
        });
        buffers.reverse();
    }

    public void blit() {
        bufferDatas.each((b, d) -> {
            if (d.enable.get()) b.blit(d.shader);
        });
    }

    public void resize(int width, int height) {
        buffers.each(b -> b.resize(width, height));
    }

    public void put(FrameBuffer buffer, Shader shader, Boolp enable) {
        buffers.add(buffer);
        bufferDatas.put(buffer, new BufferData(buffer, shader, enable));
    }

    public void put(FrameBuffer buffer, Shader shader) {
        buffers.add(buffer);
        bufferDatas.put(buffer, new BufferData(buffer, shader, True));
    }

    public void put(FrameBuffer buffer) {
        buffers.add(buffer);
        bufferDatas.put(buffer, new BufferData(buffer, Shaders.screenspace, True));
    }

    public void setShader(FrameBuffer buffer, Shader shader) {
        bufferDatas.get(buffer).shader = shader;
    }

    public void remove(FrameBuffer buffer) {
        buffers.remove(buffer);
        bufferDatas.remove(buffer);
    }

    public static class BufferData {
        public FrameBuffer buffer;
        public Shader shader;
        public Boolp enable;

        public BufferData(FrameBuffer buffer, Shader shader, Boolp enable) {
            this.buffer = buffer;
            this.shader = shader;
            this.enable = enable;
        }
    }
}
