package com.eric.util.excel;

import java.lang.reflect.Method;

/**
 * Description: 列信息资源
 * author: Eric
 * Date: 18/8/6
 */
public class FieldColumn {
    /**
     * 列对应的实体中的getter方法, 如 getName()
     */
    private Method getter;
    /**
     * Excel中的列名,即表头
     */
    private String columnName;
    /**
     * 顺序索引
     */
    private int index;

    public FieldColumn(Method getter, String columnName, int index) {
        this.getter = getter;
        this.columnName = columnName;
        this.index = index;
    }


    public Method getGetter() {
        return getter;
    }

    public void setGetter(Method getter) {
        this.getter = getter;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "FieldColumn{" +
                "getter=" + getter +
                ", columnName='" + columnName + '\'' +
                ", index=" + index +
                '}';
    }
}