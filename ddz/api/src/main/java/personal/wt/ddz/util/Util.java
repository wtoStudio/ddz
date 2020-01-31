package personal.wt.ddz.util;

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
        ImageIcon imageIcon = loadImageIcon(path);
        if (imageIcon != null) {
            return imageIcon.getImage();
        }else{
            throw new RuntimeException("加载资源文件【" + path + "】失败");
        }
    }

    public static ImageIcon loadImageIcon(String path) {
        InputStream is = Util.class.getResourceAsStream(path);
        if(is == null){
            throw new RuntimeException("资源文件不存在【" + path + "】");
        }
        try {
            BufferedImage bufferedImage = ImageIO.read(is);
            return new ImageIcon(bufferedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
