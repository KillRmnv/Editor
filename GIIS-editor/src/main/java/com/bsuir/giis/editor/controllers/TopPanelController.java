package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.controllers.handlers.ConvexHullHandler;
import com.bsuir.giis.editor.controllers.handlers.DrawableHandler;
import com.bsuir.giis.editor.controllers.handlers.ParametersCurveHandler;
import com.bsuir.giis.editor.controllers.handlers.PenHandler;
import com.bsuir.giis.editor.controllers.handlers.SimplePolygonHandler;
import com.bsuir.giis.editor.controllers.handlers.TwoPointHandler;
import com.bsuir.giis.editor.controllers.handlers.curves.CurveEllipseHandler;
import com.bsuir.giis.editor.controllers.handlers.curves.CurveHyperbolaHandler;
import com.bsuir.giis.editor.model.Pen;
import com.bsuir.giis.editor.service.curves.*;
import com.bsuir.giis.editor.service.lines.Antialiasing;
import com.bsuir.giis.editor.service.lines.BresenhamAlgorithm;
import com.bsuir.giis.editor.service.lines.CDAAlgorithm;
import com.bsuir.giis.editor.service.lines.StraightLineAlgorithm;
import com.bsuir.giis.editor.service.parameterCurves.BSplineAlgorithm;
import com.bsuir.giis.editor.service.parameterCurves.BezierAlgorithm;
import com.bsuir.giis.editor.service.parameterCurves.HermiteAlgorithm;
import com.bsuir.giis.editor.service.parameterCurves.ParameterCurveAlgorithm;
import com.bsuir.giis.editor.service.polygons.*;
import com.bsuir.giis.editor.utils.MultiStep;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.TopToolbar;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

public final class TopPanelController {
    private final TopToolbar top;
    private final ToolContainer tool;

    public TopPanelController(TopToolbar top, ToolContainer tool) {
        this.top = top;
        this.tool = tool;
        top.getPenButton().addActionListener(e -> {
            tool.setTool(new Pen());
            tool.setHandler(new PenHandler(new PenStep()));
        });
        setupLineMenu();
        setupCurveMenu();
        setupParametersCurveMenu();
        setupPolygonsMenu();
        setupReadyMadeMenu();
    }

    private void setupLineMenu() {
        JMenuItem antialiasItem = top.getLineMenu().getItem(0);
        JMenuItem cdaItem = top.getLineMenu().getItem(1);
        JMenuItem bresenhamItem = top.getLineMenu().getItem(2);

        antialiasItem.addActionListener(new LineMenuListener(new Antialiasing()));
        cdaItem.addActionListener(new LineMenuListener(new CDAAlgorithm()));
        bresenhamItem.addActionListener(new LineMenuListener(new BresenhamAlgorithm()));
    }

