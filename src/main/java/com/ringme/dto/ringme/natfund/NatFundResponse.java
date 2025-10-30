package com.ringme.dto.ringme.natfund;

import lombok.Data;

@Data
public class NatFundResponse {
    private String msisdn;
    private String errorCode;
    private String description;
}
