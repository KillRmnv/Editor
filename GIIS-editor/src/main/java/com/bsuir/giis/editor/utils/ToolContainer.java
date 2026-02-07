package com.bsuir.giis.editor.utils;

import com.bsuir.giis.editor.model.Tool;

public class ToolContainer {
    private Tool tool;
    public void setTool(Tool tool) {
        this.tool = tool;
    }
    public Tool getTool() {
        return tool;
    }
    public ToolContainer(Tool tool) {
        this.tool = tool;
    }
}
