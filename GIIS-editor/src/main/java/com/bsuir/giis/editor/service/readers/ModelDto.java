package com.bsuir.giis.editor.service.readers;

public record ModelDto(
    String objFile,
    RotationDto rotation,
    double translateX,
    double translateY,
    double scaleFactor,
    boolean reflectX,
    boolean reflectY,
    boolean perspectiveEnabled
) {}
