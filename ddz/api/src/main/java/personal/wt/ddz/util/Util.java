package personal.wt.ddz.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * @author ttb
 */
public class Util {

    private final static String[] names = new String[]{"Kobe Bryant", "勒布朗詹姆斯", "德维恩韦德", "薇恩", "德莱文", "卢锡安", "提莫", "婕拉", "阿利斯塔", "盖伦", "赵信", "奥巴马", "希维尔",
    "凯特琳", "阿卡丽", "卡特琳娜", "福尔摩斯", "格雷福斯", "艾瑞莉娅", "泰达米尔", "王八", "拉莫斯", "雷克顿", "内瑟斯", "卡尔玛", "托儿索",
    "铁男", "德莱厄斯", "斯巴达", "鲁智深", "秦琼", "李青", "杨超越", "臭干子", "牛板筋", "士力架", "威化饼", "虚竹", "太白金星", "弼马温"};

    private static Random random = new Random();

    private Util(){}

    public static String randomName(){
        int i = random.nextInt(names.length);
        return names[i];
    }

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
