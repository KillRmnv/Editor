package com.bsuir.giis.editor.view;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    private JTextField translateX, translateY;
    private JTextField scaleX, scaleY;
    private JTextField angleField;
    private JButton translateApply, scaleApply;
    private JButton rotateApply, rotateAroundPoint;
    private JButton transform3DButton;

    public BottomToolbar() {
        coordinates = new JLabel("x:0 y:0");

        bottom = new JPanel(new BorderLayout());
        
        morphButton = new JButton("Morph");
        regularModeButton = new JButton("Reg");
        transformButton = new JButton("Transform");

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

        JMenuItem debugModeItem = new JMenuItem("Debug Mode");
        debugModeItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                itemClicked = true;
            }
        });
        debugPopup.add(debugModeItem);
        debugPopup.add(new JSeparator());

        JMenuItem nextStepItem = new JMenuItem("Next Step");
        nextStepItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                itemClicked = true;
            }
        });
        debugPopup.add(nextStepItem);

        JMenuItem skipItem = new JMenuItem("Skip");
        skipItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                itemClicked = true;
            }
        });
        debugPopup.add(skipItem);

        JMenuItem showDlogItem = new JMenuItem("Show Dlog");
        showDlogItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                itemClicked = true;
            }
        });
        debugPopup.add(showDlogItem);

        debugButton = new JButton("Debug");
        debugButton.addActionListener(e -> {
            if (debugPopup.isVisible()) {
                debugPopup.setVisible(false);
            } else {
                debugPopup.show(debugButton, 0, -debugPopup.getPreferredSize().height);
            }
        });
    }

    private void setupTransformPopup() {
        transformPopup = new JPopupMenu();

        JPanel translatePanel = new JPanel(new BorderLayout(5, 5));
        translatePanel.add(new JLabel("Translation:"), BorderLayout.NORTH);
        JPanel translateFields = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        translateX = new JTextField(5);
        translateY = new JTextField(5);
        translateFields.add(new JLabel("X:"));
        translateFields.add(translateX);
        translateFields.add(new JLabel("Y:"));
        translateFields.add(translateY);
        translatePanel.add(translateFields, BorderLayout.CENTER);
        translateApply = new JButton("Apply");
        translatePanel.add(translateApply, BorderLayout.SOUTH);

        JPanel scalePanel = new JPanel(new BorderLayout(5, 5));
        scalePanel.add(new JLabel("Scaling:"), BorderLayout.NORTH);
        JPanel scaleFields = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        scaleX = new JTextField(5);
        scaleY = new JTextField(5);
        scaleFields.add(new JLabel("X:"));
        scaleFields.add(scaleX);
        scaleFields.add(new JLabel("Y:"));
        scaleFields.add(scaleY);
        scalePanel.add(scaleFields, BorderLayout.CENTER);
        scaleApply = new JButton("Apply");
        scalePanel.add(scaleApply, BorderLayout.SOUTH);

        JPanel rotatePanel = new JPanel(new BorderLayout(5, 5));
        rotatePanel.add(new JLabel("Rotation:"), BorderLayout.NORTH);
        JPanel rotateFields = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        angleField = new JTextField(8);
        rotateFields.add(new JLabel("°:"));
        rotateFields.add(angleField);
        rotatePanel.add(rotateFields, BorderLayout.CENTER);
        JPanel rotateButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        rotateApply = new JButton("Apply");
        rotateAroundPoint = new JButton("Around Point");
        rotateButtons.add(rotateApply);
        rotateButtons.add(rotateAroundPoint);
        rotatePanel.add(rotateButtons, BorderLayout.SOUTH);

        JPanel transform3DPanel = new JPanel(new BorderLayout(5, 5));
        transform3DPanel.add(new JLabel("3D Transform:"), BorderLayout.NORTH);
        transform3DButton = new JButton("Enable 3D Mode");
        transform3DPanel.add(transform3DButton, BorderLayout.CENTER);

        transformPopup.add(translatePanel);
        transformPopup.add(scalePanel);
        transformPopup.add(rotatePanel);
        transformPopup.add(new JSeparator());
        transformPopup.add(transform3DPanel);

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

    public JTextField getTranslateX() {
        return translateX;
    }

    public JTextField getTranslateY() {
        return translateY;
    }

    public JTextField getScaleX() {
        return scaleX;
    }

    public JTextField getScaleY() {
        return scaleY;
    }

    public JTextField getAngleField() {
        return angleField;
    }

    public JButton getTranslateApply() {
        return translateApply;
    }

    public JButton getScaleApply() {
        return scaleApply;
    }

    public JButton getRotateApply() {
        return rotateApply;
    }

    public JButton getRotateAroundPoint() {
        return rotateAroundPoint;
    }

    public JButton getTransform3DButton() {
        return transform3DButton;
    }
}
