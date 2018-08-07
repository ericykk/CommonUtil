package com.eric.util.excel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.Date;
import java.util.List;

/**
 * Description:Excel转换器
 *
 * @author Eric
 * @Date 18/8/6
 */
public class ExcelConvertor {
    private Logger logger = LogManager.getLogger(ExcelConvertor.class);

    /**
     * 时间类型的样式  yyyy-MM-dd HH:mm
     */
    private CellStyle dateCellStyle;

    /**
     * 创建工作簿
     *
     * @param data            表数据
     * @param fieldColumnList 列信息
     * @param <T>             实体泛型
     * @return 创建好的工作簿
     */
    public <T> SXSSFWorkbook createExcel(List<T> data, List<FieldColumn> fieldColumnList) {

        // 内存中只驻留100行数据
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);

        // 创建日期类型
        dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat((short) 0x16);

        // 创建工作簿
        Sheet sheet = workbook.createSheet();

        fillHeader(sheet, fieldColumnList);
        fillBody(sheet, fieldColumnList, data);

        return workbook;
    }


    public <T> SXSSFWorkbook appendCreateSheet(SXSSFWorkbook workbook, List<T> data, List<FieldColumn> fieldColumnList) {
        // 创建工作簿
        Sheet sheet = workbook.createSheet();

        fillHeader(sheet, fieldColumnList);
        fillBody(sheet, fieldColumnList, data);

        return workbook;
    }


    /**
     * 创建表头
     *
     * @param sheet           工作簿中的一页工作表
     * @param fieldColumnList 列信息
     */
    private void fillHeader(Sheet sheet, List<FieldColumn> fieldColumnList) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < fieldColumnList.size(); i++) {
            header.createCell(i).setCellValue(fieldColumnList.get(i).getColumnName());
        }
    }

    /**
     * 填充Excel内容
     *
     * @param sheet           工作表
     * @param fieldColumnList 列信息
     * @param data            将要填充的数据
     * @param <T>             实体泛型
     */
    private <T> void fillBody(Sheet sheet, List<FieldColumn> fieldColumnList, List<T> data) {
        for (int i = 0; i < data.size(); i++) {
            fillRow(sheet.createRow(i + 1), fieldColumnList, data.get(i));
        }
    }

    /**
     * 填充一个数据实体为一行Excel
     *
     * @param row             被填充的行对象
     * @param fieldColumnList 列信息
     * @param oneData         将要填充的数据
     * @param <T>             实体泛型
     */
    private <T> void fillRow(Row row, List<FieldColumn> fieldColumnList, T oneData) {
        for (int i = 0; i < fieldColumnList.size(); i++) {
            try {
                Object returnValue = fieldColumnList.get(i).getGetter().invoke(oneData);
                if (null == returnValue) {
                    row.createCell(i).setCellValue("");
                    continue;
                }
                Class returnType = fieldColumnList.get(i).getGetter().getReturnType();
                if (returnType == String.class) {
                    row.createCell(i).setCellValue(String.valueOf(returnValue));
                } else if (returnType == int.class || returnType == Integer.class) {
                    row.createCell(i).setCellValue((Integer) returnValue);
                } else if (returnType == boolean.class || returnType == Boolean.class) {
                    row.createCell(i).setCellValue((Boolean) returnValue);
                } else if (returnType == double.class || returnType == Double.class) {
                    row.createCell(i).setCellValue((Double) returnValue);
                } else if (returnType == Date.class) {
                    Cell dateCell = row.createCell(i);
                    dateCell.setCellValue((Date) returnValue);
                    dateCell.setCellStyle(dateCellStyle);
                } else {
                    row.createCell(i).setCellValue(String.valueOf(returnValue));
                }
            } catch (Exception e) {
                logger.error("填充单元格出错, index=" + i + ", 数据内容: " + oneData.toString());
            }
        }
    }

}
