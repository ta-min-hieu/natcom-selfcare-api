package com.ringme.dto.natcom.selfcare.response;

import lombok.Data;

@Data
public class StoreInfo {
    private String id;
    private String name;
    private String addr;
    private String openTime;
    private String provinceName;
    private String districtName;
    private String isdn;
    private Double distance;
    private Double latitude;
    private Double longitude;
    private String provinceId;
    private String districtId;
    private String storeId;
}
