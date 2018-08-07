package com.eric.util.model;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;


/**
 * description:Json扩展实体类
 * author:Eric
 * Date:16/8/25
 * Time:16:02
 * version 1.0.0
 */
public class JsonResult extends HashMap<String, Object> {

    private JsonResult() {
    }

    public JsonResult(JsonResult.STATUS status) {
        this(status, "");
    }

    public JsonResult(JsonResult.STATUS status, String message) {
        this.put("status", status.name().toLowerCase()).put("message", message);
    }

    /**
     * 添加常规结果集
     * @param key
     * @param value
     * @return
     */
    public JsonResult put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * 添加对象结果集
     * @param baseEntity
     * @return
     */
    public JsonResult put(BaseEntity baseEntity) {
        super.put(StringUtils.uncapitalize(baseEntity.getClass().getSimpleName()), baseEntity);
        return this;
    }

    /**
     * 将JsonResult转换为json字符串
     * @return
     */
    public String toJson() {
        StringWriter jsonWriter = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(jsonWriter, this);
            return jsonWriter.toString();
        } catch (IOException var4) {
            var4.printStackTrace();
            return "";
        }
    }

    public String toJson(String callback) {
        return StringUtils.isNotBlank(callback)?this.toJson():callback + "(" + this.toJson() + ")";
    }

    public JsonResult clone() {
        return (JsonResult)super.clone();
    }

    public String toString() {
        return this.toJson();
    }


    public static enum STATUS {
        OK,
        FAIL;

        private STATUS() {

        }
    }
}