package com.ringme.dto.natcom.loyalty;

import lombok.Data;

@Data
public class GiftDTO {
    private Long giftId;
    private String giftCode;
    private Integer giftType;
    private String giftName;
    private Double giftCost;
    private Integer point;
    private Integer status;
    private String giftUnit;
    private Integer tenant;
    private Double giftCostBase;
    private String giftUnitBase;
}
