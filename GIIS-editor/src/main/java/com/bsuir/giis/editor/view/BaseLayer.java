package com.bsuir.giis.editor.view;

import org.w3c.dom.css.RGBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BaseLayer extends JPanel {
    protected volatile BufferedImage canvasImage;
    protected int pixelSize;
    protected int width;
    protected int height;
    protected boolean isTransparentLayer;
    public BaseLayer(int width, int height) {
        this.height = height;
        this.width = width;
        setupCanvas(width, height);

    }
    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(isOpaque);
        this.isTransparentLayer = !isOpaque;
    }
    public int getPixelSize() {
        return pixelSize;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    protected void setupCanvas(int width, int height) {
        canvasImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = canvasImage.createGraphics();

        if (isTransparentLayer) {
            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0, 0, width, height);
            g2.setComposite(AlphaComposite.SrcOver);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);
        }
        g2.dispose();

        int displayWidth = width * (pixelSize > 0 ? pixelSize : 1);
        int displayHeight = height * (pixelSize > 0 ? pixelSize : 1);
        setPreferredSize(new Dimension(displayWidth, displayHeight));

        if (!isTransparentLayer) {
            setBackground(Color.WHITE);
        }
        repaint();
    }

    public void paintPixel(int x, int y,Color color) {
        int px = x / pixelSize;
        int py = y / pixelSize;

        if (px >= 0 && px < width && py >= 0 && py < height) {
            canvasImage.setRGB(px, py, color.getRGB());

            repaint(
                    px * pixelSize,
                    py * pixelSize,
                    pixelSize,
                    pixelSize
            );
        }
    }
    public void paintPixel(int x, int y, RGBColor color, int brightness) {
        int px = x / pixelSize;
        int py = y / pixelSize;

        if (px >= 0 && px < width && py >= 0 && py < height) {

            brightness = Math.max(0, Math.min(255, brightness));
            Color newColor=new Color(color.getRed().getPrimitiveType(),color.getGreen().getPrimitiveType(),color.getBlue().getPrimitiveType(),brightness);
            canvasImage.setRGB(px, py,newColor.getRGB());
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
    public void cleanLayer(){
        setupCanvas(width, height);
    }

}
