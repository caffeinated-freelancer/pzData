package ncu.mac.pz.helpers;

import ncu.mac.commons.utils.ConversionUtil;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PuzhongGeneralHelper {
    private static final Pattern NAME_WITH_BRACKETS = Pattern.compile("^(.*)\\s*[\\(（].*");

    public static String stripBracketInRealName(String name) {
        if (name == null) {
            return "";
        } else {
            while (true) {
                Matcher matcher = NAME_WITH_BRACKETS.matcher(name);

                if (matcher.matches()) {
                    name = matcher.group(1);
                } else {
                    return name.replace(" ", "").replace("　", "");
                }
            }
        }
    }

    public static boolean isJoinEvent(String pattern) {
        return pattern != null && (pattern.startsWith("參加")
                || pattern.startsWith("V")
                || pattern.startsWith("v")
                || pattern.startsWith("1"));
    }

    public static boolean validStudentId(String studentId) {
        return StringUtils.hasLength(studentId) && studentId.matches("[0-9]{9}");
    }

    public static int string2StudentId(@Nullable String studentId, int defaultValue) {
        return Optional.ofNullable(studentId)
                .filter(StringUtils::hasLength)
                .filter(PuzhongGeneralHelper::validStudentId)
                .map(s -> ConversionUtil.string2Integer(s, defaultValue))
                .orElse(defaultValue);
    }
}
