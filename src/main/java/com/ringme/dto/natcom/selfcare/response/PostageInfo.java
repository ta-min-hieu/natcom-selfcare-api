package com.ringme.dto.natcom.selfcare.response;

import lombok.Data;

@Data
public class PostageInfo {
    private Integer monthlyFee;
    private Integer basic;
    private Integer prom;
    private Integer callFee;
    private Integer smsFee;
    private Integer otherFee;
    private Integer dataFee;
    private Integer vasFee;
    private Integer callRc;
    private Integer smsRc;
    private Integer otherRc;
    private Integer dataRc; // nullable
    private Integer vasRc;  // nullable
}
