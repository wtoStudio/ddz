package personal.wt.ddz.entity;

import lombok.Getter;
import lombok.Setter;
import personal.wt.ddz.enums.PictureType;

import java.awt.*;
import java.io.Serializable;

/**
 * @author ttb
 */
@Setter
@Getter
public class Card implements Serializable {

    private PictureType pictureType;

    private String v;

    private boolean isSelected;

    private Image image;

    private int sortValue;

    public Card(){}

    public Card(PictureType pictureType, String v){
        this.pictureType = pictureType;
        this.v = v;
    }

    @Override
    public String toString() {
        return this.pictureType.getDesc() + "-" + this.v;
    }
}
