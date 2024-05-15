package ncu.mac.commons.helpers;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ExcelGridHelper {
    public interface DataGrid {
        void putValue(int row, int column, String data);
        void putValue(int row, int column, int data);
        void write(OutputStream outputStream) throws IOException;
        void mergeCell(int firstRow, int lastRow, int firstCol, int lastCol);
    }

    private static class DataGridImpl implements DataGrid {
        private final XSSFWorkbook workbook;
        private final XSSFSheet sheet;
        private final XSSFCellStyle cellStyle;
        private final Map<Integer, XSSFRow> rows;

        private DataGridImpl(String sheetName) {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet(sheetName);

            final var fontData = workbook.createFont();
            fontData.setFontName("Arial");
            fontData.setFontHeightInPoints((short) 16);
            fontData.setBold(false);

            cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(false);
            cellStyle.setFont(fontData);

            sheet.setDefaultRowHeight((short) 450);
            sheet.setDefaultColumnWidth(12);

            rows = new HashMap<>();
        }

        public void mergeCell(int firstRow, int lastRow, int firstCol, int lastCol) {
            sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
        }

        @Override
        public void putValue(int row, int column, String data) {
            final var xssfRow = rows.computeIfAbsent(row, k -> sheet.createRow(row));

            final var cell = xssfRow.createCell(column);
            cell.setCellValue(data);
            cell.setCellStyle(cellStyle);
        }

        @Override
        public void putValue(int row, int column, int data) {
            putValue(row, column, Integer.toString(data));
        }

        @Override
        public void write(OutputStream outputStream) throws IOException {
            workbook.write(outputStream);
            workbook.close();
        }
    }

    public static DataGrid getInstance(String sheetName) {
        return new DataGridImpl(sheetName);
    }
}
