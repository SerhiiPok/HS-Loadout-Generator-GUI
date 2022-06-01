module hs.loadoutgen.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires hs.gameitemsapi;
    requires java.net.http;
    requires org.controlsfx.controls;

    exports hsgui to javafx.graphics, javafx.fxml;
    exports hsgui.widgets to javafx.fxml;
    exports hsgui.widgets.filters to javafx.fxml;

    opens hsgui.widgets to javafx.fxml;
    opens hsgui to javafx.fxml;
    opens hsgui.widgets.filters to javafx.fxml;
}