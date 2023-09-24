package eeu.content;

import mindustry.content.Blocks;

import static ExtraUtilities.content.EUTechTree.addToNode;
import static ExtraUtilities.content.EUTechTree.node;
import static eeu.parser.JsonContent.get;

public class EEUTechTree {
    public static void load(){
        addToNode(Blocks.titaniumWall, () -> {
            node(get("eeu-crisp-steel-wall")/*EEUBlocks.crispWall*/, () -> {
                node(EEUBlocks.crispWallLarge);
            });
        });
    }
}
