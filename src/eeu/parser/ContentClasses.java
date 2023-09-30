package eeu.parser;

import ExtraUtilities.content.*;
import arc.struct.ObjectMap;
import mindustry.content.*;

public class ContentClasses {
    public static final ObjectMap<String, Class<?>> classes = new ObjectMap<>();

    static {
        classes.put("Blocks", Blocks.class);
        classes.put("Bullets", Bullets.class);
        classes.put("Fx", Fx.class);
        classes.put("Items", Items.class);
        classes.put("Liquids", Liquids.class);
        classes.put("Planets", Planets.class);
        classes.put("StatusEffects", StatusEffects.class);
        classes.put("UnitTypes", UnitTypes.class);
        classes.put("Weathers", Weathers.class);
        classes.put("EUAttribute", EUAttribute.class);
        classes.put("EUBlocks", EUBlocks.class);
        classes.put("EUBulletsTypes", EUBulletTypes.class);
        classes.put("EUFx", EUFx.class);
        classes.put("EUItems", EUItems.class);
        classes.put("EUSounds", EUSounds.class);
        classes.put("EUStatusEffects", EUStatusEffects.class);
        classes.put("EUUnitTypes", EUUnitTypes.class);
    }
}
