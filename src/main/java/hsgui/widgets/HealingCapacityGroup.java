package hsgui.widgets;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;

/**
 * represents a toggle group of buttons
 */
public class HealingCapacityGroup extends SimpleToggleGroup<Number> {

    private SimpleListProperty<Integer> options = new SimpleListProperty<>();
    private ArrayList<ToggleButton> toggleButtons = new ArrayList<>();
    private ToggleGroup thisToggleGroup = new ToggleGroup();
    private SimpleIntegerProperty groupBorderRadius = new SimpleIntegerProperty();

    @Override
    Number buttonTextToValue(String text) {
        return Integer.parseInt(text);
    }

    @Override
    String valueToButtonText(Number val) {
        return val.toString();
    }

    public HealingCapacityGroup() {
        super();
    }
}
