package com.bsuir.giis.editor.model.dimensions;

import java.util.List;

public class Face3D {
    private final List<Integer> vertexIndices;
    private final List<Integer> textureIndices;
    private final List<Integer> normalIndices;

    public Face3D(List<Integer> vertexIndices, List<Integer> textureIndices, List<Integer> normalIndices) {
        this.vertexIndices = vertexIndices;
        this.textureIndices = textureIndices;
        this.normalIndices = normalIndices;
    }

    public Face3D(List<Integer> vertexIndices) {
        this(vertexIndices, List.of(), List.of());
    }

    public List<Integer> getVertexIndices() {
        return vertexIndices;
    }

    public List<Integer> getTextureIndices() {
        return textureIndices;
    }

    public List<Integer> getNormalIndices() {
        return normalIndices;
    }

    public int getVertexCount() {
        return vertexIndices.size();
    }
}
