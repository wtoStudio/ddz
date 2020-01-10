package personal.wt.ddz.entity;

import lombok.*;
import personal.wt.ddz.enums.Side;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.util.UUID;

/**
 * @author ttb
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class User {
    /**
     * UUID的方式生成ID
     */
    private String id;

    /**
     * 用户名(亦可是昵称)
     */
    private String name;

    /**
     * 存放玩家手里的牌
     */
    private List<Card> cardList = new ArrayList<>();

    /**
     * 存放玩家已打出的牌
     */
    private List<Card> playedCardList = new ArrayList<>();

    /**
     * 相对位置
     */
    private Side side;

    /**
     * 根据index来确定相对位置
     */
    private int index;

    /**
     * 头像icon
     */
    private Image headerImage;

    public User(String name, Side side){
        this.id = UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 10);
        this.name = name;
        this.side = side;
    }
}
