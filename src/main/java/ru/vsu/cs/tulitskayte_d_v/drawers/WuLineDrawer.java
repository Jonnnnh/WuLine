package ru.vsu.cs.tulitskayte_d_v.drawers;

import javafx.scene.paint.Color;

public class WuLineDrawer implements LineDrawer {
    private final PixelDrawer pixelDrawer;

    public WuLineDrawer(PixelDrawer pixelDrawer) {
        this.pixelDrawer = pixelDrawer;
    }

    private double fractionalPart(double x) {
        return x - (int) x;
    }

    private void drawWuLine(int xStart, int yStart, int xEnd, int yEnd, boolean isSteep) {
        double deltaX = xEnd - xStart;
        double deltaY = yEnd - yStart;
        double gradient = deltaY / deltaX;
        double intersection = yStart + gradient;

        for (int x = xStart; x < xEnd; x++) {
            double frac = fractionalPart(intersection);
            double baseAlpha = 1.0 - frac;
            double topAlpha = frac;

            if (isSteep) {
                pixelDrawer.drawPixel((int) intersection, x, Color.color(0, 0, 0, baseAlpha));
                pixelDrawer.drawPixel((int) intersection + 1, x, Color.color(0, 0, 0, topAlpha));
            } else {
                pixelDrawer.drawPixel(x, (int) intersection, Color.color(0, 0, 0, baseAlpha));
                pixelDrawer.drawPixel(x, (int) intersection + 1, Color.color(0, 0, 0, topAlpha));
            }

            intersection += gradient;
        }
    }

    @Override
    public void drawLine(int xStart, int yStart, int xEnd, int yEnd, Color color) {
        boolean isSteep = Math.abs(yEnd - yStart) > Math.abs(xEnd - xStart);

        if (isSteep) {
            int temp = xStart; xStart = yStart; yStart = temp;
            temp = xEnd; xEnd = yEnd; yEnd = temp;
        }

        if (xStart > xEnd) {
            int temp = xStart; xStart = xEnd; xEnd = temp;
            temp = yStart; yStart = yEnd; yEnd = temp;
        }

        drawWuLine(xStart, yStart, xEnd, yEnd, isSteep);

        if (isSteep) {
            pixelDrawer.drawPixel(yStart, xStart, Color.BLACK);
            pixelDrawer.drawPixel(yEnd, xEnd, Color.BLACK);
        } else {
            pixelDrawer.drawPixel(xStart, yStart, Color.BLACK);
            pixelDrawer.drawPixel(xEnd, yEnd, Color.BLACK);
        }
    }
}