    private void setupCurveMenu() {
        JMenuItem ellipseItem = top.getCurveMenu().getItem(0);
        JMenuItem circleItem = top.getCurveMenu().getItem(1);
        JMenuItem parabolaItem = top.getCurveMenu().getItem(2);
        JMenuItem hyperbolaItem = top.getCurveMenu().getItem(3);

        try {
            ellipseItem.addActionListener(new CurveMenuListener(new EllipseAlgorithm(),
                    new CurveEllipseHandler(new MultiStep(3, PenStep.class))));
            circleItem.addActionListener(new CurveMenuListener(new CircleAlgorithm(), 
                    new TwoPointHandler(new MultiStep(2, PenStep.class))));
            parabolaItem.addActionListener(new CurveMenuListener(new ParabolaAlgorithm(), 
                    new TwoPointHandler(new MultiStep(2, PenStep.class))));
            hyperbolaItem.addActionListener(new CurveMenuListener(new HyperbolaAlgorithm(),
                    new CurveHyperbolaHandler(new MultiStep(3, PenStep.class))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setupParametersCurveMenu() {
        JMenuItem hermitItem = top.getParameterCurvesMenu().getItem(0);
        JMenuItem bezierItem = top.getParameterCurvesMenu().getItem(1);
        JMenuItem splineItem = top.getParameterCurvesMenu().getItem(2);

        try {
            hermitItem.addActionListener(new ParameterCurveListener(new HermiteAlgorithm(),
                    new ParametersCurveHandler(new MultiStep(4, PenStep.class))));
            bezierItem.addActionListener(new ParameterCurveListener(new BezierAlgorithm(),
                    new ParametersCurveHandler(new MultiStep(4, PenStep.class))));
            splineItem.addActionListener(new ParameterCurveListener(new BSplineAlgorithm(),
                    new ParametersCurveHandler(new MultiStep(4, PenStep.class))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setupPolygonsMenu() {
        JMenuItem simpleItem = top.getPolygonsMenu().getItem(0);
        JMenuItem grahamItem = top.getPolygonsMenu().getItem(1);
        JMenuItem jarvisItem = top.getPolygonsMenu().getItem(2);

        simpleItem.addActionListener(e -> {
            tool.setTool(new SimplePolygonAlgorithm());
            tool.setHandler(new SimplePolygonHandler());
        });
        grahamItem.addActionListener(e -> {
            tool.setTool(new GrahamScanAlgorithm());
            tool.setHandler(new ConvexHullHandler());
        });
        jarvisItem.addActionListener(e -> {
            tool.setTool(new JarvisMarchAlgorithm());
            tool.setHandler(new ConvexHullHandler());
        });
    }

    private void setupReadyMadeMenu() {
        JMenuItem regularItem = top.getReadyMadeMenu().getItem(0);
        JMenuItem rightTriItem = top.getReadyMadeMenu().getItem(1);
        JMenuItem isoscelesItem = top.getReadyMadeMenu().getItem(2);
        JMenuItem rectangleItem = top.getReadyMadeMenu().getItem(3);

        regularItem.addActionListener(e -> {
            top.getSidesSpinner().setVisible(true);
            int sides = (int) top.getSidesSpinner().getValue();
            tool.setTool(new RegularPolygonAlgorithm(sides));
            try {
                tool.setHandler(new TwoPointHandler(new MultiStep(2, PenStep.class)));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        rightTriItem.addActionListener(e -> {
            top.getSidesSpinner().setVisible(false);
            tool.setTool(new RightTriangleAlgorithm());
            try {
                tool.setHandler(new TwoPointHandler(new MultiStep(2, PenStep.class)));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        isoscelesItem.addActionListener(e -> {
            top.getSidesSpinner().setVisible(false);
            tool.setTool(new IsoscelesTriangleAlgorithm());
            try {
                tool.setHandler(new TwoPointHandler(new MultiStep(2, PenStep.class)));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        rectangleItem.addActionListener(e -> {
            top.getSidesSpinner().setVisible(false);
            tool.setTool(new RectangleAlgorithm());
            try {
                tool.setHandler(new TwoPointHandler(new MultiStep(2, PenStep.class)));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private class LineMenuListener implements ActionListener {
        private StraightLineAlgorithm algorithm;

        public LineMenuListener(StraightLineAlgorithm algorithm) {
            this.algorithm = algorithm;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tool.setTool(algorithm);
            MultiStep Step;
            try {
                Step = new MultiStep(2, PenStep.class);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                     InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
            tool.setHandler(new TwoPointHandler(Step));

        }
    }

    private class CurveMenuListener implements ActionListener {
        private final CurvesAlgorithm algorithm;
        private final DrawableHandler handler;


        public CurveMenuListener(CurvesAlgorithm algorithm, DrawableHandler algorithmHandler) {
            this.algorithm = algorithm;
            this.handler = algorithmHandler;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            tool.setTool(algorithm);
            tool.setHandler(handler);

        }
    }
    private class ParameterCurveListener implements ActionListener {
        private final ParameterCurveAlgorithm algorithm;
        private final DrawableHandler handler;
        public ParameterCurveListener(ParameterCurveAlgorithm algorithm, DrawableHandler algorithmHandler) {
            this.algorithm = algorithm;
            this.handler = algorithmHandler;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            tool.setTool(algorithm);
            tool.setHandler(handler);
        }
    }
}

