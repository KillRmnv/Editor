package com.bsuir.giis.editor.model.lines;

import com.bsuir.giis.editor.model.Tool;
import com.bsuir.giis.editor.service.lines.StraightLineAlgorithm;

public class Line implements Tool {
    private StraightLineAlgorithm straightLineAlgorithm;
    public Line(StraightLineAlgorithm straightLineAlgorithm) {
        this.straightLineAlgorithm = straightLineAlgorithm;
    }
    public StraightLineAlgorithm getStraightLineAlgorithm() {
        return straightLineAlgorithm;
    }
}
