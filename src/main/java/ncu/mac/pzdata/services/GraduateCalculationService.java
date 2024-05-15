package ncu.mac.pzdata.services;

import ncu.mac.commons.helpers.ExcelGridHelper;
import ncu.mac.pzdata.properties.ApplicationProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public interface GraduateCalculationService {
    ExcelGridHelper.DataGrid fromExcel(ApplicationProperties.GraduationSettings graduateParameter, InputStream inputStream, int sheetIndex) throws IOException;

    enum AttendClassNotation {
        PRESENT("V", "出席"),
        PERSONAL_LEAVE("O", "請假"),
        //        ABSENT("A", "晚到(曠課)"),
//        RESILED("X", "中輟"),
//        DATETIME_MAKE_UP("D", "日補"),
//        NIGHTTIME_MAKE_UP("N", "夜補"),
//        OFFICIAL_LEAVE("W", "公假"),
        HOLIDAY("F", "放香"),
        MAKE_UP("M", "補課"),
        LATE("L", "遲到"),
        //        LEAVE_EARLY("E", "早退"),
//        SPECIAL_1("S1", "特殊1"),
//        SPECIAL_2("S2", "特殊2"),
//        SPECIAL_3("S3", "特殊3"),
        UNDEFINED("-", "undefined");
        private final String notation;
        private final String chineseDescription;

        AttendClassNotation(String notation, String chineseDescription) {
            this.notation = notation;
            this.chineseDescription = chineseDescription;
        }

        public String getNotation() {
            return notation;
        }

        public String getChineseDescription() {
            return chineseDescription;
        }

        public static AttendClassNotation of(String notation) {
            return Arrays.stream(values())
                    .filter(attendClassNotation -> attendClassNotation.notation.equals(notation))
                    .findFirst()
                    .orElse(UNDEFINED);
        }
    }
}
