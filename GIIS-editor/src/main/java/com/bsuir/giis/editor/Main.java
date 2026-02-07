package com.bsuir.giis.editor;

import com.bsuir.giis.editor.controllers.BottomPanelController;
import com.bsuir.giis.editor.controllers.CanvasController;
import com.bsuir.giis.editor.controllers.TopPanelController;
import com.bsuir.giis.editor.userTools.Mode;
import com.bsuir.giis.editor.userTools.Pen;
import com.bsuir.giis.editor.userTools.Regular;
import com.bsuir.giis.editor.userTools.Tool;
import com.bsuir.giis.editor.view.BottomToolbar;
import com.bsuir.giis.editor.view.Canvas;
import com.bsuir.giis.editor.view.TopToolbar;

import javax.swing.*;
import java.awt.*;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("GIIS Editor");
        GraphicsConfiguration gc = frame.getGraphicsConfiguration();
        Rectangle bounds = gc.getBounds();
        frame.setSize(bounds.width-20, bounds.height-20);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Tool tool = new Pen();
        Mode mode=new Regular();

        JPanel mainPanel = new JPanel(new BorderLayout());

        Canvas regularCanvas = new Canvas(bounds.width,bounds.height-100);
        CanvasController canvasController = new CanvasController(regularCanvas,tool,mode);

        BottomToolbar bottom=new BottomToolbar(regularCanvas.getCoordinates());
        BottomPanelController bottomPanelController=new BottomPanelController(bottom);

        TopToolbar top=new TopToolbar();
        TopPanelController topPanelController=new TopPanelController(top);

        mainPanel.add(regularCanvas, BorderLayout.CENTER);
        mainPanel.add(bottom.getBottom(), BorderLayout.SOUTH);
        mainPanel.add(top.getUpperPanel(), BorderLayout.NORTH);

        frame.add(mainPanel);
        frame.setVisible(true);

    }
}