package com.ringme.controller.app;

import com.ringme.common.Common;
import com.ringme.common.Helper;
import com.ringme.dto.natcom.natcash.request.CallbackRequest;
import com.ringme.dto.natcom.natcash.request.WebviewRequestDto;
import com.ringme.dto.natcom.natcash.response.MerchantResponse;
import com.ringme.dto.record.Response;
import com.ringme.enums.natcash.NatcashCallbackType;
import com.ringme.enums.natcash.NatcashResponseStatus;
import com.ringme.enums.selfcare.LogCdr;
import com.ringme.service.natcom.NatcashService;
import com.ringme.service.ringme.JwtService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Controller
@RequestMapping("/natcash")
public class NatcashController {
    @Autowired
    NatcashService natcashService;
    @Autowired
    JwtService jwtService;

    @PostMapping("/credential/share-plan")
    public ResponseEntity<?> credentialSharePlan(@ModelAttribute WebviewRequestDto dto) {
        if(dto.getPackageCode() == null || dto.getPackageCode().isEmpty()) {
            log.error("package code is empty, dto: {}", dto);
            return ResponseEntity.ok(new Response(1, "Package code is not empty"));
        }
        if(dto.getIsdnee() == null || dto.getIsdnee().isEmpty()) {
            log.error("isdnee is empty, dto: {}", dto);
            return ResponseEntity.ok(new Response(2, "Isdnee is not empty"));
        }

        dto.setType(NatcashCallbackType.SHARE_PLAN);

        return ResponseEntity.ok(natcashService.credentialHandler(dto));
    }

    @GetMapping("/share-plan/call-back")
    public String sharePlanCallback(Model model, @ModelAttribute CallbackRequest request) {
        log.info("request: {}", request);
        return natcashService.callbackHandler(model, request, NatcashCallbackType.SHARE_PLAN);
    }

    @PostMapping("/credential/payment-mobile-service-vas")
    public ResponseEntity<?> credentialPaymentMobileServiceVas(@ModelAttribute WebviewRequestDto dto) {
        if(dto.getPackageCode() == null || dto.getPackageCode().isEmpty()) {
            log.error("package code is empty, dto: {}", dto);
            return ResponseEntity.ok(new Response(1, "Package code is not empty"));
        }

        dto.setType(NatcashCallbackType.MOBILE_SERVICE_VAS);

        return ResponseEntity.ok(natcashService.credentialHandler(dto));
    }

    @GetMapping("/payment-mobile-service-vas/call-back")
    public String paymentMobileServiceVasCallback(Model model, @ModelAttribute CallbackRequest request) {
        log.info("request: {}", request);
        return natcashService.callbackHandler(model, request, NatcashCallbackType.MOBILE_SERVICE_VAS);
    }

    @PostMapping("/credential/top-up/airtime")
    public ResponseEntity<?> credentialTopupAirtime(@ModelAttribute WebviewRequestDto dto) {
        if(dto.getIsdnee() == null || dto.getIsdnee().isEmpty()) {
            log.error("isdnee is empty, dto: {}", dto);
            return ResponseEntity.ok(new Response(1, "Isdnee is not empty"));
        }

        dto.setType(NatcashCallbackType.TOPUP_AIRTIME);

        return ResponseEntity.ok(natcashService.credentialHandler(dto));
    }

    @GetMapping("/top-up/airtime/call-back")
    public String topupAirtimeCallback(Model model, @ModelAttribute CallbackRequest request) {
        log.info("request: {}", request);
        return natcashService.callbackHandler(model, request, NatcashCallbackType.TOPUP_AIRTIME);
    }

    @PostMapping("/credential/ftth")
    public ResponseEntity<?> credentialFTTH(@ModelAttribute WebviewRequestDto dto) {
        if(dto.getFtthAccount() == null || dto.getFtthAccount().isEmpty()) {
            log.error("ftthAccount is empty, dto: {}", dto);
            return ResponseEntity.ok(new Response(1, "ftthAccount is not empty"));
        }

        dto.setType(NatcashCallbackType.FTTH);

        return ResponseEntity.ok(natcashService.credentialHandler(dto));
    }

