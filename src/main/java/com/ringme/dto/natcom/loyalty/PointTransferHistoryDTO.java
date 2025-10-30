package com.ringme.dto.natcom.loyalty;

import lombok.Data;

import java.util.Date;

@Data
public class PointTransferHistoryDTO {
    private Long hisId;
    private Long vtAccId;
    private Date createDate;
    private Double amount;
    private String userTransfer;
    private Long transferType;
    private Long status;
    private String productName;
    private Long productId;
    private String transferTypeName;
    private Long pointId;
}
