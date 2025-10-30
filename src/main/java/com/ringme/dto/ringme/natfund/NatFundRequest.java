package com.ringme.dto.ringme.natfund;

import lombok.Data;

@Data
public class NatFundRequest {
    private String userName;
    private String password;
    private String msisdn;
    private String addBalance;
    private String source;

    public void setMsisdn(String isdn) {
        msisdn = "509" + isdn;
    }
}
