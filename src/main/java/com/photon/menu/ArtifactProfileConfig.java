package com.photon.menu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class ArtifactProfileConfig extends AnAction {
    public ArtifactProfileConfig() {
        super("Hello");
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        //Messages.showMessageDialog(project, "Hello world!", "Greeting", Messages.getInformationIcon());

    }
}