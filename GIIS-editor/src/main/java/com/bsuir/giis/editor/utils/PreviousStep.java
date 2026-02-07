package com.bsuir.giis.editor.utils;

public class PreviousStep {
    private Step step;
    public PreviousStep(Step step) {
        this.step = step;
    }
    public Step getStep() {
        return step;
    }
    public void setStep(Step step) {
        this.step = step;
    }
}
