module edu.pmoc.practicatrim.practicatrimestralpmoc {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens edu.pmoc.practicatrim.practicatrimestralpmoc to javafx.fxml;
    exports edu.pmoc.practicatrim.practicatrimestralpmoc;

    exports edu.pmoc.practicatrim.practicatrimestralpmoc.controller;
    opens edu.pmoc.practicatrim.practicatrimestralpmoc.controller to javafx.fxml;

    exports edu.pmoc.practicatrim.practicatrimestralpmoc.model;
    opens edu.pmoc.practicatrim.practicatrimestralpmoc.model to javafx.fxml;

    exports edu.pmoc.practicatrim.practicatrimestralpmoc.db;
    opens edu.pmoc.practicatrim.practicatrimestralpmoc.db to javafx.fxml;
}