    @GetMapping("/ftth/call-back")
    public String ftthCallback(Model model, @ModelAttribute CallbackRequest request) {
        log.info("request: {}", request);
        return natcashService.callbackHandler(model, request, NatcashCallbackType.FTTH);
    }

    @PostMapping("/test/top-up/airtime")
    public ResponseEntity<?> testTopupAirtime(@RequestParam String amount) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("Natcash topup airtime| isdn: {}, amount: {}", isdn, amount);

        if(!Common.whiteList.contains(isdn)) {
            log.warn("Phone is not in white list");
            return ResponseEntity.ok(new Response(400, "Bad Request"));
        }

        if(natcashService.topupAirtime(isdn, amount, "test-" + System.currentTimeMillis(), isdn))
            return ResponseEntity.ok(new Response(200, "Success"));
        else
            return ResponseEntity.ok(new Response(400, "Bad Request"));
    }

    @PostMapping("/test/credential")
    public ResponseEntity<?> testCredential(@ModelAttribute WebviewRequestDto dto) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("Natcash topup airtime| dto: {}", dto);

        if(!Common.whiteList.contains(isdn)) {
            log.warn("Phone is not in white list");
            return ResponseEntity.ok(new Response(400, "Bad Request"));
        }
        dto.setType(NatcashCallbackType.TOPUP_AIRTIME);

        return ResponseEntity.ok(natcashService.credentialHandler(dto));
    }

    @PostMapping("/test/check-transaction")
    public ResponseEntity<?> testCheckTransaction(@RequestParam String requestId,
                                                  @RequestParam String orderNumber,
                                                  @RequestParam NatcashCallbackType type) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("Natcash topup airtime| isdn: {}, requestId: {}, orderNumber: {}, type: {}", isdn, requestId, orderNumber, type);

        if(!Common.whiteList.contains(isdn)) {
            log.warn("Phone is not in white list");
            return ResponseEntity.ok(new Response(400, "Bad Request"));
        }

        return ResponseEntity.ok(natcashService.checkTransaction(type, requestId, orderNumber));
    }

    @PostMapping("/test/cancel-trans")
    public ResponseEntity<?> testCancelTransaction(@RequestParam String requestId,
                                                   @RequestParam String orderNumber,
                                                   @RequestParam String amount,
                                                   @RequestParam(required = false) NatcashCallbackType type) {
        String isdn = jwtService.getUsernameFromJwt();
        if(type == null)
            type = NatcashCallbackType.MOBILE_SERVICE_VAS;
        log.info("Natcash topup airtime| isdn: {}, requestId: {}, orderNumber: {}", isdn, requestId, orderNumber);

        if(!Common.whiteList.contains(isdn)) {
            log.warn("Phone is not in white list");
            return ResponseEntity.ok(new Response(400, "Bad Request"));
        }

        MerchantResponse m = natcashService.cancelTrans(type, requestId, orderNumber);

        if(m != null && m.getStatus().equals(NatcashResponseStatus.MSG_SUCCESS))
            Common.logCdr(LogCdr.CMS_REFUND.getType(), "CMS", "", amount, orderNumber);

        return ResponseEntity.ok(m);
    }

    @PostMapping("/test/cancel-trans-v2")
    public ResponseEntity<?> testCancelTransactionV2(@RequestParam String requestId,
                                                     @RequestParam String orderNumber,
                                                     @RequestParam String amount,
                                                     @RequestParam(required = false) NatcashCallbackType type) {
        if(type == null)
            type = NatcashCallbackType.MOBILE_SERVICE_VAS;
        log.info("testCancelTransactionV2|, requestId: {}, orderNumber: {}", requestId, orderNumber);

        MerchantResponse m = natcashService.cancelTrans(type, requestId, orderNumber);

        if(m != null && m.getStatus().equals(NatcashResponseStatus.MSG_SUCCESS))
            Common.logCdr(LogCdr.CMS_REFUND.getType(), "CMS", "", amount, orderNumber);

        return ResponseEntity.ok(m);
    }
}
