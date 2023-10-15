package eeu.content;

import arc.Core;
import arc.files.Fi;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import eeu.EEU;

public class EEUShaders {
    public static GravityShader gravity;

    public static void load() {
        gravity = new GravityShader();
    }

    public static class GravityShader extends EEULoadShader {
        private static final FrameBuffer buffer = new FrameBuffer();
        protected Seq<GravitySource> sources = new Seq<>(256);
        protected FloatSeq data = new FloatSeq();

        public GravityShader() {
            super("normal.vert", "gravity.frag");
            EEU.draw.overriders.put(buffer, this, () -> !sources.isEmpty());
        }

        @Override
        public void apply() {
            setUniformi("u_gravitySource_count", sources.size);
            if (!sources.isEmpty()) {
                setUniformf("u_resolution", Core.camera.width, Core.camera.height);
                setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2f, Core.camera.position.y - Core.camera.height / 2f);
                data.clear();
                sources.each(i -> data.add(i.x, i.y, i.radius, i.power));
                setUniform4fv("u_gravitySource", data.items, 0, data.size);
            }
        }

        public void add(float x, float y, float radius) {
            add(x, y, radius, 1);
        }

        public void add(float x, float y, float radius, float power) {
            sources.add(new GravitySource(x, y, radius, power));
        }

        public static class GravitySource {
            public float x;
            public float y;
            public float radius;
            public float power;

            public GravitySource(float x, float y, float radius, float power) {
                this.x = x;
                this.y = y;
                this.radius = radius;
                this.power = power;
            }
        }
    }

    public static class EEULoadShader extends Shader {
        public EEULoadShader(String vertexShader, String fragmentShader) {
            super(getShaderFi(vertexShader), getShaderFi(fragmentShader));
        }
    }

    public static Fi getShaderFi(String name) {
        return EEU.getInternalFile("shaders").child(name);
    }
}
