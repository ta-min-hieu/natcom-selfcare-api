package com.ringme.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Shop implements Serializable {
    private String id;
    private String name;
    private String addr;
    private String openTime;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer provinceId;
    private String provinceName;
    private Integer districtId;
    private String districtName;
    private String isdn;
    private Integer type;
    private Integer status;
    private Date createdTime;
    private String createdBy;
    private Date lastUpdatedTime;
    private String lastUpdatedBy;
    private Integer la;
    private Integer lo;
    private Integer shopOrder;
}
