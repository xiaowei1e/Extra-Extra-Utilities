package eeu.content;

import mindustry.content.Blocks;

import static ExtraUtilities.content.EUTechTree.addToNode;
import static ExtraUtilities.content.EUTechTree.node;

public class EEUTechTree {
    public static void load() {
        addToNode(Blocks.titaniumWall, () -> {
            node(EEUBlocks.crispWall, () -> {
                node(EEUBlocks.crispWallLarge);
            });
        });
        addToNode(Blocks.beamNode, () -> {
            node(EEUBlocks.rotatableBeamNode);
        });
    }
}
