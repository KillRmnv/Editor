package com.bsuir.giis.editor.service.readers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.bsuir.giis.editor.exceptions.ReaderException;

public class ReaderFactory {
    
    private static final Map<String, Supplier<ModelReader>> readers = new HashMap<>();
    
    static {
        readers.put("obj", ObjReader::new);
    }
    
    public static ModelReader getReader(String extension) throws ReaderException {
        String ext = extension.toLowerCase().startsWith(".") 
            ? extension.substring(1).toLowerCase() 
            : extension.toLowerCase();
        
        Supplier<ModelReader> supplier = readers.get(ext);
        if (supplier == null) {
            throw new ReaderException("Unsupported file format: ." + ext);
        }
        
        return supplier.get();
    }
    
    public static ModelReader getReader(File file) throws ReaderException {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        
        if (dotIndex == -1) {
            throw new ReaderException("File has no extension: " + fileName);
        }
        
        String extension = fileName.substring(dotIndex);
        return getReader(extension);
    }
    
    public static void registerReader(String extension, Supplier<ModelReader> readerSupplier) {
        String ext = extension.toLowerCase().startsWith(".") 
            ? extension.substring(1).toLowerCase() 
            : extension.toLowerCase();
        readers.put(ext, readerSupplier);
    }
}
