package com.eric.util.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.DateTime;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:Excel处理器 用于Excel的导入和导出
 * author: Eric
 * Date: 18/8/6
 */
@Slf4j
public class ExcelHandler {

    private ExcelConvertor excelConvertor;

    // 单例起来
    private ExcelHandler() {
        excelConvertor = new ExcelConvertor();
    }

    private static class ExcelSingle {
        private static ExcelHandler instance = new ExcelHandler();
    }

    public static ExcelHandler instance() {
        return ExcelSingle.instance;
    }

    /**
     * 针对只参与一张excel导出的实体,可使用此方法
     * 即实体上只有一个@Excel注解
     *
     * @param response
     * @param data       将要导出的数据
     * @param modelClass 数据的实体类型信息, 需要使用@Excel
     * @param <T>        实体泛型
     */
    public <T> void exportToExcel(HttpServletResponse response, List<T> data, Class<T> modelClass, List<?> data2, Class<?> modelClass2) {
        exportToExcel(response, data, modelClass, data2, modelClass2, null);
    }

    /**
     * 导出Excel报表
     *
     * @param response
     * @param data       将要导出的数据
     * @param modelClass 实体类型信息
     * @param fileName   导出后的文件名, 用于在一个实体参与多张excel导出时识别本次导出哪一张
     *                   如果实体只参与一张excel导出, 可填null提高效率; @Excel
     * @param <T>        实体泛型
     */
    public <T> void exportToExcel(HttpServletResponse response, List<T> data, Class<T> modelClass, List<?> data2, Class<?> modelClass2, String fileName) {

        // 找到导出文件的信息@Excel
        Excel excelAnnotation = findExcel(modelClass, fileName);
        if (excelAnnotation.limit() < data.size()) {
            throw new ExcelException("导出数据数量超出最大限制,最大限制为:" + excelAnnotation.limit() + "条");
        }

        // 文件名转码,如果发生意外就用当前毫秒数当文件名
        String encodingName = String.valueOf(System.currentTimeMillis());

        try {
            encodingName = URLEncoder.encode(fileName != null ? fileName : new DateTime().toString("yyyyMMdd") + "-" + excelAnnotation.value()[0], "utf-8");//fileName != null? fileName : excelAnnotation.value()[0]
        } catch (UnsupportedEncodingException e) {
            log.warn("导出excel时,文件名转码失败, 文件名:" + fileName);
        }

        // 配置response
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodingName + ".xlsx");

        // 找到导出列的信息 @Column
        List<FieldColumn> fieldColumnList = findFieldColumns(modelClass, fileName);
        // 创建Convertor
        SXSSFWorkbook workbook = excelConvertor.createExcel(data, fieldColumnList);

        if (data2 != null && modelClass2 != null) {
            // 找到导出列的信息 @Column
            List<FieldColumn> fieldColumnList2 = findFieldColumns(modelClass2, fileName);
            //追加一个sheet
            excelConvertor.appendCreateSheet(workbook, data2, fieldColumnList2);
        }

