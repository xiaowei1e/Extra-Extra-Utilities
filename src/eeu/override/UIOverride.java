package eeu.override;

import eeu.override.ui.dialogs.EEUContentInfoDialog;

public class UIOverride implements Overridable {
    public EEUContentInfoDialog content;

    public UIOverride() {
        content = new EEUContentInfoDialog();
        //override();
    }

    @Override
    public void override() {
        content.override();
    }
}
