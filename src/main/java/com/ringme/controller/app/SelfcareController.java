package com.ringme.controller.app;

import com.ringme.config.AppConfig;
import com.ringme.config.LocaleFactory;
import com.ringme.dto.record.Response;
import com.ringme.dto.ringme.ftth.FtthRegister;
import com.ringme.enums.OTPKey;
import com.ringme.enums.selfcare.FtthRegisterStatus;
import com.ringme.enums.selfcare.*;
import com.ringme.enums.selfcare.mobileServices.MobileServices;
import com.ringme.model.selfcare.ScheduleCall;
import com.ringme.service.natcom.FtthService;
import com.ringme.service.natcom.NatcomSelfcareService;
import com.ringme.service.ringme.JwtService;
import com.ringme.service.ringme.NatFundService;
import com.ringme.service.ringme.OTPService;
import com.ringme.service.ringme.RingmeSelfcareService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/selfcare")
public class SelfcareController {
    @Autowired
    RingmeSelfcareService service;
    @Autowired
    NatcomSelfcareService natcomSelfcareService;
    @Autowired
    JwtService jwtService;
    @Autowired
    AppConfig appConfig;
    @Autowired
    RingmeSelfcareService ringmeSelfcareService;
    @Autowired
    FtthService ftthService;
    @Autowired
    NatFundService natFundService;
    @Autowired
    OTPService otpService;
    @Autowired
    LocaleFactory localeFactory;

