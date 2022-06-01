package hsgui.widgets;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleToggleGroup<T> extends GridPane {

    private SimpleListProperty<T> options = new SimpleListProperty<>();
    private ArrayList<ToggleButton> toggleButtons = new ArrayList<>();
    private ToggleGroup thisToggleGroup = new ToggleGroup();
    private SimpleIntegerProperty groupBorderRadius = new SimpleIntegerProperty();

    abstract T buttonTextToValue(String text);
    abstract String valueToButtonText(T val);

    public void setGroupBorderRadius(Number radius) {
        groupBorderRadius.set(radius.intValue());
    }

    public Number getGroupBorderRadius() {
        return groupBorderRadius.get();
    }

    public void setOptions(List<T> options) {
        this.options.clear();
        ObservableList<T> observable = FXCollections.observableArrayList(options);
        this.options.set(observable);
    }

    public List<T> getOptions() {
        return options.getValue();
    }

    public void setValue(T value) {
        if (!this.options.contains(value)) {

        } else {
            this.toggleButtons.forEach(btn -> {
                if (buttonTextToValue(btn.getText()) == value) {
                    thisToggleGroup.selectToggle(btn);
                }
            });
        }
    }

    public T getValue() {
        if (thisToggleGroup.getSelectedToggle() == null) {
            return null;
        }
        return buttonTextToValue(((ToggleButton)thisToggleGroup.getSelectedToggle()).getText());
    }

    private void init() {
        setUpLayout();
        setUpBindings();
        setUpStyles();
    }

    private void setUpLayout() {
        this.getChildren().clear();
        this.toggleButtons.clear();

        int row = 0;

        for (T opt : this.options) {
            ToggleButton btn = new ToggleButton(valueToButtonText(opt));
            btn.setToggleGroup(thisToggleGroup);
            this.getChildren().add(btn);
            GridPane.setColumnIndex(btn, 0); GridPane.setRowIndex(btn, row); GridPane.setHgrow(btn, Priority.ALWAYS); row = row + 1; btn.setMaxWidth(Double.POSITIVE_INFINITY);
            this.toggleButtons.add(btn);
        }
    }

    private void setUpBindings() {
        this.options.addListener((x,old,nw) -> {
            this.setUpLayout();
            this.setUpStyles();
        });
        this.groupBorderRadius.addListener((x,old,nw) -> {
            setUpStyles();
        });
    }

    private void setUpStyles() {
        this.toggleButtons.forEach(btn -> {
            btn.getStyleClass().add("item-combo-box");
        });

        int RADIUS = groupBorderRadius.getValue();
        String UPPER_BUTTON_STYLE = new StringBuilder().append("-fx-border-radius: ")
                .append(RADIUS).append(" ")
                .append(RADIUS).append(" 0 0;")
                .append("-fx-background-radius: ")
                .append(RADIUS).append(" ")
                .append(RADIUS).append(" 0 0;").toString();
        String LOWER_BUTTON_STYLE = new StringBuilder().append("-fx-border-radius: ")
                .append("0 0 ").append(RADIUS).append(" ").append(RADIUS).append(";")
                .append("-fx-background-radius: 0 0 ").append(RADIUS).append(" ").append(RADIUS).append(";").toString();
        String MIDDLE_BUTTON_STYLE = "-fx-border-radius: 0 0 0 0;-fx-background-radius: 0 0 0 0;";

        for (int i = 0; i < (toggleButtons.size()); i++) {
            if (i == 0) {
                this.toggleButtons.get(0).setStyle(UPPER_BUTTON_STYLE);
            }
            else if (i == (toggleButtons.size() - 1)) {
                this.toggleButtons.get(this.toggleButtons.size() - 1).setStyle(LOWER_BUTTON_STYLE);
            } else {
                this.toggleButtons.get(i).setStyle(MIDDLE_BUTTON_STYLE);
            }
            GridPane.setHgrow(this.toggleButtons.get(i), Priority.ALWAYS);
        }
    }

    public SimpleToggleGroup() {
        init();
    }

    public void addToggleChangeListener(ChangeListener<T> listener) {
        thisToggleGroup.selectedToggleProperty().addListener((x,old,nw) -> {
            if (old == nw) {return;}
            listener.changed(null,
                    old == null ? null : buttonTextToValue(((ToggleButton)old).getText()),
                    nw == null ? null : buttonTextToValue(((ToggleButton)nw).getText()));
        });
    }
}
