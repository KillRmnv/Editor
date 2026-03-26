package com.bsuir.giis.editor.service.readers;

import com.bsuir.giis.editor.model.Model3DInstance;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.model.dimensions.Model3D;
import com.bsuir.giis.editor.model.shapes.Drawable;
import com.bsuir.giis.editor.model.shapes.MorphableShape;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.rendering.TwoDimensionLayer;
import com.bsuir.giis.editor.exceptions.ReaderException;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class SceneDeserializer {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void load(Canvas canvas, File giisFile, JFormattedTextField pixelField) throws IOException {
        long startTotal = System.currentTimeMillis();
        System.out.println("[Load] Starting load from " + giisFile.getName());

        SceneDto scene = MAPPER.readValue(giisFile, SceneDto.class);
        System.out.println("[Load] Parsed JSON, layers in file: " + scene.layers().size());

        while (canvas.getUserLayerCount() > 1) {
            canvas.removeUserLayer(0);
        }
        if (canvas.getUserLayerCount() > 0) {
            canvas.getUserLayer(0).cleanLayer();
        }

        File parentDir = giisFile.getParentFile();

        for (int i = 0; i < scene.layers().size(); i++) {
            long startLayer = System.currentTimeMillis();
            LayerDto layerDto = scene.layers().get(i);

            TwoDimensionLayer layer = (i == 0)
                ? canvas.getUserLayer(0)
                : canvas.addUserLayer();
            layer.cleanLayer();

            if (layerDto.shapes() != null) {
                System.out.println("[Load] Layer " + layerDto.id() + ": loading " + layerDto.shapes().size() + " shapes");
                for (ShapeDto shapeDto : layerDto.shapes()) {
                    loadShape(layer, shapeDto);
                }
            }

            if (layerDto.models() != null) {
                System.out.println("[Load] Layer " + layerDto.id() + ": loading " + layerDto.models().size() + " models");
                for (ModelDto modelDto : layerDto.models()) {
                    loadModel(layer, modelDto, parentDir);
                }
            }

            System.out.println("[Load] Layer " + layerDto.id() + ": rendering...");
            long startRender = System.currentTimeMillis();
            layer.renderAndRepaint();
            long renderTime = System.currentTimeMillis() - startRender;

            long layerTime = System.currentTimeMillis() - startLayer;
            System.out.println("[Load] Layer " + layerDto.id() + " done (render: " + renderTime
                    + "ms, total: " + layerTime + "ms)");
        }

        canvas.setActiveLayerIndex(0);

        long totalTime = System.currentTimeMillis() - startTotal;
        System.out.println("[Load] Complete (" + totalTime + "ms)");
    }

    private static void loadShape(TwoDimensionLayer layer, ShapeDto dto) {
        if (!DrawableTypeMapper.isSupported(dto.type())) {
            System.err.println("[Load] Unknown shape type: " + dto.type() + ", skipping");
            return;
        }

        Drawable drawable = DrawableTypeMapper.createDrawable(dto.type());
        PointShapeParameters params = new PointShapeParameters(dto.points());
        MorphableShape shape = new MorphableShape(params, drawable);

        for (Point point : dto.points()) {
            layer.addShape(point, shape);
        }
    }

    private static void loadModel(TwoDimensionLayer layer, ModelDto dto, File parentDir) {
        File objFile = new File(dto.objFile());
        if (!objFile.isAbsolute()) {
            objFile = new File(parentDir, dto.objFile());
        }

        System.out.println("[Load]   Loading OBJ: " + objFile.getName());
        long startObj = System.currentTimeMillis();

        try {
            Model3D model = ReaderFactory.getReader(objFile).read(objFile);
            model.computeBoundingBox();

            long objTime = System.currentTimeMillis() - startObj;
            System.out.println("[Load]   OBJ loaded: " + model.getName()
                    + " (v:" + model.getVertexCount()
                    + " f:" + model.getFaceCount()
                    + ", " + objTime + "ms)");

            Model3DInstance instance = new Model3DInstance(model, dto.objFile());

            RotationDto rot = dto.rotation();
            if (rot != null) {
                instance.getRotation().setAngleX(rot.angleX());
                instance.getRotation().setAngleY(rot.angleY());
                instance.getRotation().setAngleZ(rot.angleZ());
            }

            instance.setTranslateX(dto.translateX());
            instance.setTranslateY(dto.translateY());
            instance.setScaleFactor(dto.scaleFactor());
            instance.setReflectX(dto.reflectX());
            instance.setReflectY(dto.reflectY());
            instance.setPerspectiveEnabled(dto.perspectiveEnabled());

            layer.getState().addModel(instance);

        } catch (ReaderException e) {
            System.err.println("[Load] Failed to load model: " + dto.objFile() + " - " + e.getMessage());
        }
    }
}
