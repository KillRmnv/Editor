package com.bsuir.giis.editor.service.readers;

import com.bsuir.giis.editor.exceptions.ReaderException;
import com.bsuir.giis.editor.model.dimensions.Face3D;
import com.bsuir.giis.editor.model.dimensions.Model3D;
import com.bsuir.giis.editor.model.dimensions.Point3D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjReader implements ModelReader {

    @Override
    public Model3D read(File file) throws ReaderException {
        Model3D model = new Model3D();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            List<Point3D> vertices = new ArrayList<>();
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("\\s+");
                if (parts.length == 0) {
                    continue;
                }
                
                switch (parts[0]) {
                    case "v" -> {
                        if (parts.length >= 4) {
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);
                            double z = Double.parseDouble(parts[3]);
                            vertices.add(new Point3D(x, y, z));
                        }
                    }
                    case "f" -> {
                        Face3D face = parseFace(parts);
                        if (face != null) {
                            model.addFace(face);
                        }
                    }
                    case "o" -> {
                        if (parts.length > 1) {
                            model.setName(parts[1]);
                        }
                    }
                }
            }
            
            model.getVertices().addAll(vertices);
            
        } catch (IOException e) {
            throw new ReaderException("Failed to read file: " + file.getName(), e);
        } catch (NumberFormatException e) {
            throw new ReaderException("Invalid number format in file: " + file.getName(), e);
        }
        
        return model;
    }
    
    private Face3D parseFace(String[] parts) {
        List<Integer> vertexIndices = new ArrayList<>();
        List<Integer> textureIndices = new ArrayList<>();
        List<Integer> normalIndices = new ArrayList<>();
        
        for (int i = 1; i < parts.length; i++) {
            String[] indices = parts[i].split("/");
            
            int vertexIndex = Integer.parseInt(indices[0]);
            vertexIndices.add(vertexIndex < 0 ? vertexIndex : vertexIndex - 1);
            
            if (indices.length > 1 && !indices[1].isEmpty()) {
                int textureIndex = Integer.parseInt(indices[1]);
                textureIndices.add(textureIndex < 0 ? textureIndex : textureIndex - 1);
            }
            
            if (indices.length > 2 && !indices[2].isEmpty()) {
                int normalIndex = Integer.parseInt(indices[2]);
                normalIndices.add(normalIndex < 0 ? normalIndex : normalIndex - 1);
            }
        }
        
        return new Face3D(vertexIndices, textureIndices, normalIndices);
    }
}
