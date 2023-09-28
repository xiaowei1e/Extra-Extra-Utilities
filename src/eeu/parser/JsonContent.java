package eeu.parser;

import arc.struct.ObjectMap;
import mindustry.ctype.UnlockableContent;

public class JsonContent {
    public static ObjectMap<String, UnlockableContent> contents = new ObjectMap<>();

    public static UnlockableContent get(String name) {
        return contents.get(name);
    }
}
