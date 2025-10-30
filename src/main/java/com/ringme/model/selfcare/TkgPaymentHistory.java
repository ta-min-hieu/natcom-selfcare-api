package com.ringme.model.selfcare;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TkgPaymentHistory {
    private Long id;
    private String type;
    private String isdnee;
    private String sharerIsdn;
    private String serviceCode;
    private BigDecimal price;
    private Integer status;

    public TkgPaymentHistory(String type, String isdnee, String sharerIsdn, String serviceCode, BigDecimal price, Integer status) {
        this.type = type;
        this.isdnee = isdnee;
        this.sharerIsdn = sharerIsdn;
        this.serviceCode = serviceCode;
        this.price = price;
        this.status = status;
    }
}
