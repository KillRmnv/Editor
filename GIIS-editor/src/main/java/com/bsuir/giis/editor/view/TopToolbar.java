package com.bsuir.giis.editor.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

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
        penButton = new JButton();
        penButton.setIcon(loadAnyIcon("pen-tool.png"));
        
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
        JMenu fileMenu = new JMenu();
        fileMenu.setIcon(loadIcon("File.svg"));
        fileMenu.setToolTipText("File");
        fileMenu.add(new JMenuItem("Open"));
        fileMenu.add(new JMenuItem("Save"));

        JMenu helpMenu = new JMenu();
        helpMenu.setIcon(loadIcon("Help.svg"));
        helpMenu.setToolTipText("Help");
        JMenuItem controlsItem = new JMenuItem("Controls");
        controlsItem.addActionListener(e -> showControlsDialog());
        helpMenu.add(controlsItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JMenuBar helpBar = new JMenuBar();
        helpBar.add(helpMenu);
        rightPanel.add(helpBar);

        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.add(menuBar, BorderLayout.WEST);
        menuPanel.add(rightPanel, BorderLayout.EAST);

        upperPanel.add(menuPanel, BorderLayout.NORTH);

        return fileMenu;
    }

    private JMenu getCurvesJMenu() {
        JMenu curveMenu = new JMenu();
        curveMenu.setIcon(loadIcon("curveLines.svg"));
        curveMenu.setToolTipText("Curves");
        curveMenu.add(createItem("Ellipse", "Ellipse.svg"));
        curveMenu.add(createItem("Circle", "curveLines.svg"));
        curveMenu.add(createItem("Parabola", "Parabola.svg"));
        curveMenu.add(createItem("Hyperbola", "Hiperbola.svg"));
        return curveMenu;
    }

    private JMenu getLineJMenu() {
        JMenu lineMenu = new JMenu();
        lineMenu.setIcon(loadIcon("lines.svg"));
        lineMenu.setToolTipText("Line");
        lineMenu.add(createItem("Antialias", "lines.svg"));
        lineMenu.add(createItem("CDA", "lines.svg"));
        lineMenu.add(createItem("Bresenham", "lines.svg"));
        return lineMenu;
    }

    private JMenu getParameterCurvesJMenu() {
        JMenu parameterCurvesMenu = new JMenu();
        parameterCurvesMenu.setIcon(loadAnyIcon("parametricCurve.png"));
        parameterCurvesMenu.setToolTipText("PCurves");
        parameterCurvesMenu.add(createItem("Hermit", "ParametricCurves.svg"));
        parameterCurvesMenu.add(createItem("Bezier", "ParametricCurves.svg"));
        parameterCurvesMenu.add(createItem("B Spline", "ParametricCurves.svg"));
        return parameterCurvesMenu;
    }

    private JMenuItem createItem(String text, String iconFile) {
        JMenuItem item = new JMenuItem(text);
        item.setIcon(loadAnyIcon(iconFile));
        return item;
    }

    private Icon loadAnyIcon(String fileName) {
        if (fileName.endsWith(".png")) {
            return loadPngIcon(fileName);
        }
        return loadIcon(fileName);
    }

    private FlatSVGIcon loadIcon(String fileName) {
        try {
            java.net.URL url = getClass().getResource("/" + fileName);
            if (url != null) {
                return new FlatSVGIcon(url);
            }
        } catch (Exception e) {
            // fallback
        }
        return null;
    }

    private Icon loadPngIcon(String fileName) {
        URL url = getClass().getResource("/" + fileName);
        if (url != null) {
            return new ImageIcon(url);
        }
        return null;
    }

    private void showControlsDialog() {
        String text = """
                === Controls ===

                Drawing:
                  Pen — freehand drawing (default)
                  Line — straight line algorithms (Antialias, CDA, Bresenham)
                  Curves — ellipse, circle, parabola, hyperbola
                  PCurves — parametric curves (Hermite, Bezier, B-Spline)

                Modes:
                  Regular — normal drawing mode
                  Debug — step-by-step algorithm visualization
                  Morph — drag control points to modify shapes

                3D Transform:
                  Left mouse drag — rotate model
                  Right mouse drag — translate model (X/Y)
                  Ctrl + Mouse Wheel — zoom in/out

                Reflect:
                  H — toggle horizontal reflection
                  V — toggle vertical reflection

                File:
                  Open — open file
                  Save — save file
                  Load 3D Model — load OBJ model

                Modifiers:
                  Ctrl — zoom (3D mode)
                  Shift — (available)
                  Alt  — (available)
                """;

        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        textArea.setBackground(UIManager.getColor("Panel.background"));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 450));

        JOptionPane.showMessageDialog(
                upperPanel,
                scrollPane,
                "Controls",
                JOptionPane.INFORMATION_MESSAGE
        );
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
