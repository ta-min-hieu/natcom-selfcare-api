package com.ringme.dto.natcom.selfcare.response.service.detail;

import lombok.Data;

import java.util.List;

@Data
public class ServiceDetail {
    private String name;
    private String code;
    private String fullDes;
    private String imgDesUrl;
    private String webLink;
    private String price;
    private int isRegisterAble;
    private List<ServicePackage> packages;
    private boolean isPaymentMINatcash = false;
}
