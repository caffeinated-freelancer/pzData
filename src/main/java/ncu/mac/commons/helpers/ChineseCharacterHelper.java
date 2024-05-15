package ncu.mac.commons.helpers;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class ChineseCharacterHelper {
    private static final Collator coll = Collator.getInstance(Locale.TRADITIONAL_CHINESE);
    public static final Comparator<String> chineseSimpleComparator = coll::compare;
    public static final Comparator<String> chineseComparator = (s1, s2) -> {
        if (s1 == null) {
            return s2 == null ? 0 : 1;
        } else if (s2 == null) {
            return -1;
        } else {
            return coll.compare(s1, s2);
        }
    };
}
