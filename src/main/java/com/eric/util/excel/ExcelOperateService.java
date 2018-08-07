package com.eric.util.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Description: 通过用excel操作类
 * author: Eric
 * Date: 18/8/6
 */
@Slf4j
public class ExcelOperateService {

    /**
     * 读取Excel文件
     *
     * @param filePath 工作簿文件路径
     * @param num      工作簿中第num个表格
     * @return
     */
    public List<String[]> readExcel(String filePath, int num) {
        List<String[]> dataList = new ArrayList<String[]>();
        boolean isExcel2003 = true;
        if (isExcel2007(filePath)) {
            isExcel2003 = false;
        }
        File file = new File(filePath);
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            log.info("文件找不到");
        }
        Workbook wb;
        try {
            wb = isExcel2003 ? new HSSFWorkbook(is) : new XSSFWorkbook(is);
            Sheet sheet = wb.getSheetAt(num - 1);
            int totalRows = sheet.getPhysicalNumberOfRows();
            int totalCells = 0;
            if (totalRows >= 1 && sheet.getRow(0) != null) {
                totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
            }
            for (int r = 0; r < totalRows; r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                String[] rowList = new String[totalCells];
                for (int c = 0; c < totalCells; c++) {
                    Cell cell = row.getCell(c);
                    String cellValue = "";
                    if (cell == null) {
                        rowList[c] = (cellValue);
                        continue;
                    }
                    cellValue = convertCellStr(cell, cellValue);
                    rowList[c] = (cellValue);
                }
                dataList.add(rowList);
            }
        } catch (IOException ex) {
            log.info("工作簿初始化出错");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return dataList;
    }

    /**
     * 读取单元格内容
     *
     * @param cell
     * @param cellStr
     * @return
     */
    private String convertCellStr(Cell cell, String cellStr) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                // 读取String
                cellStr = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                // 得到Boolean对象的方法
                cellStr = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                // 先看是否是日期格式
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 读取日期格式
                    cellStr = formatTime(cell.getDateCellValue().toString());
                } else {
                    // 读取数字
                    cellStr = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_FORMULA:
                // 读取公式
                cellStr = cell.getCellFormula();
                break;
            default:
                cellStr = cell.getStringCellValue();
        }
        return cellStr;
    }

    /**
     * 判断是否是Excel2007
     *
     * @param fileName
     * @return
     */
    private boolean isExcel2007(String fileName) {
        return fileName.matches("^.+\\.(?i)(xlsx)$");
    }


    private String formatTime(String s) {
        SimpleDateFormat sf = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = sf.parse(s);
        } catch (ParseException ex) {

            log.warn(ex.getMessage(), ex);

        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = sdf.format(date);
        return result;
    }

    /**
     * 通用写入excel的方法
     *
     * @param excelDataList
     * @param wb
     * @param sheetType
     * @return
     * @throws Exception
     */
    public HSSFWorkbook writeExcel(List<List<String>> excelDataList, HSSFWorkbook wb, Integer sheetType) throws Exception {
        // 添加sheet
        HSSFSheet sheet;
        if (sheetType != null) {
            sheet = wb.createSheet("sheet" + sheetType);
        } else {
            sheet = wb.createSheet("sheet");
        }
        // 表格样式
        HSSFCellStyle style = wb.createCellStyle();
        // 指定单元格居中对齐
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 设置字体
        HSSFFont f = wb.createFont();
        f.setFontName("宋体");
        f.setFontHeightInPoints((short) 10);
        f.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        style.setFont(f);
        int dataLength = excelDataList.size();
        for (int i = 0; i < dataLength; i++) {
            HSSFRow row = sheet.createRow(i);
            List<String> list = excelDataList.get(i);
            for (int j = 0; j < list.size(); j++) {
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(list.get(j));
                cell.setCellStyle(style);
            }
        }
        return wb;
    }
}
