package com.ringme.dto.natcom.natcash.response;

import com.ringme.enums.natcash.NatcashResponseStatus;
import lombok.Data;

@Data
public class CredentialResponse {
    private String code;
    private int expiredAt;
    private String message;
    private NatcashResponseStatus status;
    private String url;
}
