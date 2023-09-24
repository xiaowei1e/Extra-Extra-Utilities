package eeu.parser;

import arc.struct.ObjectMap;
import eeu.world.blocks.crafter.MultipleCrafter;
import eeu.world.blocks.defense.CrispWall;
import mindustry.mod.ClassMap;

public class Classes {
    public static final ObjectMap<String, Class<?>> classes = new ObjectMap<>();

    static {
        classes.putAll(ClassMap.classes);
        classes.put("CrispWall", CrispWall.class);
        classes.put("MultipleCrafter", MultipleCrafter.class);
    }
}
