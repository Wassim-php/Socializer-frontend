module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.desktop;
    requires javafx.graphics;



    opens com.example.demo to javafx.fxml;
    opens com.example.demo.model to com.fasterxml.jackson.databind;
    opens com.example.demo.controllers to javafx.fxml;
    opens com.example.demo.api to javafx.fxml;

    exports com.example.demo;
    exports com.example.demo.controllers;
    exports com.example.demo.api;
}
