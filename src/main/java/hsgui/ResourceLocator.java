package hsgui;

import java.io.File;
import java.util.List;

public class ResourceLocator {

    private String path;
    private final List<String> possibleLocations = List.of(
            "app/",
            "src/main/resources/"
    );

    public ResourceLocator(String path) {
        if (path.startsWith("/")) {
            this.path = path.substring(1);
        } else {
            this.path = path;
        }
    }

    public String getPath() {
        File f = getFile();
        return f == null ? null : f.getPath();
    }

    public File getFile() {
        for (String root : possibleLocations) {
            File f = new File(root + this.path);
            if (f.exists() && !f.isDirectory()) {
                return f;
            }
        }
        return null;
    }

    public String toString() {
        return getPath();
    }

}
