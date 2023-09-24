package eeu.content;

import eeu.parser.JsonContentLoader;

public class EEUContentLoader {
    public static void load() {
        EEUBlocks.load();
        JsonContentLoader.loadContent();
    }
}
