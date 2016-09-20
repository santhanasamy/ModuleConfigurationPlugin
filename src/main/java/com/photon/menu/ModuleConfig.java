package com.photon.menu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.photon.utils.Constants;

public class ModuleConfig extends AnAction {
    public ModuleConfig() {
        super(ModuleConfig.class.getSimpleName());
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);

        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow(Constants.MODULE_CONFIG_TOOL_WINDOW_ID);

        if (null != window) {
            //window.setIcon(CustomIconLoader.DEMO_ACTION);
            window.activate(null, true);
        }

        //Messages.showMessageDialog(project, "Hello world!", "Greeting", Messages.getInformationIcon());
    }
}