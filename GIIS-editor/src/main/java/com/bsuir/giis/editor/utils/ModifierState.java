package com.bsuir.giis.editor.utils;

public class ModifierState {
    private boolean shiftPressed;
    private boolean ctrlPressed;
    private boolean altPressed;

    public boolean isShiftPressed() {
        return shiftPressed;
    }

    public void setShiftPressed(boolean shiftPressed) {
        this.shiftPressed = shiftPressed;
    }

    public boolean isCtrlPressed() {
        return ctrlPressed;
    }

    public void setCtrlPressed(boolean ctrlPressed) {
        this.ctrlPressed = ctrlPressed;
    }

    public boolean isAltPressed() {
        return altPressed;
    }

    public void setAltPressed(boolean altPressed) {
        this.altPressed = altPressed;
    }

    public void reset() {
        shiftPressed = false;
        ctrlPressed = false;
        altPressed = false;
    }
}
