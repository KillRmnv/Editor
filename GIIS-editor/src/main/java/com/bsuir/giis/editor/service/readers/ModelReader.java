package com.bsuir.giis.editor.service.readers;

import com.bsuir.giis.editor.exceptions.ReaderException;
import com.bsuir.giis.editor.model.dimensions.Model3D;

import java.io.File;

public interface ModelReader {
    Model3D read(File file) throws ReaderException;
}
