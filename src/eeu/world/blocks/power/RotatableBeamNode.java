package eeu.world.blocks.power;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import eeu.ui.tables.RotationButton;
import mindustry.core.Renderer;
import mindustry.core.World;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Tex;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.world.Tile;
import mindustry.world.blocks.power.BeamNode;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.meta.BlockStatus;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class RotatableBeamNode extends BeamNode {
    public float rotateSpeed = 1f;
    public float shootY = 0f;

    public RotatableBeamNode(String name) {
        super(name);
        configurable = true;
        config(Float.class, (e, v) -> ((RotatableBeamNodeBuild) e).rotationButton.angle = v);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Lines.stroke(1f);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range * tilesize, Pal.placing);
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation) {
        Placement.calculateNodes(points, this, rotation, (point, other) -> Mathf.within(point.x, point.y, other.x, other.y, range));
    }

    public class RotatableBeamNodeBuild extends Building {
        public int lastChange = -2;
        public Building toLink = null;
        public Tile toDest = null;
        public float rotation = 0;
        private int dPos = -1;
        public RotationButton rotationButton = new RotationButton();

        @Override
        public void updateTile() {
            if (lastChange != world.tileChanges) {
                lastChange = world.tileChanges;
                updateLine();
            }
            rotation = Angles.moveToward(rotation, rotationButton.angle, rotateSpeed);
        }

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            RotatableBeamNodeBuild build = (RotatableBeamNodeBuild) super.init(tile, team, shouldAdd, rotation);
            float ang = rotation * 90 + 90;
            build.rotation = ang;
            build.rotationButton.setAngle(ang);
            build.rotationButton.background(Tex.pane);
            return build;
        }

        @Override
        public void buildConfiguration(Table table) {
            table.add(rotationButton).size(100f).update(b -> configure(b.angle));
        }

        @Override
        public void drawConfigure() {
            float tileX = x;
            float tileY = y;
            Tmp.v1.trns(rotation, range * tilesize).add(tileX, tileY);
            Color color = Pal.accent;
            Drawf.dashLine(color, tileX, tileY, Tmp.v1.x, Tmp.v1.y);
            Draw.color(color, 0.25f);
            updateLine();
            if (toDest == null) return;
            Rect rect = Tmp.r1;
            rect.setCentered(toDest.x * tilesize, toDest.y * tilesize, toDest.block().size * tilesize);
            Fill.rect(rect);
        }

        @Override
        public void draw() {
            super.draw();
            if (Mathf.zero(Renderer.laserOpacity) || toLink == null || toDest == null) return;
            Draw.z(Layer.power);
            Draw.color(laserColor1, laserColor2, (1f - power.graph.getSatisfaction()) * 0.86f + Mathf.absin(3f, 0.1f));
            Draw.alpha(Renderer.laserOpacity);
            float w = laserWidth + Mathf.absin(pulseScl, pulseMag);
            Vec2 v = Tmp.v1.trns(rotation, size / 2f + shootY).scl(tilesize).add(x, y);
            Vec2 v1 = Tmp.v2.trns(rotation, range * tilesize).add(v);
            Vec2 hv = Geometry.raycastRect(v.x, v.y, v1.x, v1.y, Tmp.r1.setCentered(toLink.x, toLink.y, toLink.block.size * tilesize));
            v1.set(hv == null ? Vec2.ZERO : hv);
            if (hv != null) Drawf.laser(laser, laserEnd, v.x, v.y, v1.x, v1.y, w);
            Draw.reset();
        }

        @Override
        public void pickedUp() {
            super.pickedUp();
            toLink = null;
            toDest = null;
        }

        @Override
        public BlockStatus status() {
            if (Mathf.equal(power.status, 0f, 0.001f)) return BlockStatus.noInput;
            if (Mathf.equal(power.status, 1f, 0.001f)) return BlockStatus.active;
            return BlockStatus.noOutput;
        }

        public void updateLine() {
            var prev = toLink;
            Vec2 v = Tmp.v1.trns(rotation, range * tilesize).add(x, y);
            World.raycastEachWorld(x, y, v.x, v.y, (wx, wy) -> {
                var other = world.build(wx, wy);
                Vec2 v1 = Tmp.v2.trns(rotation, size / 2f + shootY).scl(tilesize);
                Vec2 v2 = Tmp.v3.set(v1).setLength(v1.len() + range * tilesize).add(x, y);
                if (other != null && other != this && other.block.hasPower && other.block.connectedPower && other.team == team) {
                    if (other != prev && other.power.links.contains(pos()) && other.tile.pos() != dPos) {
                        return true;
                    }
                    if (dPos > -1) dPos = -1;
                    toLink = other;
                    toDest = other.tile;
                    Vec2 hv = Geometry.raycastRect(v1.x + x, v1.y + y, v2.x, v2.y, Tmp.r1.setCentered(toLink.x, toLink.y, toLink.block.size * tilesize));
                    if (hv == null) {
                        toLink = null;
                        toDest = null;
                        return false;
                    }
                    return true;
                }
                if (other == null) {
                    toLink = null;
                    toDest = null;
                }
                return false;
            });
            if (prev != toLink) {
                if (prev != null) {
                    prev.power.links.removeValue(pos());
                    power.links.removeValue(prev.pos());
                    prev.updatePowerGraph();

                    PowerGraph newgraph = new PowerGraph();
                    //reflow from this point, covering all tiles on this side
                    newgraph.reflow(this);

                    if (prev.power.graph != newgraph) {
                        //reflow power for other end
                        PowerGraph og = new PowerGraph();
                        og.reflow(prev);
                    }
                }
                //linked to a new one, connect graphs
                if (toLink != null) {
                    power.links.addUnique(toLink.pos());
                    toLink.power.links.addUnique(pos());
                    power.graph.addGraph(toLink.power.graph);
                }
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(rotation);
            write.bool(toDest == null);
            write.i(toDest == null ? 0 : toDest.pos());
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            rotationButton.setAngle(read.f());
            rotation = rotationButton.angle;
            boolean bool = read.bool();
            int pos = read.i();
            if (!bool) {
                dPos = pos;
            }
        }
    }
}
