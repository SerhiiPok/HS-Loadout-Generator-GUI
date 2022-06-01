package hsgui.widgets;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Level {
    SimpleIntegerProperty integerLevel = new SimpleIntegerProperty();
    SimpleStringProperty css = new SimpleStringProperty();

    public Number getIntegerLevel() {
        return integerLevel.get();
    }

    public void setIntegerLevel(Number n) {
        integerLevel.set(n.intValue());
    }

    public String getCss() {
        return css.getValue();
    }

    public void setCss(String s) {
        css.setValue(s);
    }

    public Level() {
        super();
    }
}
