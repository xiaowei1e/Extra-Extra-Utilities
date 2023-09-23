package eeu.override;

import eeu.override.ui.dialogs.EEUContentInfoDialog;

public class UIOverride {
    public EEUContentInfoDialog content;

    public UIOverride() {
        content = new EEUContentInfoDialog();
        //override();
    }

    public void override() {
        content.override();
    }
}
