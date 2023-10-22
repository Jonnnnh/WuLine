module com.example.wuline {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.wuline to javafx.fxml;
    exports com.example.wuline;
    exports com.example.wuline.drawers;
    opens com.example.wuline.drawers to javafx.fxml;
}