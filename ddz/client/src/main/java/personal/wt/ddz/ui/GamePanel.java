package personal.wt.ddz.ui;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.github.javafaker.Faker;
import lombok.Getter;
import lombok.Setter;
import personal.wt.ddz.core.GameManager;
import personal.wt.ddz.entity.Card;
import personal.wt.ddz.entity.Message;
import personal.wt.ddz.entity.User;
import personal.wt.ddz.enums.MessageType;
import personal.wt.ddz.enums.Side;
import personal.wt.ddz.enums.UserStatus;
import personal.wt.ddz.service.GameService;
import personal.wt.ddz.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import static personal.wt.ddz.config.DrawConfig.*;

/**
 * @author ttb
 */
@Setter
@Getter
public class GamePanel extends JPanel {

    private GameService gameService = new GameService();

    private GameManager gameManager = GameManager.getInstance();

    private static GamePanel gamePanel = new GamePanel();

    private Image bg;

    private Faker faker;

    private User prevUser;// = new User("盖伦", Util.loadImage("/images/headers/farmer.png"), Side.PREV);

    private User localUser;

    private User nextUser;// = new User("雷克顿", Util.loadImage("/images/headers/farmer.png"), Side.NEXT);

    /**
     * 存放底牌
     */
    private List<Card> hiddenCardList = new ArrayList<>(3);

    /**
     * 【重新发牌】按钮
     */
    private JButton redealCardBtn = new JButton("重新发牌");

    /**
     * 【准备】按钮
     */
    private JButton readyBtn = new JButton("准备");

