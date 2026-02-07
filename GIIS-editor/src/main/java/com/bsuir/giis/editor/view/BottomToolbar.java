package com.bsuir.giis.editor.view;

import javax.swing.*;
import java.awt.*;

public class BottomToolbar {
    JPanel bottom;
    public  BottomToolbar(JLabel coordinates) {
         bottom = new JPanel(new BorderLayout());
        JButton regularModeButton = new JButton("Regular");
        JButton debugModeButton = new JButton("Debug");
        JButton nextStepButton = new JButton("Next");
        nextStepButton.setVisible(false);
        JPanel bottomFlow=new JPanel(new FlowLayout());
        bottomFlow.add(regularModeButton);
        bottomFlow.add(debugModeButton);
        bottomFlow.add(nextStepButton);

        bottom.add(bottomFlow, BorderLayout.WEST);
        bottom.add(coordinates, BorderLayout.EAST);

    }
    public JPanel getBottom() {
        return bottom;
    }
}
