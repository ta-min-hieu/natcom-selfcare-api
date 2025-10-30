package com.ringme.dto.natcom.loyalty;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class AccountPointDto {
    private Long accountPointId;
    private Long pointId;
    private Long vtAccId;
    private Double pointValue;
    private ZonedDateTime pointExpireDate;
    private Long productId;
    private String productName;
    private ZonedDateTime createDate;
    private String pointName;
    private Integer pointType;
    private Integer status;
    private String description;
}
