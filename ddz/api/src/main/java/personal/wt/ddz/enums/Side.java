package personal.wt.ddz.enums;

/**
 * @author ttb
 */

public enum Side {
    LOCAL("LOCAL", "本家"), NEXT("NEXT", "下家"), PREV("PREV", "上家");

    String code;

    String desc;

    Side (String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
