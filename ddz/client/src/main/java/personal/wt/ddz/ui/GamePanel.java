package personal.wt.ddz.ui;

import org.springframework.beans.BeanUtils;
import personal.wt.ddz.core.GameManager;
import personal.wt.ddz.entity.Card;
import personal.wt.ddz.entity.User;
import personal.wt.ddz.enums.PictureType;
import personal.wt.ddz.enums.Side;
import personal.wt.ddz.enums.UserPosition;
import personal.wt.ddz.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ttb
 */
public class GamePanel extends JPanel {

    private GameManager gameManager = GameManager.getInstance();

    /**
     * 游戏画面宽度
     */
    private int width;

    /**
     * 游戏画面高度
     */
    private int height;

    /**
     * 游戏画面背景图
     */
    private Image bg;

    /**
     * 牌的宽度
     */
    private int cardWidth;

    /**
     * 牌的高度
     */
    private int cardHeight;

    /**
     * 容纳所有54张扑克牌的集合
     */
    private List<Card> allCardList = new ArrayList<>();

    /**
     * 本家的牌开始显示位置X坐标
     */
    private int myCardStartPosX;

    /**
     * 本家的牌开始显示位置Y坐标
     */
    private int myCardStartPosY;

    /**
     * 存放本家的牌
     */
    private List<Card> myCardList = new ArrayList<>();

    /**
     * 本家的牌的间隔宽度
     */
    private int myCardCap;

    /**
     * 本家打出的牌的间隔宽度
     */
    private int myPlayedCardCap;

    /**
     * 本家打出的牌开始显示位置Y坐标
     */
    private int myPlayedCardStartY;

    /**
     * 存放本家当前打出的牌
     */
    /*private List<Card> myPlayedCardList = new ArrayList<>();

    *//**
     * 存放上家的牌
     *//*
    private List<Card> prevCardList = new ArrayList<>();

    *//**
     * 存放下家的牌
     *//*
    private List<Card> nextCardList = new ArrayList<>();*/

    private User prevUser = new User("盖伦", UserPosition.PREV);

    private User localUser = new User("赵信", UserPosition.LOCAL);

    private User nextUser = new User("雷克顿", UserPosition.NEXT);

    /**
     * 存放底牌
     */
    private List<Card> hiddenCardList = new ArrayList<>();

    /**
     * 底牌开始显示位置X坐标
     */
    private int hiddenCardCap;

    /**
     * 底牌开始显示位置Y坐标
     */
    private int hiddenCardStartY;

    //------------------------上下家牌位置参数START-------------------------

    /**
     * 牌距离两侧的宽度
     */
    private int sideCap;

    /**
     * 左侧开始位置X坐标
     */
    private int leftSideStartX;

    /**
     * 右侧开始位置X坐标
     */
    private int rightSideStartX;

    /**
     * 开始位置Y坐标
     */
    private int sideStartY;

    /**
     * 相邻两张牌的间距
     */
    private int sideCardCap;

    //------------------------上下家牌位置参数END-------------------------

    private JButton redealCardBtn = new JButton("重新发牌");

    public GamePanel(){
        gameManager.dealCard(prevUser, localUser, nextUser, hiddenCardList);
        initSize();
        this.bg = Util.getImage("/images/background/bg2.jpg");
        this.setPreferredSize(new Dimension(this.width, this.height));
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
                    //GamePanel.this.localUser.getCardList().clear();
                    GamePanel.this.localUser.getPlayedCardList().addAll(selectedCards);
                    GamePanel.this.localUser.getCardList().removeAll(selectedCards);
                }
                GamePanel.this.repaint();
            }
        });

        this.add(redealCardBtn);
        redealCardBtn.setFocusPainted(false);
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
     * 初始化各种尺寸数值
     */
    private void initSize(){
        this.width = (int) (Util.getScreenSize().width * 0.8);
        this.height = (int) (this.width * 0.55);

        this.cardWidth = this.width / 25;
        this.cardHeight = (int) (this.cardWidth * 1.5);

        this.hiddenCardCap = this.cardWidth + 30;
        this.myCardCap = this.cardWidth / 2;
        this.myPlayedCardCap = this.cardWidth / 2;

        this.myCardStartPosY = this.height - (this.cardHeight + 30);
        this.myPlayedCardStartY = this.myCardStartPosY - (this.cardHeight + 30);
        this.hiddenCardStartY = 80;

        //----上下家----
        this.sideCap = 50;
        this.leftSideStartX = this.sideCap;
        this.rightSideStartX = this.width - (this.cardWidth + this.sideCap);
        this.sideStartY = 80;
        this.sideCardCap = this.cardHeight * 2 / 5;
    }

    private int calStartX(int count, int cap){
        return (this.width - ((count - 1) * cap + this.cardWidth)) / 2;
    }



    /**
     * 根据鼠标点击位置，返回指定的牌的index
     */
    private int getCardIndex(Point point){
        List<Card> cardList = this.localUser.getCardList();
        if(cardList.isEmpty()){
            return -1;
        }
        this.myCardStartPosX = calStartX(cardList.size(), this.cardWidth/2);
        int clickedX = point.x;
        int clickedY = point.y;
        if(clickedX > this.myCardStartPosX &&
            clickedX < (this.myCardStartPosX + (cardList.size()-1)*this.myCardCap+this.cardWidth)){
            if(clickedY > this.myCardStartPosY && clickedY<this.myCardStartPosY +this.cardHeight){
                int index = (clickedX - this.myCardStartPosX) / this.myCardCap;
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
        //绘制底牌
        paintCards(this.hiddenCardList, this.hiddenCardCap, this.hiddenCardStartY, g);
        //绘制本家的牌
        paintCards(this.localUser.getCardList(), this.myCardCap, this.myCardStartPosY, g);
        //绘制本家已打出的牌
        paintCards(this.localUser.getPlayedCardList(), this.myPlayedCardCap, this.myPlayedCardStartY, g);

        //绘制上家的牌
        paintSideCards(this.prevUser.getCardList(), this.sideCardCap, Side.PREV, g);
        //绘制下家的牌
        paintSideCards(this.nextUser.getCardList(), this.sideCardCap, Side.NEXT, g);
    }

    /**
     * 绘制背景
     */
    private void paintBackground(Graphics g){
        g.drawImage(bg, 0, 0, this.width, this.height, 0, 0, bg.getWidth(null), bg.getHeight(null), null);
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
        int startY = this.sideStartY;
        if(side == Side.PREV){
            startX = this.leftSideStartX;
        }else if(side == Side.NEXT){
            startX = this.rightSideStartX;
        }
        for(int i=0; i<cardList.size(); i++){
            Card card = cardList.get(i);
            Image cardImage = card.getImage();
            g.drawImage(cardImage, startX, startY + i * cap, startX + this.cardWidth, startY + i * cap + this.cardHeight, 0, 0, cardImage.getWidth(null), cardImage.getHeight(null), null);
        }
    }

    /**
     * 绘制一组牌
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
            Image cardImage = card.getImage();
            if(selected){
                g.drawImage(cardImage, startX + (i * cap), startY - 30, startX + (i * cap) + this.cardWidth, startY - 30 + this.cardHeight, 0, 0, cardImage.getWidth(null), cardImage.getHeight(null), null);
            }else{
                g.drawImage(cardImage, startX + (i * cap), startY, startX + (i * cap) + this.cardWidth, startY + this.cardHeight, 0, 0, cardImage.getWidth(null), cardImage.getHeight(null), null);
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
}
