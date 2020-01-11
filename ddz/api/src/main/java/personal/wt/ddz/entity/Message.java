package personal.wt.ddz.entity;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    /**
     * 谁发的
     */
    private String from;

    /**
     * 发给谁
     */
    private String to;

    /**
     * 消息类型
     */
    private int type;

    /**
     * 消息体：JSON字符串
     */
    private String content;
}
