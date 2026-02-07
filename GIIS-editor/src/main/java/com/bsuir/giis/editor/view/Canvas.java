package com.bsuir.giis.editor.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


public class Canvas extends JPanel {

    private final int pixelSize = 1;
    private int width;
    private int height;
    private JLabel coordinates;

    private volatile BufferedImage canvasImage;


    public Canvas(int width, int height) {
        this.height = height;
        this.width = width;
        canvasImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = canvasImage.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.dispose();

        setPreferredSize(new Dimension(width * pixelSize, height * pixelSize));
        setBackground(Color.WHITE);
        coordinates = new JLabel("x:0,y:0");
    }

    public JLabel getCoordinates() {
        return coordinates;
    }

    public void paintPixel(int x, int y) {
        int px = x / pixelSize;
        int py = y / pixelSize;

        if (px >= 0 && px < width && py >= 0 && py < height) {
            canvasImage.setRGB(px, py, Color.BLACK.getRGB());

            repaint(
                    px * pixelSize,
                    py * pixelSize,
                    pixelSize,
                    pixelSize
            );
        }
    }
    public void paintPixel(int x, int y,int brightness) {
        int px = x / pixelSize;
        int py = y / pixelSize;

        if (px >= 0 && px < width && py >= 0 && py < height) {

            brightness = Math.max(0, Math.min(255, brightness));

            // Создаём цвет с альфой
            Color c = new Color(0, 0, 0, brightness);
            canvasImage.setRGB(px, py,c.getRGB());
            repaint(
                    px * pixelSize,
                    py * pixelSize,
                    pixelSize,
                    pixelSize
            );
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(
                canvasImage,
                0, 0,
                width * pixelSize,
                height * pixelSize,
                null
        );
    }

}