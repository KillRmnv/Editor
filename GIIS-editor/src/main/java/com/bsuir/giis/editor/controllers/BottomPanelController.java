package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.controllers.handlers.MorphHandler;
import com.bsuir.giis.editor.service.flow.Debug;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.MorphStep;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.BottomToolbar;
import com.bsuir.giis.editor.view.Canvas;
import javax.swing.*;

public final class BottomPanelController {

    public BottomPanelController(
        BottomToolbar bottomToolbar,
        ModeContainer mode,
        JFrame debugFrame,
        Canvas canvas,
        ToolContainer tool
    ) {
        bottomToolbar
            .getDebugModeButton()
            .addActionListener(e -> {
                if(!tool.getTool().getClass().equals(MorphHandler.class)) {
                    canvas.getLayer2D().setPixelSizeFromField();
                    canvas.getLayer2DMoveable().setPixelSizeFromField();
                }
                mode.setMode(new Debug());
                bottomToolbar.getDebugModeButton().setSelected(true);
                bottomToolbar.getRegularModeButton().setSelected(false);
                bottomToolbar.getNextStepButton().setEnabled(true);
                bottomToolbar.getSkipButton().setEnabled(true);
                bottomToolbar.getDebugFrameButton().setEnabled(true);

            });
        bottomToolbar
            .getRegularModeButton()
            .addActionListener(e -> {
                mode.setMode(new Regular());
                bottomToolbar.getRegularModeButton().setSelected(true);
                bottomToolbar.getDebugModeButton().setSelected(false);
                bottomToolbar.getNextStepButton().setEnabled(false);
                bottomToolbar.getSkipButton().setEnabled(false);
                bottomToolbar.getDebugFrameButton().setEnabled(false);
                debugFrame.setVisible(false);
                if(!tool.getTool().getClass().equals(MorphHandler.class)) {
                    canvas.getLayer2D().setDefaultPixelSize();
                    canvas.getLayer2DMoveable().setDefaultPixelSize();
                }
            });
        bottomToolbar
            .getDebugFrameButton()
            .addActionListener(e -> {
                if (debugFrame.isVisible()) {
                    debugFrame.setVisible(false);
                } else debugFrame.setVisible(true);
            });
        //TODO:add class cast exception
        bottomToolbar
            .getNextStepButton()
            .addActionListener(e -> {
                Debug debug = (Debug) mode.getMode();
                debug.nextStep();
            });
        bottomToolbar
            .getSkipButton()
            .addActionListener(e -> {
                Debug debug = (Debug) mode.getMode();
                debug.skip();
            });
        bottomToolbar
            .getMorphButton()
            .addActionListener(e -> {
                canvas.getLayer2DMorphable().setDefaultPixelSize();
                tool.setHandler(
                    new MorphHandler(
                        canvas.getLayer2DMorphable(),
                        new MorphStep()
                    )
                );
            });
    }
}
