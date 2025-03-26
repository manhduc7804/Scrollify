module org.scrollify.main {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;

    opens org.scrollify.model to javafx.base;

    exports org.scrollify.controller to javafx.fxml;
    opens org.scrollify.controller to javafx.fxml;
    exports org.scrollify.main;
}