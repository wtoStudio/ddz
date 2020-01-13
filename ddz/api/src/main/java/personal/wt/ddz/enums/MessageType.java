package personal.wt.ddz.enums;

import lombok.Getter;

/**
 * @author ttb
 */
@Getter
public enum MessageType {
    /**
     * 用户加入房间
     */
    JOIN("JOIN", "用户加入房间"),

    /**
     * 服务端消息给所有客户端，告知客户端有几个玩家加入了房间
     */
    ALL_JOINED("ALL_JOINED", "所有已加入房间的用户"),

    /**
     * 用户离开房间时，服务端给其他客户端发送此类型的消息
     */
    EXIT("EXIT", "用户离开"),

    /**
     * 准备
     */
    READY("READY", "准备"),

    /**
     * 准备OK
     */
    READY_OK("READY_OK", "准备OK"),

    /**
     * 发牌，服务端随机分配牌，并把数据发往客户端
     */
    DEAL_CARD("DEAL_CARD", "发牌"),

    /**
     * 取消准备
     */
    UNREADY("UNREADY", "取消准备"),

    /**
     * 取消准备OK
     */
    UNREADY_OK("UNREADY_OK", "取消准备OK");

    /**
     * code
     */
    String code;

    /**
     * desc
     */
    String desc;

    MessageType(String code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
