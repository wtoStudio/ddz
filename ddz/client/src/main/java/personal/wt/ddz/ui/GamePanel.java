package personal.wt.ddz.ui;

import org.springframework.beans.BeanUtils;
import personal.wt.ddz.entity.Card;
import personal.wt.ddz.enums.PictureType;
import personal.wt.ddz.enums.Side;
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
    private List<Card> myPlayedCardList = new ArrayList<>();

    /**
     * 存放上家的牌
     */
    private List<Card> prevCardList = new ArrayList<>();

    /**
     * 存放下家的牌
     */
    private List<Card> nextCardList = new ArrayList<>();

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

    private Map<String, Image> imageMap = new HashMap<>();

    private Map<String, Integer> charValueMap = new HashMap<>();

    private JButton redealCardBtn = new JButton("重新发牌");

    {
        charValueMap.put("A", 14);charValueMap.put("K", 13);charValueMap.put("Q", 12);charValueMap.put("J", 11);
        charValueMap.put("10", 10);charValueMap.put("9", 9);charValueMap.put("8", 8);charValueMap.put("7", 7);
        charValueMap.put("6", 6);charValueMap.put("5", 5);charValueMap.put("4", 4);charValueMap.put("3", 3);
        charValueMap.put("2", 15);
    }

    public GamePanel(){
        initImageMap();
        initCards();
        dealCard();
        initSize();
        this.bg = Util.getImage("/images/background/bg2.jpg");
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int button = e.getButton();//1: 鼠标左键  3：鼠标右键
                if(button == MouseEvent.BUTTON1){ //选派（反选牌）
                    int index = getCardIndex(e.getPoint());
                    if(index > -1){
                        //调整计算误差
                        if(index == GamePanel.this.myCardList.size()){
                            index = GamePanel.this.myCardList.size() - 1;
                        }
                        boolean selected = GamePanel.this.myCardList.get(index).isSelected();
                        GamePanel.this.myCardList.get(index).setSelected(!selected);
                    }
                }else if(button == MouseEvent.BUTTON3){ //出牌
                    List<Card> selectedCards = GamePanel.this.myCardList.stream().filter(Card::isSelected).collect(Collectors.toList());
                    selectedCards.forEach(card -> card.setSelected(false));
                    GamePanel.this.myPlayedCardList.clear();
                    GamePanel.this.myPlayedCardList.addAll(selectedCards);
                    GamePanel.this.myCardList.removeAll(selectedCards);
                }
                GamePanel.this.repaint();
            }
        });

        this.add(redealCardBtn);
        redealCardBtn.setFocusPainted(false);
        redealCardBtn.addActionListener(e -> {
            GamePanel.this.myCardList.clear();
            GamePanel.this.prevCardList.clear();
            GamePanel.this.nextCardList.clear();
            GamePanel.this.hiddenCardList.clear();
            dealCard();
            GamePanel.this.repaint();
        });
    }

    /**
     * 初始各种尺寸数值
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

    private void initImageMap(){
        PictureType[] pictureTypes = PictureType.values();
        String[] values = new String[]{"A", "K", "Q", "J", "10", "9", "8", "7", "6", "5", "4", "3", "2"};
        for(PictureType pictureType : pictureTypes){
            if(pictureType.getCode().startsWith("JOKER")){
                continue;
            }
            for(String v : values){
                String imageName = pictureType.getCode() + v;
                this.imageMap.put(imageName, Util.getImage("/images/card/" + imageName + ".png"));
            }
        }
        this.imageMap.put("JOKER0", Util.getImage("/images/card/JOKER0.png"));
        this.imageMap.put("JOKER1", Util.getImage("/images/card/JOKER1.png"));
    }

    /**
     * 初始化一副牌
     */
    private void initCards(){
        PictureType[] pictureTypes = PictureType.values();
        String[] values = new String[]{"A", "K", "Q", "J", "10", "9", "8", "7", "6", "5", "4", "3", "2"};
        for(PictureType pictureType : pictureTypes){
            if(pictureType.getCode().startsWith("JOKER")){
                continue;
            }
            for(String v : values){
                Card card = new Card(pictureType, v);
                card.setImage(imageMap.get(pictureType.getCode() + v));
                card.setSortValue(charValueMap.get(v));
                allCardList.add(card);
            }
        }
        Card joker0 = new Card(PictureType.JOKER0, "0");
        joker0.setImage(imageMap.get("JOKER0"));
        joker0.setSortValue(9000001);
        Card joker1 = new Card(PictureType.JOKER1, "1");
        joker1.setImage(imageMap.get("JOKER1"));
        joker1.setSortValue(9000002);
        allCardList.add(joker0);
        allCardList.add(joker1);
    }

    /**
     * 发牌
     */
    private void dealCard(){
        Random random = new Random();
        List<Card> tempList = new ArrayList<>();
        this.allCardList.forEach(c -> {
            Card card = new Card();
            BeanUtils.copyProperties(c, card);
            tempList.add(card);
        });
        Set<Integer> set1 = new HashSet<>();
        Set<Integer> set2 = new HashSet<>();
        Set<Integer> set3 = new HashSet<>();
        while(set1.size() < 17){
            int index = random.nextInt(tempList.size());
            if(set1.add(index)){
                this.myCardList.add(tempList.get(index));
                tempList.remove(index);
            }
        }
        while(set2.size() < 17){
            int index = random.nextInt(tempList.size());
            if(set2.add(index)){
                this.prevCardList.add(tempList.get(index));
                tempList.remove(index);
            }
        }
        while(set3.size() < 3){
            int index = random.nextInt(tempList.size());
            if(set3.add(index)){
                this.hiddenCardList.add(tempList.get(index));
                tempList.remove(index);
            }
        }
        this.nextCardList = tempList;

        //对三家牌进行排序，底牌不用排序
        this.prevCardList.sort((card1, card2) -> card2.getSortValue() - card1.getSortValue());
        this.myCardList.sort((card1, card2) -> card2.getSortValue() - card1.getSortValue());
        this.nextCardList.sort((card1, card2) -> card2.getSortValue() - card1.getSortValue());
    }

    /**
     * 根据鼠标点击位置，返回指定的牌的index
     */
    private int getCardIndex(Point point){
        if(this.myCardList.isEmpty()){
            return -1;
        }
        this.myCardStartPosX = calStartX(this.myCardList.size(), this.cardWidth/2);
        int clickedX = point.x;
        int clickedY = point.y;
        if(clickedX > this.myCardStartPosX &&
            clickedX < (this.myCardStartPosX + (this.myCardList.size()-1)*this.myCardCap+this.cardWidth)){
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
        paintCards(this.myCardList, this.myCardCap, this.myCardStartPosY, g);
        //绘制本家已打出的牌
        paintCards(this.myPlayedCardList, this.myPlayedCardCap, this.myPlayedCardStartY, g);

        //绘制上家的牌
        paintSideCards(prevCardList, this.sideCardCap, Side.PREV, g);
        //绘制下家的牌
        paintSideCards(nextCardList, this.sideCardCap, Side.NEXT, g);
    }

    /**
     * 绘制背景图
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
