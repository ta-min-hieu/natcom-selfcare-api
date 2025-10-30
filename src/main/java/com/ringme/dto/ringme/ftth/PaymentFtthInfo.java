package com.ringme.dto.ringme.ftth;

import lombok.Data;

@Data
public class PaymentFtthInfo {
    private String userUsing;
    private String ftthAccount;
    private Double priorDebit;
    private Double ariseDebit;
    private Double hotCharge;
    private Double paidAmount;
    private Double prepaid;
    private Double toPay;
}