    @PostMapping("/request-otp")
    public ResponseEntity<?> requesetOTP(
            @RequestParam(required = false, defaultValue = "en") String language,
            @RequestParam OTPKey type
    ) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}", isdn);
        if(otpService.generateOTP(type, language))
            return ResponseEntity.ok(new Response(200, localeFactory.getMessage("success", language), 60));
        else
            return ResponseEntity.ok().body(new Response(1, "Generate OTP failed"));
    }

    @PostMapping("/get-banners")
    public ResponseEntity<?> getBanner() {
        return ResponseEntity.ok(new Response(200, "Success", service.getBanners()));
    }

    @PostMapping("/get-account-info")
    public ResponseEntity<?> getAccountInfo(@RequestParam String language,
                                            HttpServletRequest request) {
        String isdn = jwtService.getUsernameFromJwt();
        String vtAccId = jwtService.extractVtAccId(request);
        log.info("REQ: {}, {}, {}", isdn, vtAccId, language);
        return ResponseEntity.ok(new Response(200, localeFactory.getMessage("success", language), service.getAccountInfo(isdn, language, vtAccId)));
    }

    @PostMapping("/get-account-detail")
    public ResponseEntity<?> getAccountDetail(
            HttpServletRequest request,
            @RequestParam SubType subType,
            @RequestParam String language
    ) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("{}, subType={}, language={}", isdn, subType, language);
        String name = jwtService.extractName(request);
        return ResponseEntity.ok(new Response(200, "Success", service.getAccountInfoDetail(isdn, language, subType, name)));
    }

    @PostMapping("/payment-data-vas")
    public ResponseEntity<?> paymentDataVas(@RequestParam String language,
                                            @RequestParam String serviceCode,
                                            @RequestParam ActionType actionType, // 0 DK , 1 Huy
                                            @RequestParam(required = false) String otp
    ) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("{}, serviceCode={}, actionType={}, otp={}, language={}", isdn, serviceCode, actionType, otp, language);
        Map<String, String> otpResponse = otpService.validateOTP(OTPKey.PAYMENT_DATA_VAS, otp);

        if(!otpResponse.get("code").equals("0")) {
            log.error("otp validation failed| {}, language: {}, serviceCode: {}, actionType: {}, otpResponse: {}",isdn, language, serviceCode, actionType, otpResponse);
            return ResponseEntity.ok(new Response(Integer.parseInt(otpResponse.get("code")), otpResponse.get("message")));
        }

        return ResponseEntity.ok(ringmeSelfcareService.doActionService(language, serviceCode, actionType));
    }

    @PostMapping("/get-internet-packages-by-type")
    public ResponseEntity<?> getInternetPackageByType(@RequestParam String language,
                                                      @RequestParam MobileServices type) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}, type: {}, lang: {}", isdn, type, language);
        return ResponseEntity.ok(new Response(200, "Success", ringmeSelfcareService.getAllPackages(isdn, language, type)));
    }

    @PostMapping("/get-all-vas-packages")
    public ResponseEntity<?> getAllVasPackages(@RequestParam String language) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}, lang: {}", isdn, language);
        return ResponseEntity.ok(new Response(200, "Success", service.getAllVasPackages(isdn, language)));
    }

    @PostMapping("/get-all-ftth-packages")
    public ResponseEntity<?> getAllFtthPackages(@RequestParam(required = false, defaultValue = "en") String language) {
        log.info("REQ: {}, language: {}", jwtService.getUsernameFromJwt(), language);
        return ResponseEntity.ok(new Response(200, "Success", ftthService.getFtthPackages(language)));
    }

    @PostMapping("/get-data-package-info")
    public ResponseEntity<?> getDataPackageInfo(@RequestParam String language,
                                                @RequestParam String packageCode) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}, language: {}", isdn, language);
        return ResponseEntity.ok(ringmeSelfcareService.getDataPackageInfo(isdn, language, packageCode));
    }

    // lấy ra 3 chu kỳ gần nhất
    @PostMapping("/get-postage-info")
    public ResponseEntity<?> getPostageInfo(@RequestParam SubType subType,
                                            @RequestParam String language,
                                            @RequestParam long startDate,
                                            @RequestParam long endDate,
                                            @RequestParam(required = false, defaultValue = "all") String type) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}, type: {}, subType: {}, startDate: {}, endDate: {}, language: {}", isdn, type, subType, startDate, endDate,language);
        return ResponseEntity.ok(new Response(200, "Success", natcomSelfcareService.wsGetPostageInfo(isdn, subType, language, startDate, endDate, type)));
    }

    @PostMapping("/ftth/get-banners")
    public ResponseEntity<?> getBannerFtth() {
        return ResponseEntity.ok(new Response(200, "Success", service.getBanners()));
    }

    @PostMapping("/ftth/send-a-request-to-register")
    public ResponseEntity<?> ftthSendARequestToRegister(@RequestBody FtthRegister registerForm,
                                                        @RequestParam(required = false) String language) {
        String isdn = jwtService.getUsernameFromJwt();
        String lang;
        if(language == null)
            lang = registerForm.getLanguage();
        else
            lang = language;
        log.info("REQ: isdn: {}, language: {}, request: {}", isdn, lang, registerForm);
        if(service.ftthSendARequestToRegister(lang, registerForm))
            return ResponseEntity.ok(new Response(200, localeFactory.getMessage("ftth.register.success", lang)));
        else
            return ResponseEntity.ok(new Response(500, "Error"));
    }

    @PostMapping("/ftth/get-history-ftth-registers")
    public ResponseEntity<?> getHistoryFtthRegisters(@RequestParam(required = false) FtthRegisterStatus status,
                                                     @RequestParam(required = false) String startDate,
                                                     @RequestParam(required = false) String endDate) {
        log.info("status: {}, startDate: {}, endDate: {}", status, startDate, endDate);
        return ResponseEntity.ok(new Response(200, "Success", service.getHistoryFtthRegisters(status, startDate, endDate)));
    }

    @PostMapping("/get-postage-detail-info")
    public ResponseEntity<?> getPostageDetailInfo(@RequestParam PostType postType,
                                                  @RequestParam int pageSize,
                                                  @RequestParam int pageNum,
                                                  @RequestParam long startDate,
                                                  @RequestParam long endDate) {
        String isdn = jwtService.getUsernameFromJwt();
        return ResponseEntity.ok(new Response(200, "Success", natcomSelfcareService.wsGetPostageDetailInfo(isdn, postType, pageSize, pageNum, startDate, endDate)));
    }

    @PostMapping("/do-recharge")
    public ResponseEntity<?> doRecharge(@RequestParam String language,
                                        @RequestParam int desIsdn,
                                        @RequestParam String serial,
                                        @RequestParam String captcha) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}, desIsdn: {}, serial: {}, captcha: {}, lang: {}", isdn, desIsdn, serial, captcha, language);
        return ResponseEntity.ok(service.doRecharge(language, desIsdn, serial, captcha));
    }

    @PostMapping("/get-refill-history-info")
    public ResponseEntity<?> getRefillHistoryInfo(@RequestParam String language,
                                                  @RequestParam int pageSize,
                                                  @RequestParam int pageNum,
                                                  @RequestParam long startDate,
                                                  @RequestParam long endDate,
                                                  @RequestParam SortType sort,
                                                  HttpServletRequest request) {
        String subId = jwtService.extractSubId(request);
        return ResponseEntity.ok(new Response(200, "Success", natcomSelfcareService.wsGetRefillHistoryInfo(subId, language, pageSize, pageNum, startDate, endDate, sort)));
    }

    @PostMapping("/get-services-by-group-type")
    public ResponseEntity<?> getServicesByGroupType(@RequestParam ServiceGroupType serviceGroupType,
                                                    @RequestParam String language) {
        String isdn = jwtService.getUsernameFromJwt();
        return ResponseEntity.ok(new Response(200, "Success", natcomSelfcareService.wsGetServicesByGroupType(isdn, serviceGroupType, language, appConfig.getSelfcareGiftToken())));
    }

    @PostMapping("/gift-data-to-friend")
    public ResponseEntity<?> giftDataToFriend(@RequestParam String language,
                                              @RequestParam String command,
                                              @RequestParam String msisdnRecy,
                                              @RequestParam String otp) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}, command: {}, msisdnRecy: {}, otp: {}, lang: {}", isdn, command, msisdnRecy, otp, language);
        Map<String, String> otpResponse = otpService.validateOTP(OTPKey.GIFT_DATA_TO_FRIEND, otp);

        if(!otpResponse.get("code").equals("0")) {
            log.error("otp validation failed|{}, language: {}, command: {}, msisdnRecy: {}, otp: {}, otpResponse: {}",isdn, language, command, msisdnRecy, otp, otpResponse);
            return ResponseEntity.ok(new Response(Integer.parseInt(otpResponse.get("code")), otpResponse.get("message")));
        }

        return ResponseEntity.ok(ringmeSelfcareService.giftDataToFriend(language, command, msisdnRecy));
    }

