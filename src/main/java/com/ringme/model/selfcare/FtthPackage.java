package com.ringme.model.selfcare;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FtthPackage implements Serializable {
    private String id;
    private String productCode;
    private String productName;
    private String description;
    private String groupProduct;
    private String iconUrl;
    private String price;
    private String speed;
}
