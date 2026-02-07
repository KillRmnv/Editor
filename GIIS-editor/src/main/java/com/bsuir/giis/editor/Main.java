package com.bsuir.giis.editor;

import com.bsuir.giis.editor.controllers.BottomPanelController;
import com.bsuir.giis.editor.controllers.CanvasController;
import com.bsuir.giis.editor.controllers.TopPanelController;
import com.bsuir.giis.editor.model.Pen;
import com.bsuir.giis.editor.model.Tool;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.utils.PreviousStep;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.BottomToolbar;
import com.bsuir.giis.editor.view.Canvas;
import com.bsuir.giis.editor.view.TopToolbar;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("GIIS Editor");
        GraphicsConfiguration gc = frame.getGraphicsConfiguration();
        Rectangle bounds = gc.getBounds();
        frame.setSize(bounds.width-20, bounds.height-20);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PreviousStep previousStep=new PreviousStep(new PenStep());
        ToolContainer tool =new ToolContainer(new Pen());
        Mode mode=new Regular();

        JPanel mainPanel = new JPanel(new BorderLayout());

        Canvas regularCanvas = new Canvas(bounds.width,bounds.height-100);
        CanvasController canvasController = new CanvasController(regularCanvas,tool,mode,previousStep);

        BottomToolbar bottom=new BottomToolbar(regularCanvas.getCoordinates());
        BottomPanelController bottomPanelController=new BottomPanelController(bottom);

        TopToolbar top=new TopToolbar();
        TopPanelController topPanelController=new TopPanelController(top,tool,previousStep);

        mainPanel.add(regularCanvas, BorderLayout.CENTER);
        mainPanel.add(bottom.getBottom(), BorderLayout.SOUTH);
        mainPanel.add(top.getUpperPanel(), BorderLayout.NORTH);

        frame.add(mainPanel);
        frame.setVisible(true);

    }
}