    private GamePanel(){
        //gameManager.dealCard(prevUser, localUser, nextUser, hiddenCardList);
        //faker = new Faker(Locale.CHINA);
        this.localUser = new User(Util.randomName());
        String clientIp = gameService.getClientIp();
        int clientPort = gameService.getClientPort();
        this.localUser.setIp(clientIp);
        this.localUser.setPort(clientPort);
        Message message = Message.builder()
                .type(MessageType.JOIN)
                .content(JSONObject.toJSONString(this.localUser))
                .build();
        gameService.sendMsg(message);

        this.setLayout(null);
        this.bg = GameManager.imageMap.get("gameBg");
        this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                List<Card> cardList = GamePanel.this.localUser.getCardList();
                super.mouseClicked(e);
                int button = e.getButton();//1: 鼠标左键  3：鼠标右键
                if(button == MouseEvent.BUTTON1){ //选派（反选牌）
                    int index = getCardIndex(e.getPoint());
                    if(index > -1){
                        //调整计算误差
                        if(index == cardList.size()){
                            index = cardList.size() - 1;
                        }
                        boolean selected = cardList.get(index).isSelected();
                        cardList.get(index).setSelected(!selected);
                    }
                }else if(button == MouseEvent.BUTTON3){ //出牌
                    List<Card> selectedCards = cardList.stream().filter(Card::isSelected).collect(Collectors.toList());
                    selectedCards.forEach(card -> card.setSelected(false));
                    GamePanel.this.localUser.getPlayedCardList().clear();
                    GamePanel.this.localUser.getPlayedCardList().addAll(selectedCards);
                    GamePanel.this.localUser.getCardList().removeAll(selectedCards);
                }
                GamePanel.this.repaint();
            }
        });

        readyBtn.setFocusPainted(false);
        readyBtn.setBounds((GAME_WIDTH - 120)/2, LOCAL_CARD_START_POS_Y - 30 - 10, 120, 30);
        this.add(readyBtn);
        readyBtn.addActionListener(e -> {
            JButton btn = (JButton) e.getSource();
            Message msg = Message.builder()
                    .content(gamePanel.localUser.getId())
                    .build();
            if("准备".equals(btn.getText())){
                msg.setType(MessageType.READY);
            }else if("取消准备".equals(btn.getText())){
                msg.setType(MessageType.UNREADY);
            }
            gameService.sendMsg(msg);
        });

        redealCardBtn.setFocusPainted(false);
        this.add(redealCardBtn);
        //重新发牌
        redealCardBtn.addActionListener(e -> {
            GamePanel.this.localUser.getCardList().clear();
            GamePanel.this.prevUser.getCardList().clear();
            GamePanel.this.nextUser.getCardList().clear();
            GamePanel.this.hiddenCardList.clear();
            gameManager.dealCard(prevUser, localUser, nextUser, hiddenCardList);
            GamePanel.this.repaint();
        });
    }

    /**
     * 沿水平方向绘制一组牌时，计算开始位置的X坐标
     * @param count 牌的张数
     * @param cap 相邻两张牌的间距
     * @return
     */
    private int calStartX(int count, int cap){
        return (GAME_WIDTH - ((count - 1) * cap + CARD_WIDTH)) / 2;
    }

    /**
     * 根据鼠标点击位置，返回指定的牌的index
     */
    private int getCardIndex(Point point){
        List<Card> cardList = this.localUser.getCardList();
        if(cardList.isEmpty()){
            return -1;
        }
        int myCardStartPosX = calStartX(cardList.size(), CARD_WIDTH/2);
        int clickedX = point.x;
        int clickedY = point.y;
        if(clickedX > myCardStartPosX &&
            clickedX < (myCardStartPosX + (cardList.size()-1)*LOCAL_CARD_CAP+CARD_WIDTH)){
            if(clickedY > LOCAL_CARD_START_POS_Y && clickedY<LOCAL_CARD_START_POS_Y +CARD_HEIGHT){
                int index = (clickedX - myCardStartPosX) / LOCAL_CARD_CAP;
                return index;
            }
        }
        return -1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //绘制背景图
        paintBackground(g);
        //绘制底牌方框
        paintHiddenCardRect(g);
        //绘制底牌
        paintCards(this.hiddenCardList, HIDDEN_CARD_CAP, HIDDEN_CARD_START_Y, g);
        //绘制玩家名字和头像
        paintUserInfo(g);
    }

    /**
     * 绘制底牌方框
     * @param g
     */
    public void paintHiddenCardRect(Graphics g){
        g.setColor(Color.WHITE);
        int rectWidth = 3 * CARD_WIDTH + 2 * HIDDEN_CARD_CAP + 2 * 10;
        int rectHeight = CARD_HEIGHT + 2 * 10;
        int startX = (GAME_WIDTH - rectWidth) / 2;
        int startY = HIDDEN_CARD_START_Y - 10;
        g.drawRect(startX, startY, rectWidth, rectHeight);
    }

    /**
     * 绘制背景
     */
    private void paintBackground(Graphics g){
        g.drawImage(this.bg, 0, 0, GAME_WIDTH, GAME_HEIGHT, 0, 0, bg.getWidth(null), bg.getHeight(null), null);
    }

    /**
     * 绘制玩家信息
     */
    public void paintUserInfo(Graphics g){
        //头像尺寸为 48x48
        int headerSize = 48;
        g.setColor(Color.WHITE);
        //绘制本家信息
        if(this.localUser != null){
            Image headerImage = Util.loadImage("/images/headers/dz.png");
            g.drawString(localUser.getName(), SIDE_CAP + CARD_WIDTH + 160, GAME_HEIGHT - 30);
            g.drawImage(headerImage, SIDE_CAP + CARD_WIDTH + 160, GAME_HEIGHT - 30 - 70, SIDE_CAP + CARD_WIDTH + 160 + headerSize, GAME_HEIGHT - 30 - 70 + headerSize, 0, 0, headerImage.getWidth(null), headerImage.getHeight(null), null);

            if(this.localUser.getStatus() == UserStatus.READY){
                g.drawString("准备就绪", GAME_WIDTH / 2, GAME_HEIGHT - 50);
            }else if(this.localUser.getStatus() == UserStatus.PLAYING){
                //绘制本家的牌
                paintCards(this.localUser.getCardList(), LOCAL_CARD_CAP, LOCAL_CARD_START_POS_Y, g);
                //绘制本家已打出的牌
                paintCards(this.localUser.getPlayedCardList(), LOCAL_PLAYED_CARD_CAP, LOCAL_PLAYED_CARD_START_Y, g);
            }
        }

        //绘制上家信息
        if(this.prevUser != null){
            Image headerImage = Util.loadImage("/images/headers/farmer.png");
            g.drawString(prevUser.getName(), SIDE_CAP + CARD_WIDTH + 100, 100);
            g.drawImage(headerImage, SIDE_CAP + CARD_WIDTH + 100, 100 - 70, SIDE_CAP + CARD_WIDTH + 100 + headerSize, 100 - 70 + headerSize, 0, 0, headerImage.getWidth(null), headerImage.getHeight(null), null);
            if(this.prevUser.getStatus() == UserStatus.READY){
                g.drawString("准备就绪", PREV_SIDE_START_X, SIDE_START_Y);
            }else if(this.prevUser.getStatus() == UserStatus.PLAYING){
                //绘制上家的牌
                paintSideCards(this.prevUser.getCardList(), SIDE_CARD_CAP, Side.PREV, g);
                //绘制上家打出的牌
                paintSidePlayedCards(this.prevUser.getPlayedCardList(), LOCAL_PLAYED_CARD_CAP, Side.PREV, g);
            }
        }else{
            g.drawString("等待加入", PREV_SIDE_START_X, SIDE_START_Y);
        }

        //绘制下家信息
        if(this.nextUser != null){
            Image headerImage = Util.loadImage("/images/headers/farmer.png");
            g.drawString(nextUser.getName(), GAME_WIDTH - (SIDE_CAP + CARD_WIDTH + 100), 100);
            g.drawImage(headerImage, GAME_WIDTH - (SIDE_CAP + CARD_WIDTH + 100), 100 - 70, GAME_WIDTH - (SIDE_CAP + CARD_WIDTH + 100) + headerSize, 100 - 70 + headerSize, 0, 0, headerImage.getWidth(null), headerImage.getHeight(null), null);
            if(this.nextUser.getStatus() == UserStatus.READY){
                g.drawString("准备就绪", NEXT_SIDE_START_X, SIDE_START_Y);
            }else if(this.nextUser.getStatus() == UserStatus.PLAYING){
                //绘制下家的牌
                paintSideCards(this.nextUser.getCardList(), SIDE_CARD_CAP, Side.NEXT, g);
                //绘制下家打出的牌
                paintSidePlayedCards(this.nextUser.getPlayedCardList(), LOCAL_PLAYED_CARD_CAP, Side.NEXT, g);
            }
        }else{
            g.drawString("等待加入", NEXT_SIDE_START_X, SIDE_START_Y);
        }
    }

    /**
     * 绘制上家和下家的牌
     * @param cardList 牌集合
     * @param cap 每两张牌之间的间隔
     * @param side @see Side Side.PREV: 上家  Side.NEXT: 下家
     * @param g Graphics对象
     */
    private void paintSideCards(List<Card> cardList, int cap, Side side, Graphics g){
        int startX = 0;
        int startY = SIDE_START_Y;
        if(side == Side.PREV){
            startX = PREV_SIDE_START_X;
        }else if(side == Side.NEXT){
            startX = NEXT_SIDE_START_X;
        }
        for(int i=0; i<cardList.size(); i++){
            Card card = cardList.get(i);
            Image cardImage = GameManager.imageMap.get(card.imageKey());
            g.drawImage(cardImage, startX, startY + i * cap, startX + CARD_WIDTH, startY + i * cap + CARD_HEIGHT, 0, 0, cardImage.getWidth(null), cardImage.getHeight(null), null);
        }
    }

    /**
     * 绘制上家或下家已打出的牌
     * @param cardList
     * @param cap
     * @param side
     * @param g
     */
    private void paintSidePlayedCards(List<Card> cardList, int cap, Side side, Graphics g){
        int startX = 0;
        if(side == Side.PREV){
            startX = SIDE_PLAYED_CARD_CAP;
        }if(side == Side.NEXT){
            startX = GAME_WIDTH - (SIDE_PLAYED_CARD_CAP + (cardList.size() - 1) * cap + CARD_WIDTH);
        }
        for(int i=0; i<cardList.size(); i++){
            Card card = cardList.get(i);
            Image cardImage = GameManager.imageMap.get(card.imageKey());
            g.drawImage(cardImage, startX + (i * cap), SIDE_PLAYED_CARD_START_Y, startX + (i * cap) + CARD_WIDTH, SIDE_PLAYED_CARD_START_Y + CARD_HEIGHT, 0, 0, cardImage.getWidth(null), cardImage.getHeight(null), null);
        }
    }

    /**
     * 沿水平方向绘制一组牌
     * @param cardList 要绘制的一组牌的集合
     * @param cap 相邻两张牌之间的间距
     * @param startY 开始位置y坐标
     * @param g Graphics对象
     */
    private void paintCards(List<Card> cardList, int cap, int startY, Graphics g){
        int startX = calStartX(cardList.size(), cap);
        for(int i=0; i<cardList.size(); i++){
            Card card = cardList.get(i);
            boolean selected = card.isSelected();
            Image cardImage = GameManager.imageMap.get(card.imageKey());
            if(selected){
                g.drawImage(cardImage, startX + (i * cap), startY - 30, startX + (i * cap) + CARD_WIDTH, startY - 30 + CARD_HEIGHT, 0, 0, cardImage.getWidth(null), cardImage.getHeight(null), null);
            }else{
                g.drawImage(cardImage, startX + (i * cap), startY, startX + (i * cap) + CARD_WIDTH, startY + CARD_HEIGHT, 0, 0, cardImage.getWidth(null), cardImage.getHeight(null), null);
            }
        }
    }

    /**
     * 判断牌型:
     * 判断玩家打出的牌是单张，对子，三带一，三带二，顺子等等情况
     */
    private void judgeCardType(List<Card> cards){
        int count = cards.size();
        if(count == 1){
            //单张

        }else if(count == 2){
            //对子或王炸qazxcswAZWa

        }else if(count == 3){
            //三不带

        }else if(count == 4){
            //炸弹 或 三带一

        }else if(count == 5){
            //顺子 或者三带二

        }else if(count == 6){
            //顺子 或者 连队 或者 四带二 或者 飞机

        }
    }

    public static GamePanel getInstance(){
        return gamePanel;
    }
}
