package ncu.mac.commons.utils;

import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Pattern;

public class Miscellaneous {
    private static final Logger logger = LoggerFactory.getLogger(Miscellaneous.class);
    public static final Type LIST_TYPE = new TypeToken<List<String>>() {
    }.getType();
    public static final Pattern IPv4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    public static boolean isIPv4String(String ipString) {
        final var matches = IPv4_PATTERN.matcher(ipString).matches();
        logger.trace("{}: {}", ipString, matches);
        return matches;
    }
}
