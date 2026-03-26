package com.bsuir.giis.editor.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LayerPanel extends JPanel {

    private static final int BUTTON_SIZE = 28;
    private final JPanel layerListPanel;
    private final JButton addLayerButton;
    private final List<JToggleButton> layerButtons = new ArrayList<>();
    private int activeIndex = 0;

    public LayerPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(BUTTON_SIZE + 8, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        layerListPanel = new JPanel();
        layerListPanel.setLayout(new BoxLayout(layerListPanel, BoxLayout.Y_AXIS));

        addLayerButton = new JButton("+");
        addLayerButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        addLayerButton.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        addLayerButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        addLayerButton.setFocusPainted(false);
        addLayerButton.setMargin(new Insets(0, 0, 0, 0));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        bottomPanel.add(addLayerButton);

        JScrollPane scrollPane = new JScrollPane(layerListPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        addLayerButton(1);
    }

    public void addLayerButton(int layerNumber) {
        JToggleButton button = new JToggleButton(String.valueOf(layerNumber));
        button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (layerNumber - 1 == activeIndex) {
            button.setSelected(true);
        }

        layerButtons.add(button);

        layerListPanel.add(Box.createVerticalStrut(2));
        layerListPanel.add(button);

        renumberButtons();
        revalidate();
        repaint();
    }

    public void removeLayerButton(int index) {
        if (index >= 0 && index < layerButtons.size()) {
            JToggleButton removed = layerButtons.remove(index);
            layerListPanel.remove(removed);
            renumberButtons();
            revalidate();
            repaint();
        }
    }

    private void renumberButtons() {
        for (int i = 0; i < layerButtons.size(); i++) {
            layerButtons.get(i).setText(String.valueOf(i + 1));
        }
    }

    public void setActiveIndex(int index) {
        if (index >= 0 && index < layerButtons.size()) {
            activeIndex = index;
            for (int i = 0; i < layerButtons.size(); i++) {
                layerButtons.get(i).setSelected(i == index);
            }
        }
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public List<JToggleButton> getLayerButtons() {
        return layerButtons;
    }

    public JButton getAddLayerButton() {
        return addLayerButton;
    }
}
