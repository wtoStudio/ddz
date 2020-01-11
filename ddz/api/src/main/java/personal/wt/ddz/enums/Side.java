package personal.wt.ddz.enums;

/**
 * 相对位置枚举类
 * @author ttb
 */
public enum Side {
    /**
     * 本家
     */
    LOCAL("LOCAL", "本家"),
    /**
     * 下家
     */
    NEXT("NEXT", "下家"),
    /**
     * 上家
     */
    PREV("PREV", "上家");

    String code;

    String desc;

    Side (String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}