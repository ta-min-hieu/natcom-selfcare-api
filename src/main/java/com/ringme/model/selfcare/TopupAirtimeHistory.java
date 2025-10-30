package com.ringme.model.selfcare;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class TopupAirtimeHistory {
    private Long id;
    private String requestId;
    private BigDecimal amount;
    private String isdn;
    private Integer status;
    private String transactionId;
    private Integer errorCode;
    private String content;
    private String description;
    private String orderNumber;
    private Date createdAt;
    private Date createdDate;

    public TopupAirtimeHistory(String requestId, BigDecimal amount, String isdn, String transactionId, Integer errorCode, String content, String description, String orderNumber) {
        this.requestId = requestId;
        this.amount = amount;
        this.isdn = isdn;
        this.status = 0;
        this.transactionId = transactionId;
        this.errorCode = errorCode;
        this.content = content;
        this.description = description;
        this.orderNumber = orderNumber;
    }

    public TopupAirtimeHistory(String requestId, BigDecimal amount, String isdn) {
        this.requestId = requestId;
        this.amount = amount;
        this.isdn = isdn;
        this.status = 1;
    }
}
