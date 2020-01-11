package personal.wt.ddz.config;

import personal.wt.ddz.util.Util;

/**
 * @author lenovo
 */
public class DrawConfig {
    private DrawConfig(){}

    /**
     * 游戏画面宽度
     */
    public final static int GAME_WIDTH;

    /**
     * 游戏画面高度
     */
    public final static int GAME_HEIGHT;

    /**
     * 扑克牌宽度
     */
    public final static int CARD_WIDTH;

    /**
     * 扑克牌高度
     */
    public final static int CARD_HEIGHT;

    /**
     * 底牌， 相邻两张之间的间距
     */
    public final static int HIDDEN_CARD_CAP;

    /**
     * 底牌开始显示位置Y
     */
    public final static int HIDDEN_CARD_START_Y;

    //----------------本家的牌显示位置配置START----------------
    /**
     * 本家的牌开始显示位置Y
     */
    public final static int LOCAL_CARD_START_POS_Y;
    /**
     * 本家打出的牌开始显示位置Y
     */
    public final static int LOCAL_PLAYED_CARD_START_Y;
    /**
     * 本家的牌，相邻两张的间距
     */
    public final static int LOCAL_CARD_CAP;
    /**
     * 本家打出的牌，相邻两张的间距
     */
    public final static int LOCAL_PLAYED_CARD_CAP;
    //----------------本家的牌显示位置配置END----------------

    //----------------上下家的牌显示位置配置START----------------
    /**
     * 上下家手里的牌与左侧（或右侧）的距离
     */
    public final static int SIDE_CAP;
    public final static int PREV_SIDE_START_X;
    public final static int NEXT_SIDE_START_X;
    public final static int SIDE_START_Y;
    public final static int SIDE_CARD_CAP;
    /**
     * 上下家打出的牌的开始位置与左侧（或右侧）的距离
     */
    public final static int SIDE_PLAYED_CARD_CAP;
    public final static int SIDE_PLAYED_CARD_START_Y;
    //----------------上下家的牌显示位置配置END----------------

    //----------------用户名字和头像显示位置START----------------
    /*public final static int LOCAL_USER_NAME_START_X;
    public final static int LOCAL_USER_NAME_START_Y;

    public final static int PREV_USER_NAME_START_X;
    public final static int PREV_USER_NAME_START_Y;

    public final static int NEXT_USER_NAME_START_X;
    public final static int NEXT_USER_NAME_START_Y;*/
    //----------------用户名字和头像显示位置END----------------

    static {
        //计算游戏画面的宽度和高度
        GAME_WIDTH = (int) (Util.getScreenSize().width * 0.8);
        GAME_HEIGHT = (int) (GAME_WIDTH * 0.55);

        //计算扑克牌的宽度和高度
        CARD_WIDTH = GAME_WIDTH / 25;
        CARD_HEIGHT = (int) (CARD_WIDTH * 1.5);

        //计算底牌位置
        HIDDEN_CARD_CAP = CARD_WIDTH + 30;
        HIDDEN_CARD_START_Y = 80;

        //计算本家的牌的位置
        LOCAL_CARD_START_POS_Y = GAME_HEIGHT - (CARD_HEIGHT + 30);
        LOCAL_PLAYED_CARD_START_Y = LOCAL_CARD_START_POS_Y - (CARD_HEIGHT + 45);
        LOCAL_CARD_CAP = CARD_WIDTH / 2;
        LOCAL_PLAYED_CARD_CAP = CARD_WIDTH / 2;

        //计算上下家的牌的位置
        SIDE_CAP = 50;
        PREV_SIDE_START_X = SIDE_CAP;
        NEXT_SIDE_START_X = GAME_WIDTH - (CARD_WIDTH + SIDE_CAP);
        SIDE_START_Y = 80;
        SIDE_CARD_CAP = CARD_HEIGHT * 2 / 5;

        //上下家打出的牌的开始位置与左侧（或右侧）的距离
        SIDE_PLAYED_CARD_CAP = SIDE_CAP + CARD_WIDTH + 50;
        SIDE_PLAYED_CARD_START_Y = HIDDEN_CARD_START_Y + CARD_HEIGHT + 50;
    }
}
