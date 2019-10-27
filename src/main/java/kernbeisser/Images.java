package kernbeisser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Easy Image Manager which loads all Images from a selected path and all under Dictionaries
 */
public class Images {
    /**
     * the container for all loaded Images and keys
     *
     * @see HashMap
     */
    private static HashMap<String, BufferedImage> images = new HashMap<>();

    /**
     * sets the image source path
     *
     * @param f the file(Dictionary) which holds all the image sources
     */
    public static void setPath(File f) {
        if (!f.exists()) System.out.println("path does not exsits");
        images.clear();
        collectImages(f);
    }

    /**
     * search for a Image which has been loaded before
     *
     * @param name the Image name with extension
     * @return return the right image with the given name
     */
    public static BufferedImage getImage(String name) {
        if (images.containsKey(name))
            return images.get(name);
        System.err.println("Image not found");
        return null;
    }

    /**
     * adds all images from a Dictionary to the images HashMap
     *
     * @param dir the Dictionary with the images inside
     */
    private static void collectImages(File dir) {
        if (!dir.isDirectory()) return;
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) collectImages(file);
            else {
                try {
                    images.put(file.getName(), ImageIO.read(file));
                    System.out.println(file.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
