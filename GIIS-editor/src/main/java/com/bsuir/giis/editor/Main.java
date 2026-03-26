package com.bsuir.giis.editor;

import com.bsuir.giis.editor.controllers.BottomPanelController;
import com.bsuir.giis.editor.controllers.CanvasController;
import com.bsuir.giis.editor.controllers.FileMenuController;
import com.bsuir.giis.editor.controllers.LayerController;
import com.bsuir.giis.editor.controllers.TopPanelController;
import com.bsuir.giis.editor.controllers.handlers.PenHandler;
import com.bsuir.giis.editor.model.Pen;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.rendering.RenderThreadPool;
import com.bsuir.giis.editor.service.flow.Debug;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.utils.*;

import com.bsuir.giis.editor.view.BottomToolbar;
import com.bsuir.giis.editor.view.LayerPanel;
import com.bsuir.giis.editor.view.TopToolbar;

import javax.swing.*;
import java.awt.*;
public class Main {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(RenderThreadPool::shutdown));

        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("GIIS Editor");
        GraphicsConfiguration gc = frame.getGraphicsConfiguration();
        Rectangle bounds = gc.getBounds();
        frame.setSize(bounds.width-20, bounds.height-20);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JFrame debugFrame = getDebugFrame();

        ToolContainer tool =new ToolContainer(new Pen());
        tool.setHandler(new PenHandler(new PenStep()));
        ModeContainer mode=new ModeContainer(new Regular());

        JPanel mainPanel = new JPanel(new BorderLayout());

        BottomToolbar bottom=new BottomToolbar();
        Canvas regularCanvas = new Canvas(bounds.width,bounds.height-100,bottom.getCoordinates(),bottom.getField());
        regularCanvas.getLayer().setDefaultPixelSize();
        regularCanvas.getLayerMoveable().setDefaultPixelSize();
        regularCanvas.getLayerMorphable().setDefaultPixelSize();
        CanvasController canvasController = new CanvasController(regularCanvas,tool,mode);
        BottomPanelController bottomPanelController=new BottomPanelController(bottom,mode,debugFrame,regularCanvas,tool);

        TopToolbar top=new TopToolbar();
        TopPanelController topPanelController=new TopPanelController(top,tool);

        LayerPanel layerPanel = new LayerPanel();
        LayerController layerController = new LayerController(layerPanel, regularCanvas);

        FileMenuController fileMenuController = new FileMenuController(top.getFileMenuBar(), regularCanvas, bottom, layerController);

        mainPanel.add(regularCanvas, BorderLayout.CENTER);
        mainPanel.add(bottom.getBottom(), BorderLayout.SOUTH);
        mainPanel.add(top.getUpperPanel(), BorderLayout.NORTH);
        mainPanel.add(layerPanel, BorderLayout.WEST);

        frame.add(mainPanel);
        frame.setVisible(true);

    }

    private static JFrame getDebugFrame() {
        JFrame debugFrame = new JFrame();
        debugFrame.setVisible(false);
        debugFrame.setSize(400, 400);

        JPanel logsPanel = new JPanel(new BorderLayout());
        JTextArea textArea = new Debug().getTextArea();
        textArea.setEditable(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        logsPanel.add(scrollPane, BorderLayout.CENTER);
        debugFrame.add(logsPanel, BorderLayout.CENTER);
        return debugFrame;
    }
}
