package kernbeisser.Useful;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import kernbeisser.Config.Config;
import kernbeisser.Main;

/** Easy Image Manager which loads all Images from a selected path and all under Dictionaries */
public class Images {
  /**
   * the container for all loaded Images and keys
   *
   * @see HashMap
   */
  private static final HashMap<String, BufferedImage> images = new HashMap<>();

  static {
    collectImages(Config.getConfig().getImagePath());
  }

  /**
   * sets the image source path
   *
   * @param f the file(Dictionary) which holds all the image sources
   */
  public static void setPath(File f) {
    if (!f.exists()) {
      Main.logger.error("path does not exists");
    }
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
    if (images.containsKey(name)) {
      return images.get(name);
    }
    Main.logger.warn("requested Image '" + name + "' not found");
    return null;
  }

  /**
   * adds all images from a Dictionary to the images HashMap
   *
   * @param dir the Dictionary with the images inside
   */
  private static void collectImages(File dir) {
    if (dir == null) return;
    if (!dir.isDirectory()) {
      Main.logger.warn("invalid image dir found in config.json");
      return;
    }
    int img = 0;
    for (File file : dir.listFiles()) {
      if (file.isDirectory()) {
        collectImages(file);
      } else {
        try {
          images.put(file.getName(), ImageIO.read(file));
          img++;
        } catch (IOException e) {
          Tools.showUnexpectedErrorWarning(e);
        }
      }
    }
    if (img != 0) {
      Main.logger.info("loaded " + img + " images from " + dir.getAbsolutePath());
    }
  }
}
