package personal.wt.ddz.entity;

import lombok.*;
import personal.wt.ddz.enums.MessageType;

import java.io.Serializable;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message implements Serializable {
    /**
     * 消息类型
     */
    private MessageType type;

    /**
     * 消息体：JSON字符串
     */
    private String content;
}
