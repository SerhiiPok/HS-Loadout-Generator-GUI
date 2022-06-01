package hsgui;

import HuntShowdownLib.InGameTypes.GameItems.IItem;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// temporary imports to test functionality
import HuntShowdownLib.IO.XMLItemReader;
import HuntShowdownLib.UtilityTypes.ItemCollection;

public class App extends Application {

    private static Scene scene;
    public static ItemCollection<IItem> allItems;
    public static MainViewController mainViewController;

    @Override
    public void start(Stage primaryStage) throws Exception{

        XMLItemReader itemReader = XMLItemReader.create(new ResourceLocator("items.xml").getPath());
        try {
            allItems = itemReader.readItems();
        } catch (Exception e) {
            // TODO
        }

        FXMLLoader loader = new FXMLLoader(new ResourceLocator("mainView.fxml").getFile().toURI().toURL());
        Parent main = loader.load();
        mainViewController = loader.getController();

        scene = new Scene(main);
        scene.getStylesheets().add("/styles.css");

        primaryStage.setScene(scene);
        primaryStage.setHeight(795);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
