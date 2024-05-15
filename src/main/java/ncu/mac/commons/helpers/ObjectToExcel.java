package ncu.mac.commons.helpers;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class ObjectToExcel<T> {
    private final Class<T> tClass;
    private final Map<String, Field> fieldMap;


    public ObjectToExcel(Class<T> tClass) {
        this.tClass = tClass;

        fieldMap = Arrays.stream(tClass.getDeclaredFields())
                .map(field -> Pair.of(field.getName(), field))
                .collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond));
    }

    private static Cell addIntToCell(Row row, int col, CellStyle style, int data) {
        final var cell = row.createCell(col);
        cell.setCellValue(data);
        cell.setCellStyle(style);
        return cell;
    }

    private static Cell addStringToCell(Row row, int col, CellStyle style, String data) {
        final var cell = row.createCell(col);
        cell.setCellValue(data);
        cell.setCellStyle(style);
        return cell;
    }

    public void writeExcel(OutputStream outputStream, String sheetName,
                           LinkedHashMap<String, String> headerMap,
                           List<T> data,
                           BiFunction<String, Object, Object> mapper) throws IOException {
        final var workbook = new XSSFWorkbook();

        final var sheet = workbook.createSheet(sheetName);

        sheet.setDefaultRowHeight((short) 450);
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

        final var sheetHeader = sheet.createRow(0);

        final var fontHeader = workbook.createFont();
        fontHeader.setFontName("Arial");
        fontHeader.setFontHeightInPoints((short) 16);
        fontHeader.setBold(true);

        final var fontData = workbook.createFont();
        fontData.setFontName("Arial");
        fontData.setFontHeightInPoints((short) 16);
        fontData.setBold(false);

        final var styleHeader = workbook.createCellStyle();
        styleHeader.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setFont(fontHeader);

        final var styleData = workbook.createCellStyle();
        styleData.setWrapText(false);
        styleData.setFont(fontData);

        final var headerKeyData = headerMap.keySet().stream().toList();

        for (int i = 0; i < headerKeyData.size(); i++) {
            addStringToCell(sheetHeader, i, styleHeader, headerKeyData.get(i));
        }

        for (int i = 0; i < data.size(); i++) {
            final var row = sheet.createRow(i + 1);
            final var entry = data.get(i);

            for (int j = 0; j < headerKeyData.size(); j++) {
                final var col = j;

                Optional.ofNullable(headerMap.get(headerKeyData.get(j)))
                        .flatMap(fieldName -> {
                            final var theField = fieldMap.get(fieldName);

                            if (theField != null) {
                                return Optional.of(theField)
                                        .map(field -> {
                                            try {
                                                field.setAccessible(true);
                                                return field.get(entry);
                                            } catch (IllegalAccessException e) {
                                                return null;
                                            }
                                        })
                                        .map(o -> Optional.ofNullable(mapper)
                                                .map(f -> f.apply(fieldName, o))
                                                .orElse(o));
                            } else {
                                return Optional.ofNullable(mapper)
                                        .map(f -> f.apply(fieldName, entry));
                            }
                        })
                        .ifPresent(o -> {
                            if (o instanceof Integer intValue) {
                                addIntToCell(row, col, styleData, intValue);
                            } else {
                                addStringToCell(row, col, styleData, o.toString());
                            }
                        });
            }
        }

        workbook.write(outputStream);
        workbook.close();
    }
}
