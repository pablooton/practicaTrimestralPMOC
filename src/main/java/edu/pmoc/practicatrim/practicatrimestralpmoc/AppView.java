package edu.pmoc.practicatrim.practicatrimestralpmoc;

public enum AppView {

    LOGIN("edu/pmoc/practicatrim/practicatrimestralpmoc/login-view.fxml"),
    MAIN("edu/pmoc/practicatrim/practicatrimestralpmoc/main-view.fxml");



    private final String fxmlFile;


    AppView(String fxmlFile) {
        this.fxmlFile = fxmlFile;
    }


    public String getFxmlFile() {
        return fxmlFile;
    }
}