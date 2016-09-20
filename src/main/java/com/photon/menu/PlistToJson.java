package com.photon.menu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.photon.ui.Plist2JsonDialog;

public class PlistToJson extends AnAction {
    public PlistToJson() {
        super(PlistToJson.class.getSimpleName());
    }

    public void actionPerformed(AnActionEvent event) {

        Plist2JsonDialog lDialog = new Plist2JsonDialog(new javax.swing.JFrame(), true, event.getProject());
        lDialog.setVisible(true);
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
    }
}