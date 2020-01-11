package personal.wt.ddz;

import personal.wt.ddz.ui.GamePanel;
import personal.wt.ddz.ui.MainFrame;
import javax.swing.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

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