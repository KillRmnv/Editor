package com.bsuir.giis.editor.view;

import javax.swing.*;
import java.awt.*;

//TODO:add layers
public final class TopToolbar {
    private final JPanel upperPanel;
    private final JMenu lineMenu;
    private final JButton penButton;
    private final JMenu fileMenu;

    public TopToolbar() {

        upperPanel = new JPanel(new BorderLayout());
        JPanel icons = new JPanel(new GridLayout());


        //don't touch order of menu items
        lineMenu = new JMenu("Line");
        lineMenu.add(new JMenuItem("Antialias"));
        lineMenu.add(new JMenuItem("CDA"));
        lineMenu.add(new JMenuItem("Bresenham"));

        JButton pen = new JButton("Pen");
        this.penButton = pen;
        upperPanel.add(icons, BorderLayout.WEST);
        icons.add(pen);
        icons.add(lineMenu);
        JMenuBar menuBar = new JMenuBar();
        upperPanel.add(menuBar, BorderLayout.NORTH);

        fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("Open"));
        fileMenu.add(new JMenuItem("Save"));

        menuBar.add(fileMenu);

    }

    public JPanel getUpperPanel() {
        return upperPanel;
    }

    public JMenu getLineMenu() {
        return lineMenu;
    }

    public JButton getPenButton() {
        return penButton;
    }

    public JMenu getFileMenuBar() {
        return fileMenu;
    }


}
