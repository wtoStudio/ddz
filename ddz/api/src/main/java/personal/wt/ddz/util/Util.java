package personal.wt.ddz.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Util {
    public static Dimension getScreenSize(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        return toolkit.getScreenSize();
    }

    public static String getProjectDir(){
        return System.getProperty("user.dir");
    }

    public static Image getImage(String path) {
        InputStream is = Util.class.getResourceAsStream(path);
        if(is == null){
            throw new RuntimeException("资源文件不存在【" + path + "】");
        }
        try {
            BufferedImage bufferedImage = ImageIO.read(is);
            Image image = new ImageIcon(bufferedImage).getImage();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
