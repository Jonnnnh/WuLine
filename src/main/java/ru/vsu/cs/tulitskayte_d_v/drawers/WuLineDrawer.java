package ru.vsu.cs.tulitskayte_d_v.drawers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class WuLineDrawer implements LineDrawer {
    private final GraphicsContext graphicsContext;

    public WuLineDrawer(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    private double fractionalPart(double value) {
        return value - Math.floor(value);
    }

    private double reversedFractionalPart(double value) {
        return 1.0 - fractionalPart(value);
    }

    private void drawPixelWithBrightness(int x, int y, double brightness, Color color) {
        int alpha = (int) (brightness * 255);
        graphicsContext.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), alpha / 255.0));
        graphicsContext.fillRect(x, y, 1, 1);
    }

    private void drawWuLine(double startX, double startY, double endX, double endY, Color color) {
        boolean isSteep = Math.abs(endY - startY) > Math.abs(endX - startX);
        if (isSteep) {
            double temp = startX; startX = startY; startY = temp;
            temp = endX; endX = endY; endY = temp;
        }

        if (startX > endX) {
            double temp = startX; startX = endX; endX = temp;
            temp = startY; startY = endY; endY = temp;
        }
        double deltaX = endX - startX;
        double deltaY = endY - startY;
        double gradient = deltaY / deltaX;

        int roundedStartX = (int) Math.round(startX);
        double gradientStartY = startY + gradient * (roundedStartX - startX);
        double startXGap = reversedFractionalPart(startX + 0.5);
        int pixelStartX = roundedStartX;
        int pixelStartY = (int)gradientStartY;

        if (isSteep) {
            drawPixelWithBrightness(pixelStartY, pixelStartX, reversedFractionalPart(gradientStartY) * startXGap, color);
            drawPixelWithBrightness(pixelStartY + 1, pixelStartX, fractionalPart(gradientStartY) * startXGap, color);
        } else {
            drawPixelWithBrightness(pixelStartX, pixelStartY, reversedFractionalPart(gradientStartY) * startXGap, color);
            drawPixelWithBrightness(pixelStartX, pixelStartY + 1, fractionalPart(gradientStartY) * startXGap, color);
        }

        double gradientIntersectY = gradientStartY + gradient;

        int roundedEndX = (int) Math.round(endX);
        double gradientEndY = endY + gradient * (roundedEndX - endX);
        double endXGap = fractionalPart(endX + 0.5);

        int pixelEndX = roundedEndX;
        int pixelEndY = (int)gradientEndY;

        if (isSteep) {
            drawPixelWithBrightness(pixelEndY, pixelEndX, reversedFractionalPart(gradientEndY) * endXGap, color);
            drawPixelWithBrightness(pixelEndY + 1, pixelEndX, fractionalPart(gradientEndY) * endXGap, color);
        } else {
            drawPixelWithBrightness(pixelEndX, pixelEndY, reversedFractionalPart(gradientEndY) * endXGap, color);
            drawPixelWithBrightness(pixelEndX, pixelEndY + 1, fractionalPart(gradientEndY) * endXGap, color);
        }

        for (int x = pixelStartX + 1; x <= pixelEndX - 1; x++) {
            if (isSteep) {
                drawPixelWithBrightness((int)gradientIntersectY, x, reversedFractionalPart(gradientIntersectY), color);
                drawPixelWithBrightness((int)gradientIntersectY + 1, x, fractionalPart(gradientIntersectY), color);
            } else {
                drawPixelWithBrightness(x, (int)gradientIntersectY, reversedFractionalPart(gradientIntersectY), color);
                drawPixelWithBrightness(x, (int)gradientIntersectY + 1, fractionalPart(gradientIntersectY), color);
            }
            gradientIntersectY += gradient;
        }
    }

    @Override
    public void drawLine(int startX, int startY, int endX, int endY, Color color) {
        drawWuLine(startX, startY, endX, endY, color);
        graphicsContext.fillRect(startX, startY, 1, 1);
        graphicsContext.fillRect(endX, endY, 1, 1);

    }
}
