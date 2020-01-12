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
     * 取消准备
     */
    UN_READY("UN_READY", "取消准备");

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
