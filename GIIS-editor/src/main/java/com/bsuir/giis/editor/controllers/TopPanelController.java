package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.model.Pen;
import com.bsuir.giis.editor.model.Tool;
import com.bsuir.giis.editor.model.lines.Line;
import com.bsuir.giis.editor.service.lines.Antialiasing;
import com.bsuir.giis.editor.service.lines.BresenhamAlgorithm;
import com.bsuir.giis.editor.service.lines.CDAAlgorithm;
import com.bsuir.giis.editor.utils.LineStep;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.utils.PreviousStep;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.TopToolbar;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TopPanelController {
    private TopToolbar topToolbar;
    private ToolContainer selectedTool;
    private PreviousStep previousStep;

    public TopPanelController(TopToolbar topToolbar, ToolContainer tool, PreviousStep previousStep) {
        this.topToolbar = topToolbar;
        this.selectedTool = tool;
        this.previousStep = previousStep;

        topToolbar.getPenButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedTool.setTool(new Pen());
                previousStep.setStep(new PenStep());
            }
        });

        lineMenuConfiguration(topToolbar, previousStep);

    }

    private void lineMenuConfiguration(TopToolbar topToolbar, PreviousStep previousStep) {
        topToolbar.getLineMenu().getItem(0).addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedTool.setTool(new Line(new Antialiasing()));
                previousStep.setStep(new LineStep());
            }
        });
        topToolbar.getLineMenu().getItem(1).addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedTool.setTool(new Line(new CDAAlgorithm()));
                previousStep.setStep(new LineStep());
            }
        });
        topToolbar.getLineMenu().getItem(2).addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedTool.setTool(new Line(new BresenhamAlgorithm()));
                previousStep.setStep(new LineStep());
            }
        });
    }

}
