package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.Tool;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ModifierState;
import com.bsuir.giis.editor.utils.ToolContainer;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class ColorPickerHandler implements DrawableHandler {

    private Color pickedColor = Color.BLACK;
    private Consumer<Color> onColorPicked;

    public ColorPickerHandler() {
    }

    public void setOnColorPicked(Consumer<Color> onColorPicked) {
        this.onColorPicked = onColorPicked;
    }

    public Color getPickedColor() {
        return pickedColor;
    }

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
            Color color = canvas.getLayer().getRenderer().getPixelColor(
                canvas.getLayer(), mouseEvent.getX(), mouseEvent.getY()
            );
            pickedColor = color;
            if (onColorPicked != null) {
                onColorPicked.accept(color);
            }
        }
    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
    }

    @Override
    public void handleRelease(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
    }

    public static class ColorPickerTool implements Tool {
    }
}
