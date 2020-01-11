package personal.wt.ddz.util;

import lombok.Cleanup;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author ttb
 */
public class Util {

    private Util(){}

    public static Dimension getScreenSize(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        return toolkit.getScreenSize();
    }

    public static String getProjectDir(){
        return System.getProperty("user.dir");
    }

    /**
     * 加载类路径下的图片资源
     * @param path
     * @return
     */
    public static Image loadImage(String path) {
        @Cleanup
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