        // 输出excel
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.flush();
            workbook.write(outputStream);
        } catch (IOException e) {
            log.error("导出Excel异常 -- " + e.getMessage());
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (IOException e) {
                log.error("导出时关闭资源出错." + e.getMessage());
            }

            workbook.dispose();

            try {
                response.flushBuffer();
            } catch (IOException e) {
                log.error("导出时关闭资源出错." + e.getMessage());
            }
        }
    }


    /**
     * 找到将要导出的@Excel注解
     *
     * @param modelClass 实体类信息
     * @param fileName   将要导出的文件名, null表示只有一个@Excel,而我要的就是他
     * @param <T>        实体泛型
     * @return @Excel信息
     */
    private <T> Excel findExcel(Class<T> modelClass, String fileName) {
        Excel excelAnnotation = modelClass.getDeclaredAnnotation(Excel.class);
        // 合法性判断
        if (null == excelAnnotation) {
            throw new ExcelException("未知的导出实体,请先用@Excel注册");
        }

        if (null == fileName && excelAnnotation.value().length > 1) {
            throw new ExcelException("过多的@Excel.value注册, 请选择本次导出的文件名");
        }

        boolean isFix = false;
        if (null != fileName) {
            for (String excelFileName : excelAnnotation.value()) {
                if (fileName.equals(excelFileName)) {
                    isFix = true;
                    break;
                }
            }
        } else {
            isFix = true;
        }

        // 空值判断
        if (!isFix) {
            throw new ExcelException("未找到相匹配的@Excel,输入的文件名为:" + fileName);
        }

        return excelAnnotation;
    }

    /**
     * 找到类中需要导出的字段信息
     *
     * @param modelClass 实体类信息
     * @param <T>        实体泛型
     * @return 导出的列 列表
     */
    private <T> List<FieldColumn> findFieldColumns(Class<T> modelClass, String fileName) {
        List<FieldColumn> fieldColumnList = new ArrayList<>();

        Field[] fields = modelClass.getDeclaredFields();
        for (Field field : fields) {
            Column column = findColumn(field, fileName);
            if (null != column) {
                FieldColumn fieldColumn = createFieldColumn(modelClass, field, column);
                if (null != fieldColumn) {
                    fieldColumnList.add(fieldColumn);
                }
            }
        }
        // 根据index排序
        fieldColumnList.sort((f1, f2) -> f1.getIndex() - f2.getIndex());

        // 空值判断
        if (0 == fieldColumnList.size()) {
            throw new ExcelException("未找到相匹配的@Column,输入的文件名为:" + fileName);
        }
        return fieldColumnList;
    }

    /**
     * 在单个字段中
     * 找到@Column相匹配的字段
     *
     * @param field    字段信息
     * @param fileName 属于哪个导出文件, null表示不要判断了,有@Column就算
     * @return 相匹配的@Column信息, 如果null表示无匹配
     */
    private Column findColumn(Field field, String fileName) {
        Column[] columnAnnotations = field.getDeclaredAnnotationsByType(Column.class);

        if (columnAnnotations.length == 0) {
            return null;
        }
        if (null == fileName && columnAnnotations.length > 1) {
            throw new ExcelException("过多的@Column注册, 请选择本次要导出的文件名");
        }

        Column column;
        if (null == fileName) {
            column = columnAnnotations[0];
        } else {
            // 备选@Column列表
            List<Column> fixColumnList = new ArrayList<>(columnAnnotations.length);

            for (Column columnAnnotation : columnAnnotations) {
                String[] belongFiles = columnAnnotation.belong();
                if (belongFiles.length == 1 && "".equals(belongFiles[0])) {
                    fixColumnList.add(columnAnnotation);
                } else {
                    for (String belongFile : belongFiles) {
                        if (fileName.equals(belongFile)) {
                            fixColumnList.add(columnAnnotation);
                            break;
                        }
                    }
                }
            }

            // 找到最精确的@Column
            column = findMostFixColumn(fixColumnList);
        }

        return column;
    }

    /**
     * 从备选@Column列表中,找到最匹配的@Column,并返回
     * 匹配原则,越是精确的,优先级越高.
     * 即belong数量少的最高
     *
     * @param fixColumn 备选列表
     * @return 最精确的匹配, 无匹配时返回null
     */
    private Column findMostFixColumn(List<Column> fixColumn) {
        Column column;
        // 从备选列表中获得最精确的@Column
        if (fixColumn.size() == 1) {
            // 只有1个合适, 别犹豫,就是她,我的萌
            column = fixColumn.get(0);
        } else {
            // 多个合适时,找最精确的即belong信息最单一, 还是我的萌
            // 如果没有,得到的结果是null
            column = fixColumn.stream().min(
                    (c1, c2) -> {
                        if (c1.belong().length == 1 && "".equals(c1.belong()[0])) {
                            return 1;
                        } else if (c2.belong().length == 1 && "".equals(c2.belong()[0])) {
                            return -1;
                        } else if (c1.belong().length == c2.belong().length) {
                            throw new ExcelException("无法判断@Column精度,请修改使用方法");
                        } else {
                            return c1.belong().length - c2.belong().length;
                        }
                    }
            ).orElse(null);
        }
        return column;
    }

    /**
     * 创建字段对应的getter方法名
     *
     * @param field 字段信息
     * @return 对应的getter方法名
     */
    private String createMethodName(Field field) {
        String methodBegin = "get";
        if (field.getType() == boolean.class) {
            methodBegin = "is";
        }
        return methodBegin + (char) (field.getName().charAt(0) - 32) + field.getName().substring(1);
    }


    /**
     * 构造导出列信息
     *
     * @param modelClass 类型信息
     * @param field      对应字段
     * @param column     列注解
     * @param <T>        实体泛型
     * @return 导出列的信息
     */
    private <T> FieldColumn createFieldColumn(Class<T> modelClass, Field field, Column column) {
        try {
            Method getMethod = modelClass.getMethod(createMethodName(field));
            return new FieldColumn(
                    getMethod,
                    "".equals(column.value()) ? field.getName() : column.value(),
                    column.index()
            );
        } catch (NoSuchMethodException e) {
            log.warn("未找到字段 " + field.getName() + " 合法的getter方法, 类型:" + modelClass.toString());
        }

        return null;
    }
}
