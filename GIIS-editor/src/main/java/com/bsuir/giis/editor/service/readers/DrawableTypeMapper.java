package com.bsuir.giis.editor.service.readers;

import com.bsuir.giis.editor.model.Pen;
import com.bsuir.giis.editor.model.shapes.Drawable;
import com.bsuir.giis.editor.service.curves.CircleAlgorithm;
import com.bsuir.giis.editor.service.curves.EllipseAlgorithm;
import com.bsuir.giis.editor.service.curves.HyperbolaAlgorithm;
import com.bsuir.giis.editor.service.curves.ParabolaAlgorithm;
import com.bsuir.giis.editor.service.lines.Antialiasing;
import com.bsuir.giis.editor.service.lines.BresenhamAlgorithm;
import com.bsuir.giis.editor.service.lines.CDAAlgorithm;
import com.bsuir.giis.editor.service.parameterCurves.BSplineAlgorithm;
import com.bsuir.giis.editor.service.parameterCurves.BezierAlgorithm;
import com.bsuir.giis.editor.service.parameterCurves.HermiteAlgorithm;
import com.bsuir.giis.editor.service.polygons.GrahamScanAlgorithm;
import com.bsuir.giis.editor.service.polygons.JarvisMarchAlgorithm;
import com.bsuir.giis.editor.service.polygons.SimplePolygonAlgorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DrawableTypeMapper {

    private static final Map<String, Supplier<Drawable>> registry = new HashMap<>();

    static {
        registry.put("BresenhamAlgorithm", BresenhamAlgorithm::new);
        registry.put("CDAAlgorithm", CDAAlgorithm::new);
        registry.put("Antialiasing", Antialiasing::new);
        registry.put("EllipseAlgorithm", EllipseAlgorithm::new);
        registry.put("CircleAlgorithm", CircleAlgorithm::new);
        registry.put("ParabolaAlgorithm", ParabolaAlgorithm::new);
        registry.put("HyperbolaAlgorithm", HyperbolaAlgorithm::new);
        registry.put("HermiteAlgorithm", HermiteAlgorithm::new);
        registry.put("BezierAlgorithm", BezierAlgorithm::new);
        registry.put("BSplineAlgorithm", BSplineAlgorithm::new);
        registry.put("Pen", Pen::new);
        registry.put("SimplePolygonAlgorithm", SimplePolygonAlgorithm::new);
        registry.put("GrahamScanAlgorithm", GrahamScanAlgorithm::new);
        registry.put("JarvisMarchAlgorithm", JarvisMarchAlgorithm::new);
    }

    public static String getTypeName(Drawable drawable) {
        return drawable.getClass().getSimpleName();
    }

    public static Drawable createDrawable(String typeName) {
        Supplier<Drawable> supplier = registry.get(typeName);
        if (supplier == null) {
            throw new IllegalArgumentException("Unknown drawable type: " + typeName);
        }
        return supplier.get();
    }

    public static boolean isSupported(String typeName) {
        return registry.containsKey(typeName);
    }
}
