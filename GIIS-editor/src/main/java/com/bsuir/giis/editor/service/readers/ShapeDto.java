package com.bsuir.giis.editor.service.readers;

import com.bsuir.giis.editor.model.Point;
import java.util.List;

public record ShapeDto(String type, List<Point> points) {}
