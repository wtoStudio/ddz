package personal.wt.ddz.enums;

import lombok.Getter;

/**
 * @author ttb
 */

@Getter
public enum UserStatus {
    /**
     * 空闲状态
     */
    IDLE("IDLE", "空闲"),

    /**
     * 准备就绪
     */
    READY("READY", "准备就绪"),

    /**
     * 游戏中
     */
    PLAYING("PLAYING", "游戏中");

    String code;

    String desc;

    UserStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
