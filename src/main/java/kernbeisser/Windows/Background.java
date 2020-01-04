package kernbeisser.Windows;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Background extends JPanel {
    private Container container;
    private BufferedImage image;
    public Background(BufferedImage image){
        this(image,null);
    }
    public Background(BufferedImage image, Container container){
        this.container=container;
        this.image=image;
        setBounds(0,0,image.getWidth(),image.getHeight());
    }
    public void paint(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        if(container!=null) {
            float proportion = (float) image.getHeight()/(float) image.getWidth();
            float size = Math.max(container.getWidth(),container.getHeight()/proportion);
            setBounds(0, 0, (int)size,(int)(size*proportion));
        }
        graphics2D.drawImage(image,0,0,getWidth(),getHeight(),null);
    }
    public BufferedImage getImage() {
        return image;
    }
    public void autoSize(Container container){
        this.container=container;
    }
}
