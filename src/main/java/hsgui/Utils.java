package hsgui;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Utils {

    public static void playBackgroundTransition(Region where, Color colorFrom, Color colorTo, double duration) {

        Animation backgroundTransition = new Transition() {

            {
                setCycleDuration(Duration.millis(duration));
                setInterpolator(Interpolator.EASE_OUT);
            }

            @Override
            protected void interpolate(double v) {
                Color currentColor = new Color(colorFrom.getRed() + (colorTo.getRed() - colorFrom.getRed())*v,
                        colorFrom.getGreen() + (colorTo.getGreen() - colorFrom.getGreen())*v,
                        colorFrom.getBlue() + (colorTo.getBlue() - colorFrom.getBlue())*v,
                        colorFrom.getOpacity() + (colorTo.getOpacity() - colorFrom.getOpacity())*v);
                where.setBackground(new Background(new BackgroundFill(currentColor, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        };
        backgroundTransition.play();

    }

}
