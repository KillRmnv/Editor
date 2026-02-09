package com.bsuir.giis.editor.view;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;

public class BottomToolbar {
    private JPanel bottom;
    private JButton regularModeButton;
    private JButton debugModeButton;
    private JButton debugFrameButton;
    private JButton nextStepButton;
    private JButton skipButton;
    private JFormattedTextField field;
    private JLabel coordinates;


    public BottomToolbar() {
        coordinates = new JLabel("x:0 y:0");

        bottom = new JPanel(new BorderLayout());
        regularModeButton = new JButton("Regular");
        debugModeButton = new JButton("Debug");
        debugFrameButton = new JButton("Dlog");
        nextStepButton = new JButton("Next");
        skipButton = new JButton("Skip");


        debugFrameButton.setEnabled(false);
        skipButton.setEnabled(false);
        nextStepButton.setEnabled(false);

        NumberFormatter formatter = new NumberFormatter(
                NumberFormat.getIntegerInstance()
        );

        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(true);
        JLabel iterationsLabel = new JLabel("Pixels:");
         field =
                new JFormattedTextField(formatter);
        field.setColumns(2);
        field.setValue(8);


        JPanel bottomWestFlow = new JPanel(new FlowLayout());
        bottomWestFlow.add(regularModeButton);
        bottomWestFlow.add(debugModeButton);
        bottomWestFlow.add(nextStepButton);
        bottomWestFlow.add(skipButton);
        bottomWestFlow.add(debugFrameButton);


        JPanel bottomEastFlow = new JPanel(new FlowLayout());
        bottomEastFlow.add(iterationsLabel);
        bottomEastFlow.add(field);
        bottomEastFlow.add(coordinates);

        bottom.add(bottomWestFlow, BorderLayout.WEST);
        bottom.add(bottomEastFlow, BorderLayout.EAST);

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
    public JFormattedTextField getField() {
        return field;
    }
    public JLabel getCoordinates() {
        return coordinates;
    }
}
