package com.bsuir.giis.editor.view;

import javax.swing.*;
import java.awt.*;
//TODO:change buttons to menus
public class TopToolbar {
    private JPanel upperPanel;
    private JButton lineButton;
    private JButton penButton;
    private JMenuBar fileMenuBar;

    public TopToolbar() {

        upperPanel = new JPanel(new BorderLayout());
        JPanel icons = new JPanel(new GridLayout());
        JButton lineButton = new JButton("Line");
        JButton pen = new JButton("Pen");
        this.lineButton = lineButton;
        this.penButton = pen;
        upperPanel.add(icons, BorderLayout.WEST);
        icons.add(lineButton);
        icons.add(pen);
        JMenuBar menuBar = new JMenuBar();
        upperPanel.add(menuBar, BorderLayout.NORTH);
        JMenu fileMenu = new JMenu("File");
        this.fileMenuBar = menuBar;
        menuBar.add(fileMenu);

    }

    public JPanel getUpperPanel() {
        return upperPanel;
    }

    public JButton getLineButton() {
        return lineButton;
    }

    public JButton getPenButton() {
        return penButton;
    }

    public JMenuBar getFileMenuBar() {
        return fileMenuBar;
    }


}
