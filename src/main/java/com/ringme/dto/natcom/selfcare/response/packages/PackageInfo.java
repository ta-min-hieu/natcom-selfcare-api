package com.ringme.dto.natcom.selfcare.response.packages;

import lombok.Data;

import java.util.List;

@Data
public class PackageInfo {
    private String serviceId;
    private String name;
    private String code;
    private String fullDes;
    private String imgDesUrl;
    private String price;
    private String unit;
    private List<SubPackage> subServices;
}
