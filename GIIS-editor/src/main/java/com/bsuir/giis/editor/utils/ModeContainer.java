package com.bsuir.giis.editor.utils;

import com.bsuir.giis.editor.service.flow.Mode;
//need to mutate tool in lambda function in BottomPanelController
public class ModeContainer {
    private Mode mode;
    public ModeContainer(Mode mode) {
        this.mode = mode;
    }
    public Mode getMode() {
        return mode;
    }
    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
