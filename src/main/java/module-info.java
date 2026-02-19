module com.example.sequax40 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires junit;
    requires org.junit.jupiter.api;


    exports com.example.sequax40.app;
    opens com.example.sequax40.app to javafx.fxml;
    exports com.example.sequax40.controller;
    opens com.example.sequax40.controller to javafx.fxml;
}