package com.ringme.model.selfcare;

import com.ringme.dto.natcom.selfcare.response.SelfcareResponse;
import lombok.Data;

import java.util.Date;

@Data
public class NatcashSharePlanHistory {
    private Long id;
    private String isdner;
    private String isdnee;
    private String packageCode;
    private Double money;
    private String errorCode;
    private String orderNumber;
    private String userMsg;
    private Date createdAt;

    public NatcashSharePlanHistory(SelfcareResponse<Object> response, String orderNumber, String isdner, String isdnee, String packageCode, double money) {
        if(response != null) {
            errorCode = response.getErrorCode();
            userMsg = response.getUserMsg();
        }

        this.isdner = isdner;
        this.isdnee = isdnee;
        this.packageCode = packageCode;
        this.money = money;
        this.orderNumber = orderNumber;
    }
}
