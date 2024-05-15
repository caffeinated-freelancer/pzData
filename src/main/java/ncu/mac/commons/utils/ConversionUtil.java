package ncu.mac.commons.utils;

import org.springframework.util.StringUtils;

public class ConversionUtil {
    public static int string2Integer(String value, int defaultValue) {
        if (StringUtils.hasLength(value)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException | NullPointerException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }
}
