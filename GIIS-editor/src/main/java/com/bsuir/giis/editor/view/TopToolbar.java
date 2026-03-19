package com.bsuir.giis.editor.view;

import javax.swing.*;
import java.awt.*;

public final class TopToolbar {
    private final JPanel upperPanel;
    private final JMenu lineMenu;
    private final JButton penButton;
    private final JMenu fileMenu;
    private final JMenu curveMenu;
    private final JMenu parameterCurvesMenu;

    public TopToolbar() {

        upperPanel = new JPanel(new BorderLayout());
        JPanel icons = new JPanel(new BorderLayout());

        lineMenu = getLineJMenu();
        curveMenu = getCurvesJMenu();
        parameterCurvesMenu = getParameterCurvesJMenu();
        penButton = new JButton("Pen");
        
        JMenuBar toolsMenuBar = new JMenuBar();
        toolsMenuBar.add(lineMenu);
        toolsMenuBar.add(curveMenu);
        toolsMenuBar.add(parameterCurvesMenu);

        icons.add(penButton, BorderLayout.WEST);
        icons.add(toolsMenuBar, BorderLayout.CENTER);
        
        upperPanel.add(icons, BorderLayout.WEST);

        fileMenu = setupTopJMenuBar();
    }

    private JMenu setupTopJMenuBar() {
        final JMenu fileMenu;
        fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("Open"));
        fileMenu.add(new JMenuItem("Save"));
        JMenuBar menuBar = new JMenuBar();
        upperPanel.add(menuBar, BorderLayout.NORTH);
        menuBar.add(fileMenu);
        return fileMenu;
    }

    private JMenu getCurvesJMenu() {
        final JMenu curveMenu;
        curveMenu = new JMenu("Curves");
        curveMenu.add(new JMenuItem("Ellipse"));
        curveMenu.add(new JMenuItem("Circle"));
        curveMenu.add(new JMenuItem("Parabola"));
        curveMenu.add(new JMenuItem("Hyperbola"));
        return curveMenu;
    }

    private JMenu getLineJMenu() {
        final JMenu lineMenu;
        lineMenu = new JMenu("Line");
        lineMenu.add(new JMenuItem("Antialias"));
        lineMenu.add(new JMenuItem("CDA"));
        lineMenu.add(new JMenuItem("Bresenham"));
        return lineMenu;
    }

    private JMenu getParameterCurvesJMenu() {
        final JMenu parameterCurvesMenu;
        parameterCurvesMenu = new JMenu("PCurves");
        parameterCurvesMenu.add(new JMenuItem("Hermit"));
        parameterCurvesMenu.add(new JMenuItem("Bezier"));
        parameterCurvesMenu.add(new JMenuItem("B Spline"));
        return parameterCurvesMenu;
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

    public JMenu getCurveMenu() {
        return curveMenu;
    }

    public JMenu getParameterCurvesMenu() {
        return parameterCurvesMenu;
    }

}
