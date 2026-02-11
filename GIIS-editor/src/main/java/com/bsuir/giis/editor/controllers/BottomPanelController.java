package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.service.flow.Debug;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.view.BottomToolbar;
import com.bsuir.giis.editor.view.Canvas;

import javax.swing.*;
import java.awt.event.MouseAdapter;

public final class BottomPanelController  {
    public BottomPanelController(BottomToolbar bottomToolbar, ModeContainer mode, JFrame debugFrame, Canvas canvas) {
        bottomToolbar.getDebugModeButton().addActionListener(e -> {
            mode.setMode(new Debug());
            bottomToolbar.getDebugModeButton().setSelected(true);
            bottomToolbar.getRegularModeButton().setSelected(false);
            bottomToolbar.getNextStepButton().setEnabled(true);
            bottomToolbar.getSkipButton().setEnabled(true);
            bottomToolbar.getDebugFrameButton().setEnabled(true);
            canvas.getLayer2D().setPixelSizeFromField();
            canvas.getLayer2DMoveable().setPixelSizeFromField();

        });
        bottomToolbar.getRegularModeButton().addActionListener(e -> {
            mode.setMode(new Regular());
            bottomToolbar.getRegularModeButton().setSelected(true);
            bottomToolbar.getDebugModeButton().setSelected(false);
            bottomToolbar.getNextStepButton().setEnabled(false);
            bottomToolbar.getSkipButton().setEnabled(false);
            bottomToolbar.getDebugFrameButton().setEnabled(false);
            debugFrame.setVisible(false);
            canvas.getLayer2D().setDefaultPixelSize();
            canvas.getLayer2DMoveable().setDefaultPixelSize();

        });
        bottomToolbar.getDebugFrameButton().addActionListener(e -> {
            if (debugFrame.isVisible()) {
                debugFrame.setVisible(false);
            } else
                debugFrame.setVisible(true);
        });
        //TODO:add class cast exception
        bottomToolbar.getNextStepButton().addActionListener(e -> {
            Debug debug = (Debug) mode.getMode();
            debug.nextStep();
        });
        bottomToolbar.getSkipButton().addActionListener(e -> {
            Debug debug = (Debug) mode.getMode();
            debug.skip();
        });
    }
}
