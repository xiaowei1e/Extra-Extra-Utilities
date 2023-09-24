package eeu.parser;

import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.mod.Mods;
import mindustry.type.ErrorContent;

import java.util.Locale;

import static mindustry.Vars.content;
import static mindustry.Vars.mods;

public class JsonContentLoader {
    public static ContentParser parser = new ContentParser();

    public static void loadContent() {
        content.setCurrentMod(null);
        class LoadRun implements Comparable<LoadRun> {
            final ContentType type;
            final Fi file;
            final Mods.LoadedMod mod;

            public LoadRun(ContentType type, Fi file, Mods.LoadedMod mod) {
                this.type = type;
                this.file = file;
                this.mod = mod;
            }

            @Override
            public int compareTo(LoadRun l) {
                int mod = this.mod.name.compareTo(l.mod.name);
                if (mod != 0) return mod;
                return this.file.name().compareTo(l.file.name());
            }
        }

        Seq<LoadRun> runs = new Seq<>();

        for (Mods.LoadedMod mod : mods.orderedMods()) {
            if (mod.root.child("content_eeu").exists()) {
                Fi contentRoot = mod.root.child("content_eeu");
                for (ContentType type : ContentType.all) {
                    String lower = type.name().toLowerCase(Locale.ROOT);
                    Fi folder = contentRoot.child(lower + (lower.endsWith("s") ? "" : "s"));
                    if (folder.exists()) {
                        for (Fi file : folder.findAll(f -> f.extension().equals("json") || f.extension().equals("hjson"))) {
                            runs.add(new LoadRun(type, file, mod));
                        }
                    }
                }
            }
        }

        //make sure mod content is in proper order
        runs.sort();
        for (LoadRun l : runs) {
            Content current = content.getLastAdded();
            try {
                //this binds the content but does not load it entirely
                Content loaded = parser.parse(l.mod, l.file.nameWithoutExtension(), l.file.readString("UTF-8"), l.file, l.type);
                Log.debug("[@] Loaded '@'.", l.mod.meta.name, (loaded instanceof UnlockableContent u ? u.localizedName : loaded));
            } catch (Throwable e) {
                if (current != content.getLastAdded() && content.getLastAdded() != null) {
                    parser.markError(content.getLastAdded(), l.mod, l.file, e);
                } else {
                    ErrorContent error = new ErrorContent();
                    parser.markError(error, l.mod, l.file, e);
                }
            }
        }

        //this finishes parsing content fields
        parser.finishParsing();
    }
}
