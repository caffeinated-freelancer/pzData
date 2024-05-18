package ncu.mac.pzdata.services;

import ncu.mac.commons.helpers.ExcelGridHelper;
import ncu.mac.commons.helpers.ExcelReaderHelper;
import ncu.mac.commons.utils.ConversionUtil;
import ncu.mac.commons.utils.StackTraceUtil;
import ncu.mac.pz.helpers.PuzhongGeneralHelper;
import ncu.mac.pzdata.models.GraduationInfoModel;
import ncu.mac.pzdata.properties.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Service
public class GraduateCalculationServiceImpl implements GraduateCalculationService {
    private static final Logger logger = LoggerFactory.getLogger(GraduateCalculationServiceImpl.class);
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\s*(\\d+)/(\\d+)\\s*$");

    private final MemberService memberService;

    private enum ColumnHeader {
        STUDENT_ID("學員編號"),
        REAL_NAME("姓名"),
        DHARMA_NAME("法名"),
        GENDER("性別"),
        GROUP("組別");
        private final String stringPattern;

        ColumnHeader(String stringPattern) {
            this.stringPattern = stringPattern;
        }

        public static Optional<ColumnHeader> matching(String text) {
            if (StringUtils.hasLength(text)) {
                return Arrays.stream(values())
                        .filter(columnHeader -> text.startsWith(columnHeader.stringPattern))
                        .findFirst();
            }
            return Optional.empty();
        }
    }

    private static class AttendCounters {
        private int present = 0;
        private int late = 0;
        private int holiday = 0;
        private int makeUp = 0;
        private int personalLeave = 0;
        private int undefined = 0;
    }

    private static class GraduateResult {
        private boolean graduate;
        private boolean diligent;
        private boolean perfect;

        public GraduateResult(boolean graduate, boolean diligent, boolean perfect) {
            this.graduate = graduate;
            this.diligent = diligent;
            this.perfect = perfect;
        }
    }

    public GraduateCalculationServiceImpl(MemberService memberService) {
        this.memberService = memberService;
    }

    private static GraduateResult graduateCalculator(AttendCounters counters, ApplicationProperties.GraduationSettings graduateParameter) {
        if (counters.present + counters.late + counters.makeUp >= graduateParameter.getAttendMinimum()
                && counters.makeUp <= graduateParameter.getMakeupMaximum()
                && counters.personalLeave <= graduateParameter.getAbsentMaximum()) {
            // 結業
            if (counters.present >= graduateParameter.getPerfectAttendance()) {
                // 全勤
                return new GraduateResult(true, false, true);
            } else {
                return new GraduateResult(true, false, false);
            }
        } else if (counters.present + counters.late + counters.makeUp >= graduateParameter.getDiligent()) {
            // 勤學
            return new GraduateResult(false, true, false);
        }
        return new GraduateResult(false, false, false);
    }

    private Optional<String> dateMatching(String text) {
        if (StringUtils.hasLength(text)) {
            final var matcher = DATE_PATTERN.matcher(text);

            if (matcher.matches()) {
                return Optional.of(String.format("%02d/%02d",
                        ConversionUtil.string2Integer(matcher.group(1), 1),
                        ConversionUtil.string2Integer(matcher.group(2), 1)));
            }
        }
        return Optional.empty();
    }

