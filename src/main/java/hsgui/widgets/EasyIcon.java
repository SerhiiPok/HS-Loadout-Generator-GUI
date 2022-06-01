package hsgui.widgets;

import hsgui.ResourceLocator;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

/**
 * an image that can be instantiated in fxml using path to image resource only
 */
public class EasyIcon extends ImageView {

    Image myImage;

    Property<String> path = new SimpleStringProperty();
    Property<String> color = new SimpleStringProperty();
    Property<Number> noise = new SimpleDoubleProperty(0.0);

    public void setNoise(Number val) {
        this.noise.setValue(val);
    }

    public Number getNoise() {
        return this.noise.getValue();
    }

    public void setPath(String url) {
        path.setValue(url);
    }

    public String getPath() {
        return path.getValue();
    }

    public void setColor(String hex) {
        color.setValue(hex);
    }

    public String getColor() {
        return color.getValue();
    }

    {
        path.addListener((obs,old,nw) -> {
            try {
                myImage = new Image(new FileInputStream(new ResourceLocator(nw).getFile()));
            } catch (Exception e) {

            }
            this.setImage(myImage);
        });

        color.addListener((obs,old,nw) -> {
            applyColor();
        });

        noise.addListener((obs,old,nw) -> {
            applyColor();
        });

    }

    public Color noise(Color inputColor, Number scale) {
        double scaledRandomMultiplication = Math.pow(0.5 + new Random().nextDouble(), scale.doubleValue());
        double newBrightness = inputColor.getBrightness() * scaledRandomMultiplication > 1.0 ? 1.0 : inputColor.getBrightness() * scaledRandomMultiplication;
        Color hsbColor = Color.hsb(inputColor.getHue(), inputColor.getSaturation(), newBrightness);
        return new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), inputColor.getOpacity());
    }

    public void applyColor() {
        Blend colorOverlay = new Blend(BlendMode.OVERLAY);

        // might not be safe...
        int pixelWidth = (int) myImage.getWidth();
        int pixelHeight = (int) myImage.getHeight();

        Color newColor = Color.web(color.getValue());
        Number noise_scale = noise.getValue() == null ? 0.0 : noise.getValue();

        WritableImage nw = new WritableImage(pixelWidth, pixelHeight);

        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                Color nwColor = new Color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), myImage.getPixelReader().getColor(i,j).getOpacity());
                nw.getPixelWriter().setColor(i, j, noise(nwColor, noise_scale));

            }
        }

        this.setImage(nw);
    }

}
