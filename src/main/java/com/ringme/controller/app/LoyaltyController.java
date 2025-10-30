package com.ringme.controller.app;

import com.ringme.dto.record.Response;
import com.ringme.enums.loyalty.LoyaltyStatus;
import com.ringme.enums.loyalty.gift.*;
import com.ringme.enums.loyalty.PointTransactionType;
import com.ringme.enums.loyalty.PointTypeId;
import com.ringme.service.natcom.NatcomLoyaltyService;
import com.ringme.service.ringme.JwtService;
import com.ringme.service.ringme.RingmeLoyaltyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Log4j2
@RestController
@RequestMapping("/loyalty")
public class LoyaltyController {
    @Autowired
    JwtService jwtService;
    @Autowired
    NatcomLoyaltyService natcomLoyaltyService;
    @Autowired
    RingmeLoyaltyService ringmeLoyaltyService;

    @PostMapping("/my-gift")
    public ResponseEntity<?> myGift(@RequestParam MyGiftStatus status) {
        String isdn = jwtService.getUsernameFromJwt();
        return ResponseEntity.ok(new Response(200, "Success", ringmeLoyaltyService.getMyGifts(isdn, status)));
    }

    @PostMapping("/get-souvenirs")
    public ResponseEntity<?> getSouvenirs() {
        return ResponseEntity.ok(new Response(200, "Success", ringmeLoyaltyService.getSouvenirs()));
    }

    @PostMapping("/get-souvenir-by-id")
    public ResponseEntity<?> getSouvenirById(@RequestParam long id) {
        return ResponseEntity.ok(new Response(200, "Success", ringmeLoyaltyService.getSouvenirById(id)));
    }

    @PostMapping("/exchange-souvenir")
    public ResponseEntity<?> exchangeSouvenir(HttpServletRequest request,
                                              @RequestParam("souvenirId") long souvenirReviewId,
                                              @RequestParam(required = false) String language,
                                              @RequestParam String shopId) {
        String isdn = jwtService.getUsernameFromJwt();
        String vtAccId = jwtService.extractVtAccId(request);

        return ResponseEntity.ok(ringmeLoyaltyService.exchangeSouvenir(language, isdn, vtAccId, souvenirReviewId, shopId));
    }

    @PostMapping("/get-voucher-topics")
    public ResponseEntity<?> getVoucherTopics() {
        return ResponseEntity.ok(new Response(200, "Success", ringmeLoyaltyService.getVoucherTopics()));
    }

    @PostMapping("/get-vouchers-by-topic")
    public ResponseEntity<?> getVouchers(@RequestParam String language,
                                         @RequestParam int topicId) {
        return ResponseEntity.ok(ringmeLoyaltyService.getVoucherGroupsByTopicId(language, topicId));
    }

    @PostMapping("/get-voucher-by-id")
    public ResponseEntity<?> getVoucherById(@RequestParam long id) {
        return ResponseEntity.ok(ringmeLoyaltyService.getVoucherGroupsById(id));
    }

    @PostMapping("/exchange-voucher")
    public ResponseEntity<?> exchangeVoucher(HttpServletRequest request,
                                             @RequestParam int topicId,
                                             @RequestParam(required = false) String language,
                                             @RequestParam("voucherId") long voucherGroupId) {
        String isdn = jwtService.getUsernameFromJwt();
        String vtAccId = jwtService.extractVtAccId(request);

        return ResponseEntity.ok(ringmeLoyaltyService.exchangeVoucher(language, isdn, vtAccId, voucherGroupId, topicId));
    }

    @PostMapping("/redeem-point-lottery-code")
    public ResponseEntity<?> redeemPointLotteryCode() {
        String isdn = jwtService.getUsernameFromJwt();
        return ResponseEntity.ok(new Response(200, "Success", natcomLoyaltyService.redeemPointLotteryCode(isdn)));
    }

    @PostMapping("/find-store-by-addr")
    public ResponseEntity<?> FindStoreByAddr(@RequestParam(required = false) String longitude,
                                             @RequestParam(required = false) String latitude,
                                             @RequestParam(required = false) String provinceId,
                                             @RequestParam(required = false) String districtId) {
        return ResponseEntity.ok(new Response(200, "Success", ringmeLoyaltyService.findStoreHasGiftByAddr(longitude, latitude, provinceId, districtId)));
    }

    @PostMapping("/get-customer-info")
    public ResponseEntity<?> getCustomerInfo(HttpServletRequest request) {
        String isdn = jwtService.getUsernameFromJwt();
        String vtAccId = jwtService.extractVtAccId(request);
        return ResponseEntity.ok(new Response(200, "Success", ringmeLoyaltyService.getCustomerInfo(isdn, vtAccId)));
    }

    @PostMapping("/get-point-transfer-history")
    public ResponseEntity<?> getPointTransferHistory(
//            @RequestParam(required = false) LocalDate fromDate,
//            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) Long offset,
            @RequestParam(required = false) Long limit,
            @RequestParam(required = false) PointTransactionType type,
            @RequestParam(required = false) PointTypeId pointId
            ) {
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = toDate.minusDays(30);

        String isdn = jwtService.getUsernameFromJwt();
        return ResponseEntity.ok(new Response(200, "Success", natcomLoyaltyService.getPointTransferHistory(isdn, null, null, null, fromDate, toDate, offset, limit, type, pointId)));
    }

    @PostMapping("/get-rank-define-info")
    public ResponseEntity<?> getRankDefineInfo(
            @RequestParam(required = false) String tenant
            ) {
        return ResponseEntity.ok(new Response(200, "Success", natcomLoyaltyService.getRankDefineInfo(tenant)));
    }

    @PostMapping("/get-gift-info")
    public ResponseEntity<?> getGiftInfo(
            @RequestParam(required = false) GiftId giftId,
            @RequestParam(required = false) GiftCode giftCode,
            @RequestParam(required = false, defaultValue = "1") Long giftType,
            @RequestParam(required = false) GiftName giftName,
            @RequestParam(required = false, defaultValue = "1") Long point,
            @RequestParam(required = false) LoyaltyStatus status
            ) {
        return ResponseEntity.ok(new Response(200, "Success", natcomLoyaltyService.getGiftInfo(giftId, giftCode, giftType, giftName, point, status)));
    }
}
