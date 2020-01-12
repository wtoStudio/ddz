package personal.wt.ddz;

import com.github.javafaker.Faker;
import personal.wt.ddz.ui.GamePanel;
import personal.wt.ddz.ui.MainFrame;
import javax.swing.*;
import java.util.Locale;

/**
 * @author ttb
 */
public class AppClient {
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        MainFrame mainFrame = new MainFrame("斗地主");
        GamePanel gamePanel = mainFrame.getGamePanel();
        gamePanel.requestFocus();
    }
}