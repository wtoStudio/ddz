package personal.wt.ddz.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/**
 * @author ttb
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DealCardResult {
    private List<List<Card>> userCardList;

    private List<Card> hiddenCardList;

    public static DealCardResult of(List<Card> hiddenCardList, List<Card> ... lists){
        DealCardResult result = new DealCardResult();
        result.setHiddenCardList(hiddenCardList);
        result.setUserCardList(Arrays.asList(lists));
        return result;
    }
}
