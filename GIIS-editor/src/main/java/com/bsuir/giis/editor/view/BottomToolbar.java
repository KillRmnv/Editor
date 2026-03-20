package com.bsuir.giis.editor.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.text.NumberFormat;

public final class BottomToolbar {

    private final JPanel bottom;
    private final JButton morphButton;
    private final JButton regularModeButton;
    private JButton debugButton;
    private JButton transformButton;
    private JPopupMenu debugPopup;
    private JPopupMenu transformPopup;
    private final JFormattedTextField field;
    private final JLabel coordinates;

    private volatile boolean itemClicked = false;

    private JButton transform3DButton;
    private JButton reflectHButton;
    private JButton reflectVButton;
    private JCheckBox perspectiveCheckBox;

    public BottomToolbar() {
        coordinates = new JLabel("x:0 y:0");

        bottom = new JPanel(new BorderLayout());

        morphButton = createIconButton("Morph.svg");
        regularModeButton = createIconButton("Regular.svg");
        debugButton = createIconButton("Debug.svg");
        transformButton = createIconButton("3DTransform.svg");

        setupDebugPopup();
        setupTransformPopup();

        NumberFormatter formatter = new NumberFormatter(NumberFormat.getIntegerInstance());
        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(true);
        
        JLabel iterationsLabel = new JLabel("Px:");
        field = new JFormattedTextField(formatter);
        field.setColumns(2);
        field.setValue(8);

        JPanel bottomWestFlow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        bottomWestFlow.add(morphButton);
        bottomWestFlow.add(regularModeButton);
        bottomWestFlow.add(debugButton);
        bottomWestFlow.add(transformButton);

        JPanel bottomEastFlow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        bottomEastFlow.add(iterationsLabel);
        bottomEastFlow.add(field);
        bottomEastFlow.add(coordinates);

        bottom.add(bottomWestFlow, BorderLayout.WEST);
        bottom.add(bottomEastFlow, BorderLayout.EAST);
    }

    private JButton createIconButton(String iconFile) {
        JButton button = new JButton();
        button.setIcon(loadAnyIcon(iconFile));
        return button;
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

    private void setupDebugPopup() {
        debugPopup = new JPopupMenu();
        debugPopup.setLightWeightPopupEnabled(false);

        PopupMenuListener listener = new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                itemClicked = false;
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (itemClicked) {
                    SwingUtilities.invokeLater(() -> {
                        Component invoker = debugPopup.getInvoker();
                        if (invoker != null) {
                            debugPopup.show(invoker, 0, -debugPopup.getPreferredSize().height);
                        }
                    });
                }
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                itemClicked = false;
            }
        };
        debugPopup.addPopupMenuListener(listener);

        JMenuItem debugModeItem = createPopupItem("Debug Mode", "Debug.svg");
        debugPopup.add(debugModeItem);
        debugPopup.add(new JSeparator());

        JMenuItem nextStepItem = createPopupItem("Next Step", "next.png");
        debugPopup.add(nextStepItem);

        JMenuItem skipItem = createPopupItem("Skip", "fast-forward.png");
        debugPopup.add(skipItem);

        JMenuItem showDlogItem = createPopupItem("Show Dlog", "DLog.svg");
        debugPopup.add(showDlogItem);

        debugButton.addActionListener(e -> {
            if (debugPopup.isVisible()) {
                debugPopup.setVisible(false);
            } else {
                debugPopup.show(debugButton, 0, -debugPopup.getPreferredSize().height);
            }
        });
    }

    private JMenuItem createPopupItem(String text, String iconFile) {
        JMenuItem item = new JMenuItem(text);
        if (iconFile != null) {
            item.setIcon(loadAnyIcon(iconFile));
        }
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                itemClicked = true;
            }
        });
        return item;
    }

    private void setupTransformPopup() {
        transformPopup = new JPopupMenu();

        JPanel transform3DPanel = new JPanel(new BorderLayout(5, 5));
        transform3DPanel.add(new JLabel("3D Transform:"), BorderLayout.NORTH);
        transform3DButton = new JButton();
        transform3DButton.setIcon(loadAnyIcon("3DTransform.svg"));
        transform3DPanel.add(transform3DButton, BorderLayout.CENTER);

        JPanel reflectPanel = new JPanel(new BorderLayout(5, 5));
        reflectPanel.add(new JLabel("Reflect:"), BorderLayout.NORTH);
        JPanel reflectButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        reflectHButton = createIconButton("HorizontalReflection.svg");
        reflectVButton = createIconButton("verticalReflection.svg");
        reflectButtons.add(reflectHButton);
        reflectButtons.add(reflectVButton);
        reflectPanel.add(reflectButtons, BorderLayout.CENTER);

        perspectiveCheckBox = new JCheckBox("Perspective", true);

        transformPopup.add(transform3DPanel);
        transformPopup.add(new JSeparator());
        transformPopup.add(reflectPanel);
        transformPopup.add(new JSeparator());
        transformPopup.add(perspectiveCheckBox);

        transformButton.addActionListener(e -> {
            Component button = (Component) e.getSource();
            Dimension popupSize = transformPopup.getPreferredSize();
            int x = 0;
            int y = -popupSize.height;
            transformPopup.show(button, x, y);
        });
    }

    public JButton getMorphButton() {
        return morphButton;
    }

    public JButton getRegularModeButton() {
        return regularModeButton;
    }

    public JButton getDebugButton() {
        return debugButton;
    }

    public JButton getTransformButton() {
        return transformButton;
    }

    public JPopupMenu getDebugPopup() {
        return debugPopup;
    }

    public JPopupMenu getTransformPopup() {
        return transformPopup;
    }

    public JPanel getBottom() {
        return bottom;
    }

    public JFormattedTextField getField() {
        return field;
    }

    public JLabel getCoordinates() {
        return coordinates;
    }

    public JButton getTransform3DButton() {
        return transform3DButton;
    }

    public JButton getReflectHButton() {
        return reflectHButton;
    }

    public JButton getReflectVButton() {
        return reflectVButton;
    }

    public JCheckBox getPerspectiveCheckBox() {
        return perspectiveCheckBox;
    }
}
