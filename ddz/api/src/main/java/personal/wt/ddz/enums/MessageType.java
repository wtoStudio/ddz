package personal.wt.ddz.enums;

import lombok.Getter;

/**
 * @author ttb
 */
@Getter
public enum MessageType {
    READY("READY", "准备"),
    UN_READY("UN_READY", "取消准备");

    String code;

    String desc;

    MessageType(String code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
