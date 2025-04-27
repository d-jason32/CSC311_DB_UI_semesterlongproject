module com.example.csc311_db_ui_semesterlongproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.prefs;
    requires simple.openai;
    requires java.net.http;
    requires org.slf4j.simple;
    requires layout;
    requires kernel;
    requires com.opencsv;

    opens viewmodel;
    exports viewmodel;
    opens dao;
    exports dao;
    opens model;
    exports model;
}