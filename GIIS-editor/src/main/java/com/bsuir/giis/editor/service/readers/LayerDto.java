package com.bsuir.giis.editor.service.readers;

import java.util.List;

public record LayerDto(int id, List<ShapeDto> shapes, List<ModelDto> models) {}
