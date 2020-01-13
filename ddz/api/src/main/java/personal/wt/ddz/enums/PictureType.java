package personal.wt.ddz.enums;

import lombok.Getter;

/**
 * 表示扑克牌中的几种花色
 * @author ttb
 */
@Getter
public enum PictureType {
    HX("HX", "红心"),
    FP("FP", "方片"),
    HT("HT", "黑桃"),
    MH("MH", "梅花"),
    JOKER0("JOKER", "小王"),
    JOKER1("JOKER", "大王");

    String code;

    String desc;

    PictureType(String code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
