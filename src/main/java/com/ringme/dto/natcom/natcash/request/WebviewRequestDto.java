package com.ringme.dto.natcom.natcash.request;

import com.ringme.enums.natcash.NatcashCallbackType;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class WebviewRequestDto {
    private String deviceId;
    private String deviceModel;
    private String osName;
    private String osVersion;
    private BigDecimal amount;

    NatcashCallbackType type;

    private String language;

    // topup airtime, share plan
    private String isdnee;

    // ftth
    private String ftthAccount;

    // mobile service and vas
    private String packageCode;

    public String getLanguage() {
        if(language == null || language.equals("ht"))
            return "ht";
        return "en";
    }
}
