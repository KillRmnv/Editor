package com.bsuir.giis.editor.service.flow;

import com.bsuir.giis.editor.utils.Step;

public interface Mode {
     void onStep(Step step,String logLine) ;
     void onFinish() ;
}
