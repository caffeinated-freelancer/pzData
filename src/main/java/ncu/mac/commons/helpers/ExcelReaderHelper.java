package ncu.mac.commons.helpers;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ExcelReaderHelper {
    private static final Logger logger = LoggerFactory.getLogger(ExcelReaderHelper.class);

    public interface ExcelReader {

        void each(int sheetIndex, BiConsumer<Integer, List<String>> biConsumer);

        void close();
    }

    private static class ExcelReaderImpl implements ExcelReader {
        private final Workbook workbook;

        public ExcelReaderImpl(InputStream inputStream) throws IOException {
            workbook = new XSSFWorkbook(inputStream);
        }

        @Override
        public void each(int sheetIndex, BiConsumer<Integer, List<String>> biConsumer) {
            final var sheet = workbook.getSheetAt(sheetIndex);
            for (var row : sheet) {
                final var dataList = new ArrayList<String>();


                short lastCellNum = row.getLastCellNum();

                for (var i = 0; i < lastCellNum; i++) {
                    var cell = row.getCell(i);

                    if (cell == null) {
                        dataList.add("");
                    } else {
                        try {
                            dataList.add(switch (cell.getCellType()) {
                                case STRING -> cell.getRichStringCellValue().getString();
                                case NUMERIC -> Long.toString(Double.valueOf(cell.getNumericCellValue()).longValue());
                                case FORMULA -> cell.getCellFormula();
                                case BOOLEAN -> cell.getBooleanCellValue() ? "true" : "false";
                                case ERROR -> "Error";
                                default -> "";
                            });
                        } catch (Exception e) {
                            logger.warn("{}: {}", e.getClass().getName(), e.getMessage());
                            dataList.add("");
                        }
                    }
                }

                biConsumer.accept(row.getRowNum(), dataList);
            }
        }

        @Override
        public void close() {
            try {
                workbook.close();
            } catch (IOException e) {
                logger.warn("{}: {}", e.getClass().getName(), e.getMessage());
            }
        }
    }

    public static ExcelReader getInstance(InputStream inputStream) throws IOException {
        return new ExcelReaderImpl(inputStream);
    }
}
