package ru.vsu.cs.tulitskayte_d_v;

import javafx.scene.control.ColorPicker;
import javafx.scene.transform.Scale;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import ru.vsu.cs.tulitskayte_d_v.drawers.LineDrawer;
import ru.vsu.cs.tulitskayte_d_v.drawers.WuLineDrawer;

import java.util.ArrayList;

public class HelloController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ColorPicker backgroundColorPicker;
    private Color currentLineColor = Color.BLACK;
    private Color currentBackgroundColor = Color.WHITE;

    ArrayList<Point2D> points = new ArrayList<>();
    private LineDrawer lineDrawer;
    private Point2D prevPoint = null;

    @FXML
    private void initialize() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        lineDrawer = new WuLineDrawer(graphicsContext);
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        canvas.setOnMouseClicked(this::handlePrimaryClick);
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseMoved(this::mouseMoved);
        colorPicker.setValue(Color.BLACK);
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                    currentLineColor = newValue;
        });
        backgroundColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentBackgroundColor = newValue;
            updateCanvasBackground();
        });

        canvas.setOnScroll(event -> {
            double zoomFactor = 1.1;
            double deltaY = event.getDeltaY();

            if (deltaY < 0) {
                zoomFactor = 1 / zoomFactor;
            }

            Scale scale = new Scale();
            scale.setPivotX(event.getX());
            scale.setPivotY(event.getY());
            scale.setX(canvas.getScaleX() * zoomFactor);
            scale.setY(canvas.getScaleY() * zoomFactor);

            canvas.getTransforms().add(scale);
            event.consume();
        });
    }

    private void updateCanvasBackground() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(currentBackgroundColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    Point2D last = new Point2D(0, 0);

    @FXML
    private void mouseMoved(MouseEvent event) {
        canvas.getGraphicsContext2D().clearRect(0, 0, 1000, 1000);
        updateCanvasBackground();
        lineDrawer.drawLine(400, 300, (int) event.getX(), (int) event.getY(), currentLineColor);
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
        graphicsContext.setFill(currentLineColor);
        graphicsContext.fillOval(clickPoint.getX() - POINT_RADIUS, clickPoint.getY() - POINT_RADIUS, 2 * POINT_RADIUS, 2 * POINT_RADIUS);

        if (!points.isEmpty()) {
            final Point2D lastPoint = points.get(points.size() - 1);
            lineDrawer.drawLine((int) lastPoint.getX(), (int) lastPoint.getY(), (int) clickPoint.getX(), (int) clickPoint.getY(), currentLineColor);
        }
        points.add(clickPoint);
    }
}
