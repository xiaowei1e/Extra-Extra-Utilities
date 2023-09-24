package eeu.world.blocks;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import eeu.ui.tables.RotationButton;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Player;
import mindustry.gen.Tex;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;

public class ExtraBreaker extends Block {
    public int tileLength = 2;
    public int breakAngle = 30;

    public ExtraBreaker(String name) {
        super(name);
        rotate = true;
        drawArrow = false;
        update = destructible = true;
        rebuildable = false;
        configurable = true;
        config(float.class, (ExtraBreakerBuild build, Float config) -> build.rotTable.angle = config);
        config(Boolean.class, (ExtraBreakerBuild build, Boolean config) -> build.isBegin = config);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        float tileX = x * 8;
        float tileY = y * 8;
        float rot = rotation * 90 + 90;
        Tmp.v1.trns(rot + breakAngle / 2f, tileLength * 8).add(tileX, tileY);
        Color color = valid ? Pal.accent : Color.clear;
        Drawf.dashLine(color, tileX, tileY, Tmp.v1.x, Tmp.v1.y);
        Tmp.v1.trns(rot - breakAngle / 2f, tileLength * 8).add(tileX, tileY);
        Drawf.dashLine(color, tileX, tileY, Tmp.v1.x, Tmp.v1.y);
        Seq<Tile> tiles = getTiles(tileX, tileY, rot, tileLength, breakAngle);
        Draw.color(color, valid ? 0.25f : 0);
        tiles.each(tile -> {
            Rect rect = Tmp.r1;
            rect.setCentered(tile.x * 8, tile.y * 8, tile.block().size * 8);
            Fill.rect(rect);
        });
    }

    public Seq<Tile> getTiles(float x, float y, float rotation, int length, float angle) {
        Seq<Tile> tiles = new Seq<>();
        Geometry.circle(0, 0, length, (tx, ty) -> {
            Tile tile = Vars.world.tileWorld(tx * 8 + x, ty * 8 + y);
            if (Angles.near(rotation, Angles.angle(x, y, x + tx * 8, y + ty * 8), angle / 2f) && checkValid(tile))
                tiles.add(tile);
        });
        return tiles;
    }

    public boolean checkValid(Tile tile) {
        return tile != null && tile.block() != null && tile.build == null && tile.block().solid && !tile.block().breakable;
    }

    public class ExtraBreakerBuild extends Building {
        public float angle = 0;
        public float rot = 0;
        public boolean isBegin = false;
        public RotationButton rotTable = new RotationButton();
        public int tileChanges;
        public Seq<Tile> tiles;

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            ExtraBreakerBuild build = (ExtraBreakerBuild) super.init(tile, team, shouldAdd, rotation);
            float ang = rotation * 90 + 90;
            build.rot = ang;
            build.rotTable.setAngle(ang);
            build.rotTable.background(Tex.pane);
            return build;
        }

        @Override
        public void buildConfiguration(Table table) {
            table.add(rotTable).size(100f).update(b -> {
                configure(b.angle);
            }).row();
            table.button(Icon.up, () -> {
                isBegin = true;
            });
        }

        @Override
        public void drawConfigure() {
            tiles = getTiles(x, y, rot, tileLength, breakAngle);
            float tileX = x;
            float tileY = y;
            Tmp.v1.trns(rot + breakAngle / 2f, tileLength * 8).add(tileX, tileY);
            Color color = Pal.accent;
            Drawf.dashLine(color, tileX, tileY, Tmp.v1.x, Tmp.v1.y);
            Tmp.v1.trns(rot - breakAngle / 2f, tileLength * 8).add(tileX, tileY);
            Drawf.dashLine(color, tileX, tileY, Tmp.v1.x, Tmp.v1.y);
            Draw.color(color, 0.25f);
            tiles.each(tile -> {
                Rect rect = Tmp.r1;
                rect.setCentered(tile.x * 8, tile.y * 8, tile.block().size * 8);
                Fill.rect(rect);
            });
        }

        @Override
        public void updateTile() {
            if (tileChanges != Vars.world.tileChanges) {
                tileChanges = Vars.world.tileChanges;
                tiles = getTiles(x, y, rot, tileLength, breakAngle);
            }
            if (isBegin) {
                tiles.each(Tile::remove);
                kill();
            }
            rot = Angles.moveToward(rot, rotTable.angle, 1f);
        }

        @Override
        public boolean shouldShowConfigure(Player player) {
            return !isBegin;
        }

        @Override
        public boolean shouldHideConfigure(Player player) {
            return isBegin;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(rot);
            write.bool(isBegin);
            write.f(angle);
            write.f(rotTable.angle);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            rot = read.f();
            isBegin = read.bool();
            angle = read.f();
            rotTable.setAngle(read.f());
        }
    }
}
