package com.bsuir.giis.editor.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class MultiStep implements Step {
    private List<Step> steps;
    private int stepIndex = 0;
    private int size = 0;
    public int getSize(){
        return size;
    }
    public MultiStep(List<Step> steps,int stepIndex) {
        this.steps = steps;
        this.size = steps.size();
        this.stepIndex = stepIndex;
    }

    public MultiStep(int steps, Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.steps = new ArrayList<>(steps);
        for (int i = 0; i < steps; i++) {
            this.steps.add((Step) clazz.getConstructor().newInstance());
        }
        this.size = steps;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public Step getStep(int index) {
        return steps.get(index);
    }

    @Override
    public boolean isReady() {
        for (Step step : steps) {
            if (!step.isReady()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void clean() {
        for (Step step : steps) {
            step.clean();
        }

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int stepIndex = 1;
        for (Step step : steps) {

            builder.append(stepIndex).append(": ").append(step.toString());
        }
        return builder.toString();
    }

    public void add(Step step) {
        steps.add(step);
    }

    public void setStep(int index, Step step) {
        steps.set(index, step);
    }

    public void setStep(Step step) {
        if (stepIndex == size ) {
            Class<?> clazz = step.getClass();
            steps.clear();
            stepIndex = 0;
            for (int i = 0; i < size; i++) {
                try {
                    this.steps.add((Step) clazz.getConstructor().newInstance());
                } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        steps.set(stepIndex, step);
        stepIndex++;
    }

    public MultiStep copy() {
        List<Step> stepsCopy = new ArrayList<>(size);
        stepsCopy.addAll(steps);
        return new MultiStep( stepsCopy,stepIndex);
    }

}
