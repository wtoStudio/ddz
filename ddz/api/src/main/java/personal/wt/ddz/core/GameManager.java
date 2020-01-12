package personal.wt.ddz.core;

import org.springframework.beans.BeanUtils;
import personal.wt.ddz.entity.Card;
import personal.wt.ddz.entity.User;
import personal.wt.ddz.enums.PictureType;
import personal.wt.ddz.util.Util;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author ttb
 */
public class GameManager {
    public static final int MAX_MESSAGE_LENGTH = 1024 * 1000;

    /**
     * 存放所有54张扑克牌的集合
     */
    private static List<Card> allCardList = new ArrayList<>(54);

    public static List<Card> getAllCardList() {
        return allCardList;
    }

    /**
     * 存放所有用到的图片
     */
    public static Map<String, Image> imageMap = new HashMap<>(54);

    /**
     * 存放牌面字符与实际值的映射关系
     */
    private static Map<String, Integer> charValueMap = new HashMap<>(13);

    static {
        initImageMap();
        initCharValueMap();
        initCardList();
    }

    private static GameManager gameManager = new GameManager();

    private GameManager(){}

    public static GameManager getInstance(){
        return gameManager;
    }

    /**
     * 加载所有的图片
     */
    private static void initImageMap(){
        PictureType[] pictureTypes = PictureType.values();
        String[] values = new String[]{"A", "K", "Q", "J", "10", "9", "8", "7", "6", "5", "4", "3", "2"};
        for(PictureType pictureType : pictureTypes){
            if(pictureType.getCode().startsWith("JOKER")){
                continue;
            }
            for(String v : values){
                String imageName = pictureType.getCode() + v;
                imageMap.put(imageName, Util.loadImage("/images/card/" + imageName + ".png"));
            }
        }
        imageMap.put("JOKER0", Util.loadImage("/images/card/JOKER0.png"));
        imageMap.put("JOKER1", Util.loadImage("/images/card/JOKER1.png"));
        imageMap.put("gameBg", Util.loadImage("/images/background/bg2.jpg"));
    }

    private static void initCharValueMap(){
        charValueMap.put("A", 14);charValueMap.put("K", 13);charValueMap.put("Q", 12);charValueMap.put("J", 11);
        charValueMap.put("10", 10);charValueMap.put("9", 9);charValueMap.put("8", 8);charValueMap.put("7", 7);
        charValueMap.put("6", 6);charValueMap.put("5", 5);charValueMap.put("4", 4);charValueMap.put("3", 3);
        charValueMap.put("2", 15);
    }

    /**
     * 初始化一副牌
     */
    private static void initCardList(){
        PictureType[] pictureTypes = PictureType.values();
        String[] values = new String[]{"A", "K", "Q", "J", "10", "9", "8", "7", "6", "5", "4", "3", "2"};
        for(PictureType pictureType : pictureTypes){
            if(pictureType.getCode().startsWith("JOKER")){
                continue;
            }
            for(String v : values){
                Card card = new Card(pictureType, v);
                //card.setImage(imageMap.get(pictureType.getCode() + v));
                card.setSortValue(charValueMap.get(v));
                allCardList.add(card);
            }
        }
        Card joker0 = new Card(PictureType.JOKER0, "0");
        //joker0.setImage(imageMap.get("JOKER0"));
        joker0.setSortValue(9000001);
        Card joker1 = new Card(PictureType.JOKER1, "1");
        //joker1.setImage(imageMap.get("JOKER1"));
        joker1.setSortValue(9000002);
        allCardList.add(joker0);
        allCardList.add(joker1);
    }

    /**
     * 发牌
     */
    public void dealCard(User user1, User user2, User user3, List<Card> hiddenCardList){
        Random random = new Random();
        List<Card> tempList = new ArrayList<>();
        allCardList.forEach(c -> {
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
                user1.getCardList().add(tempList.get(index));
                tempList.remove(index);
            }
        }
        while(set2.size() < 17){
            int index = random.nextInt(tempList.size());
            if(set2.add(index)){
                user2.getCardList().add(tempList.get(index));
                tempList.remove(index);
            }
        }
        while(set3.size() < 3){
            int index = random.nextInt(tempList.size());
            if(set3.add(index)){
                hiddenCardList.add(tempList.get(index));
                tempList.remove(index);
            }
        }
        user3.getCardList().addAll(tempList);

        //对三家牌按照大小排序，底牌不用排序
        user1.getCardList().sort((card1, card2) -> card2.getSortValue() - card1.getSortValue());
        user2.getCardList().sort((card1, card2) -> card2.getSortValue() - card1.getSortValue());
        user3.getCardList().sort((card1, card2) -> card2.getSortValue() - card1.getSortValue());

        //return DealCardResult.of(hiddenCardList, list1, list2, list3);
    }
}
