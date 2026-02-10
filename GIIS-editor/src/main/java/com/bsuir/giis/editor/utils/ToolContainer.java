package com.bsuir.giis.editor.utils;

import com.bsuir.giis.editor.controllers.handlers.DrawableHandler;
import com.bsuir.giis.editor.model.Tool;

public class ToolContainer {
    private Tool tool;
    private DrawableHandler handler;
    public ToolContainer(final Tool tool, final DrawableHandler handler) {
        this.tool = tool;
        this.handler = handler;
    }
    public DrawableHandler getHandler() {
        return handler;
    }
    public void setHandler(final DrawableHandler handler) {
        this.handler = handler;
    }
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
