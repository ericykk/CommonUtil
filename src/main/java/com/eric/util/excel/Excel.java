package com.eric.util.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:Excel注解
 * author: Eric
 * Date: 18/8/6
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Excel {

    /**
     * 导出文件名
     *
     * @return
     */
    String[] value() default "默认导出表";

    /**
     * 最大导出条数
     */
    int limit() default 1040000;
}
