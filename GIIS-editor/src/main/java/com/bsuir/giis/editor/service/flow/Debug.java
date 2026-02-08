package com.bsuir.giis.editor.service.flow;

import com.bsuir.giis.editor.utils.Step;

import javax.swing.*;

public class Debug implements Mode {
    private final Object lock = new Object();
    private boolean proceed = false;
    private boolean skip = false;
    private final static JTextArea textArea = new JTextArea();

    @Override
    public void onStep(Step step,String logLine) {
        SwingUtilities.invokeLater(()->textArea.append(logLine+step.toString()));
        synchronized (lock) {
            if (!skip) {
                while (!proceed) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                proceed = false;
            }
        }
    }

    @Override
    public void onFinish() {
        synchronized (lock) {
            skip = false;
        }
    }

    public void nextStep() {
        synchronized (lock) {
            proceed = true;
            lock.notifyAll();
        }
    }

    public void skip() {
        synchronized (lock) {
            skip = true;
            proceed = true;
            lock.notifyAll();
        }
    }
    public JTextArea getTextArea() {
        return textArea;
    }
}
