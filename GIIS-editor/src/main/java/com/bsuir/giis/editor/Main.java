package com.bsuir.giis.editor;

import com.bsuir.giis.editor.controllers.BottomPanelController;
import com.bsuir.giis.editor.controllers.CanvasController;
import com.bsuir.giis.editor.controllers.TopPanelController;
import com.bsuir.giis.editor.model.Pen;
import com.bsuir.giis.editor.service.flow.Debug;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.utils.ModeContainer;
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

        JFrame debugFrame = getDebugFrame();

        PreviousStep previousStep=new PreviousStep(new PenStep());
        ToolContainer tool =new ToolContainer(new Pen());
        ModeContainer mode=new ModeContainer(new Regular());

        JPanel mainPanel = new JPanel(new BorderLayout());

        BottomToolbar bottom=new BottomToolbar();
        Canvas regularCanvas = new Canvas(bounds.width,bounds.height-100,bottom.getCoordinates(),bottom.getField());
        CanvasController canvasController = new CanvasController(regularCanvas,tool,mode,previousStep);
        BottomPanelController bottomPanelController=new BottomPanelController(bottom,mode,debugFrame,regularCanvas);






        TopToolbar top=new TopToolbar();
        TopPanelController topPanelController=new TopPanelController(top,tool,previousStep);

        mainPanel.add(regularCanvas, BorderLayout.CENTER);
        mainPanel.add(bottom.getBottom(), BorderLayout.SOUTH);
        mainPanel.add(top.getUpperPanel(), BorderLayout.NORTH);

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