/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

public enum FieldTypeEnum {
    STRING("String", "String"),
    INT("int", "int"),
    SHORT("short", "short"),
    BOOLEAN("boolean", "boolean"),
    FLOAT("float", "float"),
    DOUBLE("double", "double"),
    LONG("long", "long"),
    CHAR("char", "char");


    private String code;
    private String value;

    FieldTypeEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    /**
     * 根据字符串获取枚举类
     *
     * @param name
     * @return
     */
    public static FieldTypeEnum getFieldTypeByValue(String name) {
        for (FieldTypeEnum operateTypeEnum : values()) {
            if (name.equals(operateTypeEnum.getValue())) {
                return operateTypeEnum;
            }
        }
        return null;
    }


    /**
     * 根据字符串获取枚举类
     *
     * @param name
     * @return
     */
    public static FieldTypeEnum getFieldTypeByCode(String name) {
        for (FieldTypeEnum operateTypeEnum : values()) {
            if (name.equals(operateTypeEnum.getCode())) {
                return operateTypeEnum;
            }
        }
        return null;
    }

    /**
     * Getter method for property <tt>code</tt>.
     *
     * @return property value of code
     */
    public String getCode() {
        return code;
    }

    /**
     * Setter method for property <tt>counterType</tt>.
     *
     * @param code value to be assigned to property code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Getter method for property <tt>value</tt>.
     *
     * @return property value of value
     */
    public String getValue() {
        return value;
    }

    /**
     * Setter method for property <tt>counterType</tt>.
     *
     * @param value value to be assigned to property value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
