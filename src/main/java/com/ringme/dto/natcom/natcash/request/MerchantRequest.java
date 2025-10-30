package com.ringme.dto.natcom.natcash.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantRequest {
    private String username;
    private String password;
    private String requestId;
    private String partnerCode;
    private String orderNumber;
    private String signature;
}