//    @PostMapping("/gift-xchange-to-friend")
//    public ResponseEntity<?> giftXchangeToFriend(@RequestParam String language,
//                                                 @RequestParam String command,
//                                                 @RequestParam String msisdnRecy) {
//        return ResponseEntity.ok(ringmeSelfcareService.giftXchangeToFriend(language, command, msisdnRecy));
//    }

    @PostMapping("/ishare")
    public ResponseEntity<?> getIshare(@RequestParam String language,
                                       @RequestParam String receiveIsdn,
                                       @RequestParam String amount,
                                       @RequestParam String otp) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}, amount: {}, receiveIsdn: {}, otp: {}, lang: {}", isdn, amount, receiveIsdn, otp, language);
        Map<String, String> otpResponse = otpService.validateOTP(OTPKey.I_SHARE, otp);

        if(!otpResponse.get("code").equals("0")) {
            log.error("otp validation failed|{}, language: {}, receiveIsdn: {}, amount: {}, otp: {}, otpResponse: {}", isdn, language, receiveIsdn, amount, otp, otpResponse);
            return ResponseEntity.ok(new Response(Integer.parseInt(otpResponse.get("code")), otpResponse.get("message")));
        }

        return ResponseEntity.ok(ringmeSelfcareService.ishare(receiveIsdn, amount, language));
    }

    @PostMapping("/get-data-volume-level-to-buy")
    public ResponseEntity<?> getDataVolumeLevelToBuy(@RequestParam String language,
                                                     @RequestParam String packageCode) {
        String isdn = jwtService.getUsernameFromJwt();
        return ResponseEntity.ok(new Response(200, "Success", natcomSelfcareService.wsGetDataVolumeLevelToBuy(isdn, packageCode, language)));
    }

    @PostMapping("/do-buy-data")
    public ResponseEntity<?> doBuyData(@RequestParam(required = false, defaultValue = "en") String language,
                                       @RequestParam String packageCode,
                                       @RequestParam String price,
                                       @RequestParam int volume,
                                       @RequestParam String otp) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}, packageCode: {}, price: {}, volume: {}, otp: {}, lang: {}", isdn, packageCode, price, volume,otp, language);
        Map<String, String> otpResponse = otpService.validateOTP(OTPKey.PAYMENT_DATA_PLUS, otp);

        if(!otpResponse.get("code").equals("0")) {
            log.error("otp validation failed|{}, language: {}, packageCode: {}, price: {}, volume: {}, otp: {}, otpResponse: {}",isdn, language, packageCode, price, volume, otp, otpResponse);
            return ResponseEntity.ok(new Response(Integer.parseInt(otpResponse.get("code")), otpResponse.get("message")));
        }

        return ResponseEntity.ok(ringmeSelfcareService.doBuyData(language, packageCode, price, volume));
    }

    @PostMapping("/get-provinces")
    public ResponseEntity<?> getProvinces() {
        return ResponseEntity.ok(new Response(200, "Success", natcomSelfcareService.wsGetProvinces()));
    }

    @PostMapping("/get-districts")
    public ResponseEntity<?> getDistricts(@RequestParam String provinceId) {
        return ResponseEntity.ok(new Response(200, "Success", natcomSelfcareService.wsGetDistricts(provinceId)));
    }

    @PostMapping("/find-store-by-addr")
    public ResponseEntity<?> findStoreByAddr(@RequestParam(required = false) String longitude,
                                             @RequestParam(required = false) String latitude,
                                             @RequestParam(required = false) String provinceId,
                                             @RequestParam(required = false) String districtId) {
        return ResponseEntity.ok(new Response(200, "Success", natcomSelfcareService.wsFindStoreByAddr(longitude, latitude, provinceId, districtId)));
    }

    @PostMapping("/get-services")
    public ResponseEntity<?> getServices(@RequestParam String language) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}, lang: {}", isdn, language);
        return ResponseEntity.ok(new Response(200, "Success", natcomSelfcareService.wsGetServices(isdn, language)));
    }

    @PostMapping("/get-services-v2")
    public ResponseEntity<?> getServicesV2(@RequestParam String language) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}, lang: {}", isdn, language);
        return ResponseEntity.ok(new Response(200, "Success", ringmeSelfcareService.wsGetServices(isdn, language)));
    }

    @PostMapping("/get-services-by-group")
    public ResponseEntity<?> getServicesByGroup(@RequestParam String language,
                                                @RequestParam String serviceGroupId) {
        String isdn = jwtService.getUsernameFromJwt();
        return ResponseEntity.ok(new Response(200, "Success", natcomSelfcareService.wsGetServicesByGroup(isdn, language, serviceGroupId)));
    }

    @PostMapping("/get-service-detail")
    public ResponseEntity<?> getServiceDetail(@RequestParam String language,
                                              @RequestParam String serviceCode) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}, lang: {}", isdn, language);
        return ResponseEntity.ok(new Response(200, "Success", natcomSelfcareService.wsGetServiceDetail(isdn, language, serviceCode)));
    }

    @PostMapping("/get-current-used-services")
    public ResponseEntity<?> getCurrentUsedServices(HttpServletRequest request,
                                                    @RequestParam String language) {
        String subId = jwtService.extractSubId(request);
        SubType subType = jwtService.extractSubType(request);
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}, subId: {}, subType: {}, lang: {}", isdn, subId, subType, language);
        return ResponseEntity.ok(new Response(200, "Success", natcomSelfcareService.wsGetCurrentUsedServices(subId, language, subType, isdn)));
    }

    @PostMapping("/get-current-used-vas")
    public ResponseEntity<?> getCurrentUsedVas(@RequestParam String language) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}, lang: {}", isdn,language);
        return ResponseEntity.ok(new Response(200, "Success", natcomSelfcareService.wsGetCurrentUsedVas(isdn, language)));
    }

    @PostMapping("/schedule-a-call")
    public ResponseEntity<?> scheduleACall(@RequestBody ScheduleCall scheduleCall) {
        return ResponseEntity.ok(new Response(200, localeFactory.getMessage("schedule-call-success", scheduleCall.getLanguage()), ringmeSelfcareService.scheduleCall(scheduleCall)));
    }

    @PostMapping("/nat-fund")
    public ResponseEntity<?> natFund(@RequestParam String language,
                                     @RequestParam double amount,
                                     @RequestParam String otp) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}, amount: {}, otp: {}, lang: {}", isdn, amount, otp, language);
        Map<String, String> otpResponse = otpService.validateOTP(OTPKey.NAT_FUND, otp);

        if(!otpResponse.get("code").equals("0")) {
            log.error("otp validation failed|{}, language: {}, amount: {}, otp: {}, otpResponse: {}", isdn, language, amount, otp, otpResponse);
            return ResponseEntity.ok(new Response(Integer.parseInt(otpResponse.get("code")), otpResponse.get("message")));
        }

        return ResponseEntity.ok(natFundService.handleDonate(language, jwtService.getUsernameFromJwt(), amount));
    }
}
