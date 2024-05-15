package ncu.mac.commons.utils;

import java.util.Comparator;

public class ComparingUtil {
    public static <T> int compareNullable(T t1, T t2, Comparator<T> comparator) {
        if (t1 != null) {
            if (t2 != null) {
                return comparator.compare(t1, t2);
            } else {
                return -1;
            }
        } else if (t2 != null) {
            return 1;
        } else {
            return 0;
        }
    }
}