    @Override
    public ExcelGridHelper.DataGrid fromExcel(ApplicationProperties.GraduationSettings graduateParameter, InputStream inputStream, int sheetIndex) throws IOException {
        final var excelReader = ExcelReaderHelper.getInstance(inputStream);
        final var inDataRow = new AtomicBoolean(false);
        final var headerLocation = new HashMap<ColumnHeader, Integer>();
        final var dateOnColumn = new TreeMap<String, Integer>();
        final var dataLocationDate = new HashMap<Integer, String>();
        final var maxHeaderLocation = new AtomicInteger(0);
        final var maxDataLocation = new AtomicInteger(0);
        final var minDataLocation = new AtomicInteger(Integer.MAX_VALUE);
        final var currentRow = new AtomicInteger(1);
        final var groupSerialNo = new AtomicInteger(0);

        final var dataGrid = ExcelGridHelper.getInstance("Sheet1");
        final var dateColumnBegin = 5;

        excelReader.each(sheetIndex, (row, cells) -> {
            if (row > 0 && !cells.isEmpty()) {
                // System.out.printf("Row: %d, %d\n", row, cells.size());
                if (!inDataRow.get()) {
                    IntStream.range(0, cells.size())
                            .forEach(integer -> ColumnHeader.matching(cells.get(integer))
                                    .ifPresentOrElse(columnHeader -> {
                                                headerLocation.putIfAbsent(columnHeader, integer);
                                                maxHeaderLocation.set(Math.max(integer, maxHeaderLocation.get()));
                                            },
                                            () -> dateMatching(cells.get(integer))
                                                    .ifPresent(s -> {
                                                        dateOnColumn.put(s, integer);
                                                        dataLocationDate.put(integer, s);
                                                        maxDataLocation.set(Math.max(integer, maxDataLocation.get()));
                                                        minDataLocation.set(Math.min(integer, minDataLocation.get()));
                                                    })));

                    if (headerLocation.containsKey(ColumnHeader.STUDENT_ID)
                            && headerLocation.containsKey(ColumnHeader.REAL_NAME)
                            && headerLocation.containsKey(ColumnHeader.DHARMA_NAME)) {
                        inDataRow.set(true);

                        dataGrid.putValue(0, 0, "組序");
                        dataGrid.putValue(0, 1, "姓名");
                        dataGrid.putValue(0, 2, "法名");
                        dataGrid.putValue(0, 3, "組別");
                        dataGrid.putValue(0, 4, "執事");

                        int j = 0;
                        for (var i = minDataLocation.get(); i < maxDataLocation.get(); i++, j++) {
                            dataGrid.putValue(0, dateColumnBegin + j, dataLocationDate.getOrDefault(i, ""));
                        }
                        dataGrid.putValue(0, dateColumnBegin + j++, "出席\r\nV");
                        dataGrid.putValue(0, dateColumnBegin + j++, "遲到\r\nL");
                        dataGrid.putValue(0, dateColumnBegin + j++, "補課\r\nM");
                        dataGrid.putValue(0, dateColumnBegin + j++, "請假\r\nO");
                        dataGrid.putValue(0, dateColumnBegin + j++, "全勤");
                        dataGrid.putValue(0, dateColumnBegin + j++, "勤學");
                        dataGrid.putValue(0, dateColumnBegin + j, "結業");

                        currentRow.set(1);
                        groupSerialNo.set(0);
                    }
                    if (logger.isDebugEnabled()) {
                        StackTraceUtil.print1(headerLocation);
                        StackTraceUtil.print1(dateOnColumn);
                    }
                } else if (cells.size() > headerLocation.size() &&
                        cells.size() > maxHeaderLocation.get()) {

                    final var studentId = cells.get(headerLocation.get(ColumnHeader.STUDENT_ID));
                    final var pzFullName = cells.get(headerLocation.get(ColumnHeader.REAL_NAME));
                    final var dharmaName = cells.get(headerLocation.get(ColumnHeader.DHARMA_NAME));

                    if (StringUtils.hasLength(pzFullName) && !pzFullName.startsWith("範例-")) {
                        final var memberModelOptional = memberService.getMemberByStudentId(studentId);
                        final var realName = PuzhongGeneralHelper.stripBracketInRealName(pzFullName);

                        if (memberModelOptional.isEmpty()) {
                            System.out.printf("Student %s not found. (not in db, only in class attendance)\n", studentId);
                        } else {
                            final var memberModel = memberModelOptional.get();
                            final var dbRealName = PuzhongGeneralHelper.stripBracketInRealName(memberModel.getStudentName());

                            if (!realName.equals(dbRealName)) {
                                System.out.printf("Student %s has %s (in db) vs %s (in class attendance)\n", studentId,
                                        dbRealName, realName);
                            } else {
                                if (logger.isDebugEnabled()) {
                                    System.out.printf("[%s][%s][%s] - ", studentId, realName, dharmaName);
                                    for (var i = minDataLocation.get(); i < maxDataLocation.get() && i < cells.size(); i++) {
                                        System.out.printf("[%s: %s] ",
                                                dataLocationDate.getOrDefault(i, ""),
                                                cells.get(i));
                                    }
                                    System.out.println();
                                }
                                final var r = currentRow.getAndIncrement();
                                dataGrid.putValue(r, 0, groupSerialNo.incrementAndGet());
                                dataGrid.putValue(r, 1, realName);
                                dataGrid.putValue(r, 2, dharmaName);
                                dataGrid.putValue(r, 3, memberModel.getClassGroup());
                                if (StringUtils.hasLength(memberModel.getDeacon())) {
                                    dataGrid.putValue(r, 4, memberModel.getDeacon());
                                }

                                final var counters = new AttendCounters();

                                int j = 0;
                                for (var i = minDataLocation.get(); i < maxDataLocation.get() && i < cells.size(); i++, j++) {
                                    final var cellValue = cells.get(i);
                                    final var attendClassNotation = AttendClassNotation.of(cellValue);

                                    switch (attendClassNotation) {
                                        case PRESENT -> counters.present++;
                                        case LATE -> counters.late++;
                                        case HOLIDAY -> counters.holiday++;
                                        case MAKE_UP -> counters.makeUp++;
                                        case PERSONAL_LEAVE -> counters.personalLeave++;
                                        case UNDEFINED -> counters.undefined++;
                                    }
                                    dataGrid.putValue(r, dateColumnBegin + j, cellValue);
                                }

                                final var graduateResult = graduateCalculator(counters, graduateParameter);

                                dataGrid.putValue(r, dateColumnBegin + j++, counters.present);
                                dataGrid.putValue(r, dateColumnBegin + j++, counters.late);
                                dataGrid.putValue(r, dateColumnBegin + j++, counters.makeUp);
                                dataGrid.putValue(r, dateColumnBegin + j++, counters.personalLeave);
                                dataGrid.putValue(r, dateColumnBegin + j++, graduateResult.perfect ? "全勤" : "");
                                dataGrid.putValue(r, dateColumnBegin + j++, graduateResult.diligent ? "勤學" : "");
                                dataGrid.putValue(r, dateColumnBegin + j, graduateResult.graduate ? "V" : "");

                                memberModel.setGraduationInfoModel(new GraduationInfoModel(
                                        graduateResult.graduate,
                                        counters.personalLeave,
                                        counters.makeUp
                                ));
                            }
                        }
                    }
                }
//                for (var i = 0; i < cells.size(); i++) {
//                    System.out.printf("[%s] ", cells.get(i));;
//                }
//                System.out.println();
            }
        });

        return dataGrid;

//        StackTraceUtil.print1(importMembers);
//        return CommonAjaxResponse.withResult("");
    }
}
