package hsgui.widgets;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * represents a field that holds a number, and has decrement ('-') and increment ('+') buttons to the left and right
 * of the field
 * it also supports incrementing/decrementing by touch-and-drag, and click-and-drag
 */
public class ConstrainedIntegerField extends HBox {

    static class IntegerInputValidator {
        private int minValue;
        private int maxValue;

        public IntegerInputValidator(int minVal, int maxVal) {
            this.minValue = minVal;
            this.maxValue = maxVal;
        }

        public int digest(int inp) {
            if (inp < minValue) {
                return minValue;
            } else if (inp > maxValue) {
                return maxValue;
            } else {
                return inp;
            }
        }

        public void setMinValue(int val) {
            this.minValue = val;
        }

        public void setMaxValue(int val) {
            this.maxValue = val;
        }
    }

    // controls
    private Button btnDecrement;
    private Button btnIncrement;
    private TextField numberField;

    //
    private IntegerInputValidator inputValidator = new IntegerInputValidator(Integer.MIN_VALUE,
            Integer.MAX_VALUE);

    // properties
    SimpleIntegerProperty minValue = new SimpleIntegerProperty();
    SimpleIntegerProperty maxValue = new SimpleIntegerProperty();
    SimpleIntegerProperty incrementButtonStep = new SimpleIntegerProperty();
    SimpleIntegerProperty touchIncrementSpeed = new SimpleIntegerProperty();
    SimpleIntegerProperty scrollIncrementSpeed = new SimpleIntegerProperty();

    // touch incrementing aid variables
    private double initialX;
    private int initialValue;

    public int getMinValue() {
        return minValue.get();
    }

    public void setMinValue(int minValue) {
        this.minValue.set(minValue);
    }

    public int getMaxValue() {
        return maxValue.get();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue.set(maxValue);
    }

    public int getIncrementButtonStep() {
        return incrementButtonStep.get();
    }

    public void setIncrementButtonStep(int incrementButtonStep) {
        this.incrementButtonStep.set(incrementButtonStep);
    }

    public int getTouchIncrementSpeed() {
        return touchIncrementSpeed.get();
    }

    public void setTouchIncrementSpeed(int touchIncrementSpeed) {
        this.touchIncrementSpeed.set(touchIncrementSpeed);
    }

    public int getScrollIncrementSpeed() {
        return scrollIncrementSpeed.get();
    }

    public Property<String> valueProperty() {return this.numberField.textProperty();}

    public void setScrollIncrementSpeed(int scrollIncrementSpeed) {
        this.scrollIncrementSpeed.set(scrollIncrementSpeed);
    }

    private void sendIncrement(int increm) {
        int newValue = this.getValue() + increm;
        this.setValue(this.inputValidator.digest(newValue));
    }

    private void setUpLayout() {
        this.btnDecrement = new Button("-");
        this.btnIncrement = new Button("+");
        this.numberField = new TextField();

        this.getChildren().add(btnDecrement);
        this.getChildren().add(numberField);
        this.getChildren().add(btnIncrement);
    }

    private void setUpBindings() {

        // property events
        minValue.addListener((x,old,nw) -> {
            this.inputValidator.setMinValue(nw.intValue());
            setInitialValue();
        });

        maxValue.addListener((x,old,nw) -> {
            this.inputValidator.setMaxValue(nw.intValue());
            setInitialValue();
        });

        // controls events
        this.btnIncrement.setOnAction(e -> {
            sendIncrement(incrementButtonStep.get());
        });

        this.btnDecrement.setOnAction(e -> {
            sendIncrement(-incrementButtonStep.get());
        });

        this.numberField.setOnScroll(e -> {
            sendIncrement(((int)e.getDeltaY() > 0 ? 1 : -1)*(scrollIncrementSpeed.get()));
        });

        this.numberField.setOnTouchPressed(touchEvent -> {
            initialX = touchEvent.getTouchPoint().getSceneX();
            initialValue = this.getValue();
        });

        this.numberField.setOnTouchMoved(touchEvent -> {
            this.setValue((int)(touchEvent.getTouchPoint().getSceneX() - initialX) + initialValue);
        });

    }

    private void setUpStyles() {
        this.btnDecrement.getStyleClass().add("increment-button");
        this.btnIncrement.getStyleClass().add("increment-button");
        this.numberField.getStyleClass().add("numerical-field");
        this.numberField.setEditable(false);
        this.numberField.setPrefWidth(48);
    }

    {
        setUpLayout();
        setUpBindings();
        setUpStyles();
        setInitialValue();
    }

    // sets to an arbitrary int value
    public void setValue(int value) {
        this.numberField.setText(String.valueOf(value));
    }

    // returns an integer value of this field
    public Integer getValue() {
        return Integer.parseInt(this.numberField.getText());
    }

    private void setInitialValue() {
        this.setValue(0);
        sendIncrement(0);
    }

}
