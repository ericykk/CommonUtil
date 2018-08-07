package com.eric.util.excel;

import java.lang.annotation.*;

/**
 * Description:excel导出列注解
 * author: Eric
 * Date: 18/8/6
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Columns.class)
public @interface Column {

    /**
     * excel列名,就是表头名称
     */
    String value() default "";

    /**
     * 导出列表排序,按照从小到大排序, 默认按实体字段顺序排序
     */
    int index() default 0;

    /**
     * 属于哪个@Excel, 填@Excel.value
     */
    String[] belong() default "";
}
