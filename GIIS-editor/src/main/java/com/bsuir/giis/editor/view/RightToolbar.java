package com.bsuir.giis.editor.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public final class RightToolbar {

    private static final int BUTTON_SIZE = 28;

    private final JPanel rightPanel;
    private final JButton paletteButton;
    private final JPanel fillColorPreview;
    private final JPanel borderColorPreview;
    private final JButton fillButton;
    private final JPopupMenu fillPopup;
    private final JLabel borderColorLabel;

    public RightToolbar() {
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(BUTTON_SIZE + 16, 0));
        rightPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));

        paletteButton = createIconButton("palette.png");
        paletteButton.setToolTipText("Color Palette");

        fillColorPreview = createColorPreview(Color.BLACK);
        fillColorPreview.setToolTipText("Fill Color");

        borderColorPreview = createColorPreview(Color.BLACK);
        borderColorPreview.setToolTipText("Border Color");
        borderColorLabel = new JLabel("Border:");
        borderColorLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
        borderColorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        fillButton = createIconButton("dropper.png");
        fillButton.setToolTipText("Fill Tool");

        fillPopup = setupFillPopup();

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        fillButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        paletteButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonsPanel.add(Box.createVerticalStrut(4));
        buttonsPanel.add(paletteButton);
        buttonsPanel.add(Box.createVerticalStrut(8));
        buttonsPanel.add(fillButton);

        JPanel colorPreviews = new JPanel();
        colorPreviews.setLayout(new BoxLayout(colorPreviews, BoxLayout.Y_AXIS));
        colorPreviews.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel fillLabel = new JLabel("Fill:");
        fillLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
        fillLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        colorPreviews.add(fillLabel);
        colorPreviews.add(Box.createVerticalStrut(2));
        colorPreviews.add(fillColorPreview);
        colorPreviews.add(Box.createVerticalStrut(4));
        colorPreviews.add(borderColorLabel);
        colorPreviews.add(Box.createVerticalStrut(2));
        colorPreviews.add(borderColorPreview);
        colorPreviews.add(Box.createVerticalStrut(8));

        rightPanel.add(buttonsPanel);
        rightPanel.add(colorPreviews);
        rightPanel.add(Box.createVerticalGlue());
    }

    private JPopupMenu setupFillPopup() {
        JPopupMenu popup = new JPopupMenu();

        popup.add(createItem("Simple Seed", "fill.png"));
        popup.add(createItem("Scanline Seed", "fill.png"));
        popup.add(createItem("Scanline AEL", "fill.png"));
        popup.add(createItem("Scanline OEL", "fill.png"));

        fillButton.addActionListener(e -> {
            if (popup.isVisible()) {
                popup.setVisible(false);
            } else {
                popup.show(fillButton, 0, -popup.getPreferredSize().height);
            }
        });

        return popup;
    }

    private JMenuItem createItem(String text, String iconFile) {
        JMenuItem item = new JMenuItem(text);
        item.setName(text);
        item.setIcon(loadAnyIcon(iconFile));
        return item;
    }

    private JButton createIconButton(String iconFile) {
        JButton button = new JButton();
        button.setIcon(loadAnyIcon(iconFile));
        button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMinimumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
    }

    private JPanel createColorPreview(Color color) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.DARK_GRAY);
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };
        panel.setBackground(color);
        panel.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        panel.setMinimumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        panel.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return panel;
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

    public void setFillColorPreview(Color color) {
        fillColorPreview.setBackground(color);
        fillColorPreview.repaint();
    }

    public void setBorderColorPreview(Color color) {
        borderColorPreview.setBackground(color);
        borderColorPreview.repaint();
    }

    public void setBorderColorVisible(boolean visible) {
        borderColorLabel.setVisible(visible);
        borderColorPreview.setVisible(visible);
    }

    public JPanel getRightPanel() {
        return rightPanel;
    }

    public JButton getPaletteButton() {
        return paletteButton;
    }

    public JPanel getFillColorPreview() {
        return fillColorPreview;
    }

    public JPanel getBorderColorPreview() {
        return borderColorPreview;
    }

    public JButton getFillButton() {
        return fillButton;
    }

    public JPopupMenu getFillPopup() {
        return fillPopup;
    }
}
