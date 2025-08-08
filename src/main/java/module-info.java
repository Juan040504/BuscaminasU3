module com.example.gato {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.fasterxml.jackson.databind;
    
    // Dependencias para SQLite
    requires java.sql;
    requires org.slf4j;

    opens aplication to javafx.fxml;
    exports aplication;
    exports controller;
    opens controller to javafx.fxml;

    opens celda to javafx.fxml;
    exports celda;
    
    // Exportar el paquete persistence
    exports persistence;
}