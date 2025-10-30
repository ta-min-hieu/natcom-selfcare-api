package com.ringme.model.loyalty.voucher;

import com.ringme.enums.loyalty.LoyaltyStatus;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VoucherGroup implements Serializable {
    private Long id;
    private Long topicId;
    private String topicName;
    private Long idMerchant;
    private String merchantName;
    private LoyaltyStatus status;
    private String name;
    private Integer quantityTotal;
    private Integer quantityExchanged;
    private Integer maxPoint;
    private Date startDate;
    private Date endDate;
    private String description;
    private String imageUrl;
    private Double discountAmount;
    private String pointUnit;
    private String discountUnit;
}
