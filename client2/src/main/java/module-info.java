module pl.edu.client2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires xmlrpc.client;
    requires xmlrpc.common;

    opens pl.edu.client2 to javafx.fxml;
    exports pl.edu.client2;
}

