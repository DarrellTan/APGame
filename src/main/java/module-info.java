module com.example.gametest {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.gametest to javafx.fxml;
    exports com.example.gametest;
}