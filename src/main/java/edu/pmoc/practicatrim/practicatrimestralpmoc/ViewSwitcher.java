package edu.pmoc.practicatrim.practicatrimestralpmoc;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.util.Objects;

public class ViewSwitcher {

    private static BorderPane mainContentPane;


    public static void setMainContentPane(BorderPane mainContentPane) {
        ViewSwitcher.mainContentPane = mainContentPane;
    }


    public static void switchView(AppView view) {
        if (mainContentPane == null) {
            System.err.println("Error: El panel de contenido principal (mainContentPane) " +
                    "no ha sido inicializado en ViewSwitcher.");
            return;
        }

        try {

            Parent viewRoot = FXMLLoader.load(Objects.requireNonNull(ViewSwitcher.class.getResource(view.getFxmlFile())));


            mainContentPane.setCenter(viewRoot);

        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + view.getFxmlFile());
            e.printStackTrace();
        }
    }
}
