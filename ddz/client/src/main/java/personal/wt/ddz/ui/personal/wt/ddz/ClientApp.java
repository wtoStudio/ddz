package personal.wt.ddz.ui.personal.wt.ddz;

import personal.wt.ddz.ui.MainFrame;
import javax.swing.*;

/**
 * @author ttb
 */
public class ClientApp {
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        MainFrame app = new MainFrame("关起门来斗地主");
        app.getGamePanel().requestFocus();
    }
}
