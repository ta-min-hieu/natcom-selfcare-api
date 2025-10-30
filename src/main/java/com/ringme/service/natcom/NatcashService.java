package com.ringme.service.natcom;

import com.ringme.dto.natcom.natcash.request.CallbackRequest;
import com.ringme.dto.natcom.natcash.request.WebviewRequestDto;
import com.ringme.dto.natcom.natcash.response.MerchantResponse;
import com.ringme.dto.record.Response;
import com.ringme.enums.natcash.NatcashCallbackType;
import org.springframework.ui.Model;

public interface NatcashService {
    Response credentialHandler(WebviewRequestDto dto);

    MerchantResponse checkTransaction(NatcashCallbackType type, String requestId, String orderNumber);

    MerchantResponse cancelTrans(NatcashCallbackType type, String requestId, String orderNumber);

    String callbackHandler(Model model, CallbackRequest request, NatcashCallbackType natcashCallbackType);

    boolean topupAirtime(String isdn, String amount, String orderNumber, String isdner);
}
