package personal.wt.ddz.ui;

import lombok.Getter;
import lombok.Setter;
import javax.swing.*;
import java.awt.*;

/**
 * @author ttb
 */
@Setter
@Getter
public class MainFrame extends JFrame {

    private GamePanel gamePanel = new GamePanel();

    public MainFrame(String title){
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(gamePanel);
        this.pack();
        this.setVisible(true);
        this.setResizable(true);
    }
}
