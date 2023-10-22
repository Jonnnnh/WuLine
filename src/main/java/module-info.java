module com.example.wuline {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.vsu.cs.tulitskayte_d_v to javafx.fxml;
    exports ru.vsu.cs.tulitskayte_d_v;
    exports ru.vsu.cs.tulitskayte_d_v.drawers;
    opens ru.vsu.cs.tulitskayte_d_v.drawers to javafx.fxml;
}