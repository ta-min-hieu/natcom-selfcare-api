package com.ringme.dto.natcom.selfcare.response.packages;

import lombok.Data;

@Data
public class FtthPackage {
    private String id;
    private String productCode;
    private String productName;
    private String description;
    private String groupProduct;
    private String iconUrl;
    private Integer price;
    private Integer speed;
}
