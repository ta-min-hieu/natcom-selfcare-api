package com.ringme.dto.natcom.selfcare.response.packages;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubPackage implements Serializable {
    private String code;
    private String price;
    private String priceOrigin;
    private String priceNatcash;
    private String taxNatcash;
    private String discountNatcash = "0.00";
    private String unit;
    private Integer type; // 1. normal, 2. auto renew, 3. share
    private boolean isPaymentMINatcash = true;

    public void setTypeAndPaymentMINatcash(Integer isAutorenew, Integer isGift) {
        if(isAutorenew != null && isAutorenew == 1) {
            type = 2;
            isPaymentMINatcash = false;
        } else if (isGift != null && isGift == 1)
            type = 3;
        else {
            type = 1;
//            isPaymentMINatcash = true;
        }
    }

    public String getPriceOrigin() {
        return price;
    }

    public String getPriceNatcash() {
        if (price == null) return null;
        try {
            BigDecimal p = new BigDecimal(price);
            BigDecimal total = p.multiply(BigDecimal.valueOf(1.1));
            return total.setScale(2, RoundingMode.HALF_UP).toPlainString();
        } catch (NumberFormatException e) {
            return "0.00";
        }
    }

    public String getTaxNatcash() {
        if (price == null) return null;
        try {
            BigDecimal p = new BigDecimal(price);
            BigDecimal tax = p.multiply(BigDecimal.valueOf(0.1));
            return tax.setScale(2, RoundingMode.HALF_UP).toPlainString();
        } catch (NumberFormatException e) {
            return "0.00";
        }
    }
}
