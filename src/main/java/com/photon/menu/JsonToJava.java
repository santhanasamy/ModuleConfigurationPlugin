package com.photon.menu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.photon.ui.Json2JavaDialog;
import com.photon.ui.Plist2JsonDialog;

public class JsonToJava extends AnAction {


    public JsonToJava() {
        super(JsonToJava.class.getSimpleName());
    }

    public void actionPerformed(AnActionEvent event) {
        Json2JavaDialog lDialog = new Json2JavaDialog(new javax.swing.JFrame(), true, event.getProject());
        lDialog.setVisible(true);
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
    }
}