package com.ringme.model.loyalty.voucher;

import com.ringme.enums.loyalty.LoyaltyStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class SubMerchant implements Serializable {
    private Long id;
    private Long idMerchant;
    private String name;
    private String address;
    private String ownerName;
    private String ownerPhoneNumber;
    private LoyaltyStatus status;
}
