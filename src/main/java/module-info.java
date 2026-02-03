module com.example.sequax40 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens com.example.sequax40 to javafx.fxml;
    exports com.example.sequax40;
}