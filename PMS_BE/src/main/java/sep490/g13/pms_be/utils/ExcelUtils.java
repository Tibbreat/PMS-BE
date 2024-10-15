package sep490.g13.pms_be.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

public class ExcelUtils {

    public static <T> ByteArrayInputStream dataToExcel(List<T> data, String[] columns, String sheetName, BiConsumer<Row, T> dataMapper) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // Tạo header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Thêm dữ liệu
            int rowIdx = 1;
            for (T record : data) {
                Row row = sheet.createRow(rowIdx++);
                dataMapper.accept(row, record);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
    // T import là kiểu dữ liệu của các đối tượng sẽ được tạo ra từ file Excel
    public static <T> List<T> importDataFromExcel(InputStream is, BiConsumer<Row, T> dataMapper, Class<T> clazz) throws IOException {
        List<T> dataList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Bỏ qua dòng tiêu đề (header)
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                try {
                    T instance = clazz.getDeclaredConstructor().newInstance(); // Tạo đối tượng kiểu T

                    // Ánh xạ dữ liệu từ Row trong Excel sang đối tượng T
                    dataMapper.accept(currentRow, instance);
                    dataList.add(instance);

                } catch (Exception e) {
                    e.printStackTrace(); // Xử lý lỗi khi tạo đối tượng
                }
            }
        }
        return dataList;
    }
}
