package com.tool.dto;

/**
 * Created by bzheng on 2018/3/30.
 */
public class Body {

    /**
     * 变量名（id）
     */
    private String name;

    /**
     * 数据类型 （integer）
     */
    private String type;

    /**
     * 说明（ID）
     */
    private String remark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
