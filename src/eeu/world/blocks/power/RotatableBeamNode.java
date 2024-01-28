package eeu.world.blocks.power;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import eeu.ui.tables.RotationButton;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.core.Renderer;
import mindustry.core.World;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.BlockUnitc;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.world.Tile;
import mindustry.world.blocks.ControlBlock;
import mindustry.world.blocks.power.BeamNode;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.meta.BlockStatus;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class RotatableBeamNode extends BeamNode {
    public float rotateSpeed = 1f;
    public float shootY = 0f;
    private TextureRegion topRegion;

    public RotatableBeamNode(String name) {
        super(name);
        consumesPower = outputsPower = true;
        configurable = true;
        outlineIcon = true;
        lockRotation = false;
        rotate = true;
        rotateDraw = false;
        drawArrow = true;
        config(Float.class, (e, v) -> {
            if (e instanceof RotatableBeamNodeBuild b) {
                b.ang = b.rotation() * 90 + v;
                b.rotationButton.angle = b.ang;
            }
        });
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{region, topRegion};
    }

    @Override
    public TextureRegion getPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        return region;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Lines.stroke(1f);
        Draw.rect(topRegion, x * tilesize, y * tilesize, rotation * 90 - 90);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range * tilesize, Pal.placing);
    }

    @Override
    public void flipRotation(BuildPlan req, boolean x) {
        super.flipRotation(req, x);
        if (req.config instanceof Float f) req.config = f - 2 * f;//why?
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
        if (!(plan.config instanceof Float f)) return;
        Draw.rect(topRegion, plan.drawx(), plan.drawy(), plan.rotation * 90 - 90 + f);
        Tmp.v1.trns(plan.rotation * 90 + f, range * tilesize).add(plan.drawx(), plan.drawy());
        Color color = Pal.accent;
        Drawf.dashLine(color, plan.drawx(), plan.drawy(), Tmp.v1.x, Tmp.v1.y);
        Draw.color(color, 0.25f);
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation) {
        Placement.calculateNodes(points, this, rotation, (point, other) -> Mathf.within(point.x, point.y, other.x, other.y, range));
    }

    public class RotatableBeamNodeBuild extends Building implements ControlBlock {
        public int lastChange = -2;
        public Building toLink = null;
        public Tile toDest = null;
        public float rotation = 0;
        public float ang = 0;
        public RotationButton rotationButton = new RotationButton();
        private int dPos = -1;
        public @Nullable BlockUnitc unit;

        @Override
        public Unit unit() {
            if (unit == null) {
                unit = (BlockUnitc) UnitTypes.block.create(team);
                unit.tile(this);
            }
            return (Unit) unit;
        }

        @Override
        public boolean shouldAutoTarget() {
            return false;
        }

        @Override
        public void updateTile() {
            if (ang != rotation) updateLine();
            if (lastChange != world.tileChanges) {
                lastChange = world.tileChanges;
                updateLine();
            }
            if (unit != null && isControlled()) {
                unit.health(health);
                unit.team(team);
                unit.set(x, y);
                configure(Angles.angle(unit.aimX() - x, unit.aimY() - y) - rotation() * 90f);
            }
            rotation = Angles.moveToward(rotation, ang, rotateSpeed);
        }

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            RotatableBeamNodeBuild build = (RotatableBeamNodeBuild) super.init(tile, team, shouldAdd, rotation);
            float ang = rotation * 90;
            build.ang = ang;
            build.rotation = ang;
            build.rotationButton.setAngle(ang);
            return build;
        }

        @Override
        public Object config() {
            return ang - rotation() * 90f;
        }

        @Override
        public void buildConfiguration(Table table) {
            table.add(rotationButton).size(100f).update(b -> {
                if (ang != b.angle) {
                    configure(b.angle - rotation() * 90);
                }
            });
        }

        @Override
        public void updateTableAlign(Table table) {
            Vec2 pos = Core.input.mouseScreen(this.x, this.y);
            rotationButton.setSize(block.size * tilesize * 2f * Vars.renderer.getDisplayScale());
            table.setPosition(pos.x, pos.y, Align.center);
            rotationButton.setPosition(table.getWidth() / 2f, table.getHeight() / 2f, Align.center);
        }

        @Override
        public void drawConfigure() {
            float tileX = x;
            float tileY = y;
            Tmp.v1.trns(rotation, range * tilesize).add(tileX, tileY);
            Color color = Pal.accent;
            Drawf.dashLine(color, tileX, tileY, Tmp.v1.x, Tmp.v1.y);
            Draw.color(color, 0.25f);
            if (toDest == null) return;
            Rect rect = Tmp.r1;
            rect.setCentered(toDest.x * tilesize, toDest.y * tilesize, toDest.block().size * tilesize);
            Fill.rect(rect);
        }

        @Override
        public void draw() {
            super.draw();
            Draw.rect(topRegion, x, y, rotation - 90f);
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
                    if (toLink != other) setConnection(other.pos());
                    Vec2 hv = Geometry.raycastRect(v1.x + x, v1.y + y, v2.x, v2.y, Tmp.r1.setCentered(toLink.x, toLink.y, toLink.block.size * tilesize));
                    if (hv == null) {
                        if (toLink != null || toDest != null) setConnection(null);
                        return false;
                    }
                    return true;
                }
                if ((toLink != null || toDest != null) && other == null) setConnection(null);
                return false;
            });
            //cvcvcvcvcvcvcvcv
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

        public void setConnection(Building building) {
            toLink = building;
            toDest = building == null ? null : building.tile;
        }

        public void setConnection(int pos) {
            setConnection(world.build((short) (pos >>> 16), (short) (pos & 0xFFFF)));
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
            ang = rotation;
            boolean bool = read.bool();
            int pos = read.i();
            if (!bool) {
                dPos = pos;
            }
        }
    }
}
