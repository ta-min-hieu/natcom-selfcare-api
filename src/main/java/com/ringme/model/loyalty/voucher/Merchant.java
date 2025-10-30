package com.ringme.model.loyalty.voucher;

import com.ringme.enums.loyalty.LoyaltyStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class Merchant implements Serializable {
    private Long id;
    private String name;
    private String ownerName;
    private String ownerPhoneNumber;
    private LoyaltyStatus status;
    private String note;
}
