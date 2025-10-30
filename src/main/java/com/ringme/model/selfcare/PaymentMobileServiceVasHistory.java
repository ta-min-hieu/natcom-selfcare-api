package com.ringme.model.selfcare;

import com.ringme.dto.natcom.ftth.FtthResponse;
import com.ringme.dto.natcom.selfcare.response.SelfcareResponse;
import lombok.Data;

import java.util.Date;

@Data
public class PaymentMobileServiceVasHistory {
    private Long id;
    private String isdn;
    private String packageCode;
    private Double money;
    private String errorCode;
    private String orderNumber;
    private String userMsg;
    private Date createdAt;

    public PaymentMobileServiceVasHistory(SelfcareResponse<Object> response, String orderNumber, String isdnee, String packageCode, double money) {
        if(response != null) {
            errorCode = response.getErrorCode();
            userMsg = response.getUserMsg();
        }

        isdn = isdnee;
        this.packageCode = packageCode;
        this.money = money;
        this.orderNumber = orderNumber;
    }
}
