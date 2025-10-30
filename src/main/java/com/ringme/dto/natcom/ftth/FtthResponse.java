package com.ringme.dto.natcom.ftth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FtthResponse {
    private String address;
    private String content;
    private String contractNo;
    private Double contractDebt;
    private Integer errorCode;
    private Double hotcharge;
    private Double monthlyFee;
    private String payer;
    private Double payment;
    private String productCode;
    private Double remainPayment;
    private String service;
    private String status;
    private String subId;
    private String telFax;
}
