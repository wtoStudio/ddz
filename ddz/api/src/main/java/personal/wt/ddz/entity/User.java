package personal.wt.ddz.entity;

import lombok.*;
import personal.wt.ddz.enums.Side;
import personal.wt.ddz.enums.UserStatus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author ttb
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Builder
public class User implements Serializable {
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
     * 玩家状态
     */
    private UserStatus status;

    /**
     * 根据index来确定相对位置
     */
    private int index;

    /**
     * IP地址字符串
     */
    private String ip;

    /**
     * 端口
     */
    private int port;

    /**
     * 头像icon
     */
    //private Image headerImage;

    public User(String name){
        //生成默认ID
        this.id = UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 10);
        this.name = name;
        //this.headerImage = headerImage;
        //状态默认为【空闲】
        this.status = UserStatus.IDLE;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", index=" + index +
                ", port=" + port +
                '}';
    }
}
