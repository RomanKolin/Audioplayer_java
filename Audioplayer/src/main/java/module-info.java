module com.example.audioplayer
{
    requires jdk.compiler;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.graphics;
    requires java.desktop;
    requires java.logging;
    requires java.sql;
    requires jaudiotagger;

    opens com.example.audioplayer to javafx.fxml;
    exports com.example.audioplayer;
}