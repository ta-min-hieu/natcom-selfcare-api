package com.ringme.dto.natcom.natcash.response;

import com.ringme.enums.natcash.NatcashResponseStatus;
import lombok.Data;

@Data
public class MerchantResponse {
    private NatcashResponseStatus status;
    private String code;
    private String message;
    private PaymentStatusData data;
}
