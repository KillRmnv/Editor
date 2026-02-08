package com.bsuir.giis.editor.view;

import javax.swing.*;
import java.awt.*;

public class BottomToolbar {
    private JPanel bottom;
    private JButton regularModeButton;
    private JButton debugModeButton;
    private JButton debugFrameButton;
    private JButton nextStepButton;
    private JButton skipButton;

    public BottomToolbar(JLabel coordinates) {
        bottom = new JPanel(new BorderLayout());
        regularModeButton = new JButton("Regular");
        debugModeButton = new JButton("Debug");
        debugFrameButton = new JButton("Dlog");
        nextStepButton = new JButton("Next");
        skipButton = new JButton("Skip");




        debugFrameButton.setEnabled(false);
        skipButton.setEnabled(false);
        nextStepButton.setEnabled(false);

        JPanel bottomFlow = new JPanel(new FlowLayout());
        bottomFlow.add(regularModeButton);
        bottomFlow.add(debugModeButton);
        bottomFlow.add(nextStepButton);
        bottomFlow.add(skipButton);
        bottomFlow.add(debugFrameButton);

        bottom.add(bottomFlow, BorderLayout.WEST);
        bottom.add(coordinates, BorderLayout.EAST);

    }

    public JButton getRegularModeButton() {
        return regularModeButton;
    }

    public JButton getDebugModeButton() {
        return debugModeButton;
    }

    public JButton getNextStepButton() {
        return nextStepButton;
    }

    public JButton getDebugFrameButton() {
        return debugFrameButton;
    }

    public JPanel getBottom() {
        return bottom;
    }

    public JButton getSkipButton() {
        return skipButton;
    }
}
