package com.ringme.dto.natcom.natcash.response;

import lombok.Data;

@Data
public class PaymentStatusData {
    private String amount;
    private String orderNumber;
    private Integer responseCode;
    private String toPhone;
    private String transId;
}
