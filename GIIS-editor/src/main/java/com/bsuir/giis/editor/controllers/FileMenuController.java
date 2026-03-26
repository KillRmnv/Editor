package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.model.Model3DInstance;
import com.bsuir.giis.editor.model.dimensions.Model3D;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.exceptions.ReaderException;
import com.bsuir.giis.editor.service.readers.ReaderFactory;
import com.bsuir.giis.editor.service.readers.SceneDeserializer;
import com.bsuir.giis.editor.service.readers.SceneSerializer;
import com.bsuir.giis.editor.view.BottomToolbar;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class FileMenuController {

    private final BottomToolbar bottomToolbar;
    private final LayerController layerController;

    public FileMenuController(JMenu fileMenu, Canvas canvas, BottomToolbar bottomToolbar, LayerController layerController) {
        this.bottomToolbar = bottomToolbar;
        this.layerController = layerController;
        int itemCount = fileMenu.getItemCount();
        
        JMenuItem openItem = itemCount > 0 ? fileMenu.getItem(0) : null;
        JMenuItem saveItem = itemCount > 1 ? fileMenu.getItem(1) : null;

        fileMenu.addSeparator();
        
        JMenuItem load3DItem = new JMenuItem("Load 3D Model...");
        fileMenu.add(load3DItem);

        if (openItem != null) {
            openItem.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Open Scene");
                fileChooser.addChoosableFileFilter(
                    new javax.swing.filechooser.FileNameExtensionFilter("GIIS Scene (*.giis)", "giis"));

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        SceneDeserializer.load(canvas, file, bottomToolbar.getField());
                        canvas.setActiveLayerIndex(0);
                        layerController.syncPanel();
                        System.out.println("[FileMenu] Scene loaded: " + file.getName());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null,
                            "Failed to load scene: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        if (saveItem != null) {
            saveItem.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Scene");
                fileChooser.addChoosableFileFilter(
                    new javax.swing.filechooser.FileNameExtensionFilter("GIIS Scene (*.giis)", "giis"));

                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (!file.getName().endsWith(".giis")) {
                        file = new File(file.getAbsolutePath() + ".giis");
                    }
                    try {
                        SceneSerializer.save(canvas, file);
                        System.out.println("[FileMenu] Scene saved: " + file.getName());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null,
                            "Failed to save scene: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
        load3DItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Load 3D Model");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("OBJ Files (*.obj)", "obj"));

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    Model3D model = ReaderFactory.getReader(file).read(file);
                    model.computeBoundingBox();
                    System.out.println("[FileMenu] Loaded: " + model.getName() +
                                     " (v:" + model.getVertexCount() +
                                     " f:" + model.getFaceCount() +
                                     " scale:" + String.format("%.2f", model.getNormalizedScale()) + ")");

                    Model3DInstance instance = new Model3DInstance(model, file.getAbsolutePath());
                    canvas.getLayer().getState().addModel(instance);
                    canvas.getLayer().renderAndRepaint();

                    JComboBox<String> selector = bottomToolbar.getModelSelector();
                    selector.addItem("Model " + canvas.getLayer().getState().getModelCount() + ": " + model.getName());
                    selector.setSelectedIndex(canvas.getLayer().getState().getActiveModelIndex());
                } catch (ReaderException ex) {
                    JOptionPane.showMessageDialog(null,
                        "Failed to load model: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
