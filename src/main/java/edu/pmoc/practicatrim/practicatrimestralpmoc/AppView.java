package edu.pmoc.practicatrim.practicatrimestralpmoc;

public enum AppView {

    LOGIN("/edu/pmoc/practicatrim/practicatrimestralpmoc/login-view.fxml"),
    MAIN("/edu/pmoc/practicatrim/practicatrimestralpmoc/main-view.fxml"),
    USERS("/edu/pmoc/practicatrim/practicatrimestralpmoc/users-view.fxml"),
    PROFILE("/edu/pmoc/practicatrim/practicatrimestralpmoc/profile-view.fxml"),
    MARKET("/edu/pmoc/practicatrim/practicatrimestralpmoc/market-view.fxml");

    private final String fxmlFile;

    AppView(String fxmlFile) {
        this.fxmlFile = fxmlFile;
    }

    public String getFxmlFile() {
        return fxmlFile;
    }
}