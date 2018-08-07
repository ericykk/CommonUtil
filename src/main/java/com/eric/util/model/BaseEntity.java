package com.eric.util.model;

import java.io.Serializable;

/**
 * 基础实体类
 *
 */
public class BaseEntity implements Serializable {
    protected static final long serialVersionUID = -3837567657603261711L;
    private int pageNo = 0;
    private int pageSize = 25;
    private String sql;
    private String orderBy;

    public BaseEntity() {
    }

    public int getPageNo() {
        return this.pageNo;
    }

    public void setPageNo(int pageNo) {
        if(pageNo <= 0) {
            this.pageNo = 0;
        } else {
            this.pageNo = pageNo - 1;
        }

    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
