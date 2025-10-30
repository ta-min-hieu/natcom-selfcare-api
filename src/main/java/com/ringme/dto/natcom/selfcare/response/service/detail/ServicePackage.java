package com.ringme.dto.natcom.selfcare.response.service.detail;

import lombok.Data;

@Data
public class ServicePackage {
    private String name;
    private String code;
    private String shortDes;
    private String fullDes;
    private String iconUrl;
    private String price;
    private String regisState;
    private String unit;
    private boolean isPaymentMINatcash = false;
}
