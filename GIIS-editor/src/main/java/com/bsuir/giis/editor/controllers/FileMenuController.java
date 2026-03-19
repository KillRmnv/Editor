package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.model.dimensions.Model3D;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.exceptions.ReaderException;
import com.bsuir.giis.editor.service.readers.ReaderFactory;

import javax.swing.*;
import java.io.File;

public class FileMenuController {

    public FileMenuController(JMenu fileMenu, Canvas canvas) {
        int itemCount = fileMenu.getItemCount();
        
        JMenuItem openItem = itemCount > 0 ? fileMenu.getItem(0) : null;
        JMenuItem saveItem = itemCount > 1 ? fileMenu.getItem(1) : null;

        fileMenu.addSeparator();
        
        JMenuItem load3DItem = new JMenuItem("Load 3D Model...");
        fileMenu.add(load3DItem);

        if (openItem != null) {
            openItem.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Open File");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.showOpenDialog(null);
            });
        }

        if (saveItem != null) {
            saveItem.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save File");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.showSaveDialog(null);
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

                    canvas.getLayer2D().getState().setCurrentModel(model);
                    canvas.getLayer2D().renderAndRepaint();
                } catch (ReaderException ex) {
                    JOptionPane.showMessageDialog(null,
                        "Failed to load model: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
