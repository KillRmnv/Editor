package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.controllers.handlers.ColorPickerHandler;
import com.bsuir.giis.editor.controllers.handlers.FillHandler;
import com.bsuir.giis.editor.model.FillTool;
import com.bsuir.giis.editor.service.fill.ScanlineAELFill;
import com.bsuir.giis.editor.service.fill.ScanlineOELFill;
import com.bsuir.giis.editor.service.fill.ScanlineSeedFill;
import com.bsuir.giis.editor.service.fill.SimpleSeedFill;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.RightToolbar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class RightPanelController {

    private final RightToolbar rightToolbar;
    private final ToolContainer tool;
    private final FillHandler fillHandler;
    private final ColorPickerHandler colorPickerHandler;

    private Color currentFillColor = Color.BLACK;
    private Color currentBorderColor = Color.BLACK;
    private int currentAlgorithmIndex = 0;

    public RightPanelController(RightToolbar rightToolbar, ToolContainer tool) {
        this.rightToolbar = rightToolbar;
        this.tool = tool;
        this.fillHandler = new FillHandler();
        this.colorPickerHandler = new ColorPickerHandler();

        setupBorderColorClick();
        setupPaletteButton();
        setupAlgorithmSelector();
        selectDefaultFillTool();
    }

    private void selectDefaultFillTool() {
        FillTool defaultTool = new SimpleSeedFill();
        tool.setTool(defaultTool);
        tool.setHandler(fillHandler);
        fillHandler.setFillColor(currentFillColor);
        updateBorderColorVisibility();
    }

    private void setupPaletteButton() {
        rightToolbar.getPaletteButton().addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(
                rightToolbar.getRightPanel(),
                "Choose Fill Color",
                currentFillColor
            );
            if (chosen != null) {
                currentFillColor = chosen;
                rightToolbar.setFillColorPreview(chosen);
                fillHandler.setFillColor(chosen);
            }
        });
    }

    private void setupAlgorithmSelector() {
        JPopupMenu fillPopup = rightToolbar.getFillPopup();
        for (int i = 0; i < fillPopup.getComponentCount(); i++) {
            if (fillPopup.getComponent(i) instanceof JMenuItem item) {
                int index = i;
                item.addActionListener(e -> {
                    currentAlgorithmIndex = index;
                    FillTool selectedTool = getSelectedFillTool(index);
                    tool.setTool(selectedTool);
                    tool.setHandler(fillHandler);
                    updateBorderColorVisibility();
                });
            }
        }
    }

    private FillTool getSelectedFillTool(int index) {
        return switch (index) {
            case 0 -> new SimpleSeedFill();
            case 1 -> new ScanlineSeedFill();
            case 2 -> new ScanlineAELFill();
            case 3 -> new ScanlineOELFill();
            default -> new SimpleSeedFill();
        };
    }

    private void updateBorderColorVisibility() {
        boolean isSeedFill = currentAlgorithmIndex == 0 || currentAlgorithmIndex == 1;
        rightToolbar.setBorderColorVisible(isSeedFill);
    }

    private void setupBorderColorClick() {
        rightToolbar.getBorderColorPreview().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color chosen = JColorChooser.showDialog(
                    rightToolbar.getRightPanel(),
                    "Choose Border Color",
                    currentBorderColor
                );
                if (chosen != null) {
                    currentBorderColor = chosen;
                    rightToolbar.setBorderColorPreview(chosen);
                    fillHandler.setBorderColor(chosen);
                }
            }
        });
    }

    public ColorPickerHandler getColorPickerHandler() {
        return colorPickerHandler;
    }

    public FillHandler getFillHandler() {
        return fillHandler;
    }

    public Color getCurrentFillColor() {
        return currentFillColor;
    }

    public Color getCurrentBorderColor() {
        return currentBorderColor;
    }
}
