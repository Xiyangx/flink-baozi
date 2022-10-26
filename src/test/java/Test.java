import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: PACKAGE_NAME
 * @date:2022/8/30
 */
public class Test {
    public static void main(String[] args) {
        System.out.println();
        System.out.println(Math.ceil(new Double(55) / new Double(4)));
        List<String> set = new ArrayList<>();
        for (int i = 0; i < 55; i++) {
            set.add(i + "");
        }
        int indexOfThisSubtask=4;
        List<List<String>> newList = Lists.partition(set, (int) Math.ceil(new Double(set.size()) / new Double(4)));
        for (int index = 0; index < newList.size(); index++) {
            if (indexOfThisSubtask == (index + 1)) {
                List<String> strings = newList.get(index);
                System.out.println(strings.toString());
                break;
            }
        }
    }
}
