package hsgui.widgets;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;

import java.util.List;

public class SpecBox extends Label {

    SimpleListProperty<Level> conditionalFormatting = new SimpleListProperty<>();

    public void setConditionalFormatting(List<Level> conditionalFormatting) {
        this.conditionalFormatting.set(FXCollections.observableList(conditionalFormatting));
    }

    public List<Level> getConditionalFormatting() {
        return conditionalFormatting.get();
    }

    public Integer getValue() {
        try {
            return Integer.parseInt(this.getText());
        } catch (Exception e) {
            return null;
        }
    }

    public void setValue(Integer value) {
        this.setText(value.toString());
    }

    {
        this.textProperty().addListener((x,old,nw) -> {
            Integer intVal = this.getValue();
            if (intVal == null) {return;}

            for (Level level : this.conditionalFormatting) {
                if (intVal <= level.getIntegerLevel().intValue()) {
                    this.setStyle(level.getCss());
                    return;
                }
            }
        });
    }

}
