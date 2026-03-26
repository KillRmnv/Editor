package com.bsuir.giis.editor.service.readers;

import com.bsuir.giis.editor.model.CanvasState;
import com.bsuir.giis.editor.model.Model3DInstance;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.shapes.Drawable;
import com.bsuir.giis.editor.model.shapes.MorphableShape;
import com.bsuir.giis.editor.model.shapes.Shape;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.rendering.TwoDimensionLayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SceneSerializer {

    private static final ObjectWriter WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();

    public static void save(Canvas canvas, File giisFile) throws IOException {
        long startTotal = System.currentTimeMillis();

        List<LayerDto> layers = new ArrayList<>();
        int layerCount = canvas.getUserLayerCount();
        System.out.println("[Save] Starting save to " + giisFile.getName() + ", layers: " + layerCount);

        for (int i = 0; i < layerCount; i++) {
            long startLayer = System.currentTimeMillis();
            TwoDimensionLayer layer = canvas.getUserLayer(i);
            CanvasState state = layer.getState();

            layers.add(buildLayerDto(i + 1, state));

            long layerTime = System.currentTimeMillis() - startLayer;
            System.out.println("[Save] Layer " + (i + 1) + " done (" + layerTime + "ms)");
        }

        WRITER.writeValue(giisFile, new SceneDto(layers));

        long totalTime = System.currentTimeMillis() - startTotal;
        System.out.println("[Save] Complete (" + totalTime + "ms)");
    }

    private static LayerDto buildLayerDto(int id, CanvasState state) {
        List<ShapeDto> shapes = new ArrayList<>();
        Set<MorphableShape<?>> processed = new HashSet<>();

        for (var entry : state.getLayersMap().entrySet()) {
            for (MorphableShape<?> shape : entry.getValue()) {
                if (processed.add(shape)) {
                    shapes.add(toShapeDto(shape));
                }
            }
        }

        for (Shape<?> shape : state.getAllShapes()) {
            if (shape instanceof MorphableShape<?> ms) {
                if (processed.add(ms)) {
                    shapes.add(toShapeDto(ms));
                }
            } else {
                Drawable drawable = shape.getDrawable();
                String typeName = DrawableTypeMapper.getTypeName(drawable);
                if (DrawableTypeMapper.isSupported(typeName)) {
                    shapes.add(toShapeDto(shape));
                } else {
                    System.out.println("[Save] Skipping unsupported drawable: " + typeName);
                }
            }
        }

        List<ModelDto> models = new ArrayList<>();
        for (Model3DInstance inst : state.getModels()) {
            models.add(new ModelDto(
                inst.getObjFilePath(),
                new RotationDto(
                    inst.getRotation().getAngleX(),
                    inst.getRotation().getAngleY(),
                    inst.getRotation().getAngleZ()
                ),
                inst.getTranslateX(),
                inst.getTranslateY(),
                inst.getScaleFactor(),
                inst.isReflectX(),
                inst.isReflectY(),
                inst.isPerspectiveEnabled()
            ));
        }

        return new LayerDto(id, shapes, models);
    }

    private static ShapeDto toShapeDto(Shape<?> shape) {
        return new ShapeDto(
            DrawableTypeMapper.getTypeName(shape.getDrawable()),
            shape.getParameters().getPoints()
        );
    }
}
