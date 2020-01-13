package personal.wt.ddz.entity;

import lombok.*;
import personal.wt.ddz.core.GameManager;
import personal.wt.ddz.enums.PictureType;

import java.awt.*;
import java.io.Serializable;

/**
 * @author ttb
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card implements Serializable {

    private PictureType pictureType;

    private String v;

    private boolean isSelected;

    private int sortValue;

    public Card(PictureType pictureType, String v){
        this.pictureType = pictureType;
        this.v = v;
    }

    public String imageKey(){
        return this.pictureType.getCode() + this.v;
    }

    @Override
    public String toString() {
        return this.pictureType.getDesc() + "-" + this.v;
    }
}
