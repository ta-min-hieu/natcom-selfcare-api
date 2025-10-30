package com.ringme.dto.natcom.natcash.request;

import com.ringme.enums.natcash.NatcashCallbackCode;
import lombok.Data;

@Data
public class CallbackRequest {
    private NatcashCallbackCode code;
    private String transId;
    private String orderNumber;
    private String signature;

    private String language;

    // share plan
    private String isdner;

    // topup airtime || payment mobile service, vas || share plan
    private String isdnee;

    // ftth
    private String ftthAccount;

    // payment mobile service, vas
    private String packageCode;

    public String getLanguage() {
        if(language == null || language.equals("ht"))
            return "ht";

        return language;
    }
}
