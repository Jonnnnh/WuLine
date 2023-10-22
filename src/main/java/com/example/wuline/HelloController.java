package com.example.wuline;

import com.example.wuline.drawers.*;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class HelloController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    ArrayList<Point2D> points = new ArrayList<>();
    private LineDrawer lineDrawer;
    private Point2D prevPoint = null;
    private double zoomFactor = 1.0;
    private static final double ZOOM_INTENSITY = 0.02;
    private static final double MIN_ZOOM = 0.2;
    private static final double MAX_ZOOM = 5.0;

    @FXML
    private void initialize() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        GraphicsContextPixelDrawer pixelDrawer = new GraphicsContextPixelDrawer(graphicsContext);
        lineDrawer = new WuLineDrawer(pixelDrawer);
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        canvas.setOnMouseClicked(this::handlePrimaryClick);
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnScroll(this::handleScroll);
        canvas.setOnMouseDragged(this::handleMouseDragged);
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY && prevPoint != null) {
            Point2D currentPoint = canvas.localToParent(event.getX(), event.getY());
            double deltaX = currentPoint.getX() - prevPoint.getX();
            double deltaY = currentPoint.getY() - prevPoint.getY();
            canvas.setTranslateX(canvas.getTranslateX() + deltaX);
            canvas.setTranslateY(canvas.getTranslateY() + deltaY);
            prevPoint = currentPoint;
        }
    }

    private void handleMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            prevPoint = canvas.localToParent(event.getX(), event.getY());
        }
    }

    private void handlePrimaryClick(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) return;
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        final Point2D clickPoint = new Point2D(event.getX(), event.getY());
        final int POINT_RADIUS = 2;
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillOval(clickPoint.getX() - POINT_RADIUS, clickPoint.getY() - POINT_RADIUS, 2 * POINT_RADIUS, 2 * POINT_RADIUS);

        if (!points.isEmpty()) {
            final Point2D lastPoint = points.get(points.size() - 1);
            lineDrawer.drawLine((int) lastPoint.getX(), (int) lastPoint.getY(), (int) clickPoint.getX(), (int) clickPoint.getY(), Color.BLACK);
        }
        points.add(clickPoint);
    }

    private void handleScroll(ScrollEvent event) {
        double delta = event.getDeltaY();
        double zoom = Math.exp(ZOOM_INTENSITY * delta);
        double newZoomFactor = Math.max(MIN_ZOOM, Math.min(zoomFactor * zoom, MAX_ZOOM));

        if (newZoomFactor != zoomFactor) {
            double scaleFactor = newZoomFactor / zoomFactor;
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            double adjustmentX = (1 - scaleFactor) * (mouseX - (canvas.getBoundsInParent().getMinX() + canvas.getTranslateX()));
            double adjustmentY = (1 - scaleFactor) * (mouseY - (canvas.getBoundsInParent().getMinY() + canvas.getTranslateY()));

            canvas.setScaleX(newZoomFactor);
            canvas.setScaleY(newZoomFactor);
            canvas.setTranslateX(canvas.getTranslateX() - adjustmentX);
            canvas.setTranslateY(canvas.getTranslateY() - adjustmentY);

            zoomFactor = newZoomFactor;
        }
    }
}
