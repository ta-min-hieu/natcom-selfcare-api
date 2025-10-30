package com.ringme.model.selfcare;

import com.ringme.dto.natcom.ftth.FtthResponse;
import lombok.Data;

@Data
public class FtthPaymentHistory {
    private Long id;
    private String ftthAccount;
    private Double moneyDolar;
    private Double moneyHaiti;
    private Integer errorCode;
    private String orderNumber;
    private String content;

    public FtthPaymentHistory(FtthResponse response, String orderNumber, String ftthAccount, double moneyDolar, double moneyHaiti) {
        this.orderNumber = orderNumber;
        this.moneyDolar = moneyDolar;
        this.moneyHaiti = moneyHaiti;
        this.ftthAccount = ftthAccount;
        this.content = response.getContent();
        this.errorCode = response.getErrorCode();
    }
}
