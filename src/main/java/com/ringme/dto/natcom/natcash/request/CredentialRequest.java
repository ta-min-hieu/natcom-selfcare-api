package com.ringme.dto.natcom.natcash.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CredentialRequest {
    private String requestId;
    private String partnerCode;
    private String username;
    private String password;
    private String deviceId;
    private String deviceModel;
    private String osName;
    private String osVersion;
    private String callbackUrl;
    private long timestamp;
    private String orderNumber;
    private BigDecimal amount;
    private String msisdn;
    private String signature;
    private boolean enableFee = false;
    private String language;
    private boolean skipPhoneInput = true;
}
