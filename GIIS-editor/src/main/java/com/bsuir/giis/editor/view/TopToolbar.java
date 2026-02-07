package com.bsuir.giis.editor.view;

import javax.swing.*;
import java.awt.*;

public class TopToolbar {
    JPanel upperPanel;

    public TopToolbar() {

        upperPanel = new JPanel(new BorderLayout());
        JPanel icons = new JPanel(new GridLayout());
        JButton lineButton = new JButton("Line");
        JButton pen = new JButton("Pen");

        upperPanel.add(icons, BorderLayout.WEST);
        icons.add(lineButton);
        icons.add(pen);
        JMenuBar menuBar = new JMenuBar();
        upperPanel.add(menuBar, BorderLayout.NORTH);
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

    }

    public JPanel getUpperPanel() {
        return upperPanel;
    }

}
