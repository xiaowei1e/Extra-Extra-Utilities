package eeu.content;

import mindustry.content.Blocks;
import mindustry.world.Block;

import static ExtraUtilities.content.EUTechTree.*;

public class EEUTechTree {
    public static void load(){
        addToNode(Blocks.titaniumWall, () -> {
            node(EEUBlocks.crispWall, () -> {
                node(EEUBlocks.crispWallLarge);
            });
        });
    }
}
