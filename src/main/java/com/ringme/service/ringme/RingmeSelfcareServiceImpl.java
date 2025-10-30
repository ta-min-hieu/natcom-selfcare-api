package com.ringme.service.ringme;

import com.ringme.common.Common;
import com.ringme.common.Helper;
import com.ringme.config.LocaleFactory;
import com.ringme.dao.mysql.FtthDao;
import com.ringme.dao.mysql.SubServiceDao;
import com.ringme.dao.mysql.VasDao;
import com.ringme.dao.mysql.selfcare.ScheduleCallDao;
import com.ringme.dao.mysql.selfcare.TkgPaymentHistoryDao;
import com.ringme.dto.natcom.loyalty.LoyaltyResponse;
import com.ringme.dto.natcom.selfcare.response.SelfcareResponse;
import com.ringme.dto.natcom.selfcare.response.SubAccountInfo;
import com.ringme.dto.natcom.selfcare.response.SubMainInfo;
import com.ringme.dto.natcom.selfcare.response.accounts.detail.AccountsDetail;
import com.ringme.dto.natcom.selfcare.response.packages.Package;
import com.ringme.dto.natcom.selfcare.response.packages.SubPackage;
import com.ringme.dto.natcom.selfcare.response.service.detail.ServiceGroup;
import com.ringme.dto.natcom.selfcare.response.service.detail.ServiceTeam;
import com.ringme.dto.record.Response;
import com.ringme.dto.ringme.selfcare.AccountInfo;
import com.ringme.dto.ringme.ftth.FtthRegister;
import com.ringme.dto.ringme.selfcare.AccountInfoDetail;
import com.ringme.dto.ringme.selfcare.BannerDto;
import com.ringme.enums.CaptchaKey;
import com.ringme.enums.loyalty.PointTypeId;
import com.ringme.enums.selfcare.*;
import com.ringme.enums.selfcare.mobileServices.*;
import com.ringme.model.selfcare.ScheduleCall;
import com.ringme.model.selfcare.TkgPaymentHistory;
import com.ringme.service.natcom.NatcomLoyaltyService;
import com.ringme.service.natcom.NatcomSelfcareService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RingmeSelfcareServiceImpl implements RingmeSelfcareService {
    @Autowired
    NatcomSelfcareService natcomSelfcareService;
    @Autowired
    NatcomLoyaltyService natcomLoyaltyService;
    @Autowired
    JwtService jwtService;
    @Autowired
    FtthDao ftthDao;
    @Autowired
    ScheduleCallDao scheduleCallDao;
    @Autowired
    VasDao vasDao;
    @Autowired
    SubServiceDao subServiceDao;
    @Autowired
    TkgPaymentHistoryDao tkgPaymentHistoryDao;
    @Autowired
    CaptchaService captchaService;
    @Autowired
    LocaleFactory localeFactory;
    @Autowired
    OTPService otpService;

    @Override
    public List<BannerDto> getBanners() {
        List<BannerDto> list = new ArrayList<>();
        list.add(new BannerDto(null, "https://cdn-my.lumitel.bi/cdn-media-01/payment/background/img/banner-selfcare.png"));
        list.add(new BannerDto(null, "https://cdn-my.lumitel.bi/cdn-media-01/payment/background/img/banner-selfcare.png"));
        list.add(new BannerDto(null, "https://cdn-my.lumitel.bi/cdn-media-01/payment/background/img/banner-selfcare.png"));

        return list;
    }

    @Override
    public AccountInfo getAccountInfo(String isdn, String language, String vtAccId) {
        try {
            SelfcareResponse<SubMainInfo> mainInfoR = natcomSelfcareService.wsGetSubMainInfo(isdn, language);
            log.info("mainInfoR:{}", mainInfoR);
            if(mainInfoR != null) {
                SubMainInfo subMainInfo = mainInfoR.getWsResponse();
                if(subMainInfo != null) {
                    SelfcareResponse<SubAccountInfo> accountInfoR = natcomSelfcareService.wsGetSubAccountInfo(isdn, subMainInfo.getSubType(), language);
                    LoyaltyResponse<Object> totalAccountPointR = natcomLoyaltyService.getTotalPoint(vtAccId, PointTypeId.CONSUMPTION);

                    AccountInfo accountInfo = new AccountInfo(isdn, mainInfoR, accountInfoR, totalAccountPointR);
                    log.info("accountInfoR: {}", accountInfoR);

                    return accountInfo;
                }
            }
        } catch (Exception e) {
            log.error("isdn: {}, subType: {}, language: {}, error: {}", isdn, language, e.getMessage(), e);
        }

        return new AccountInfo();
    }

    @Override
    public SelfcareResponse<List<ServiceGroup>> getAllVasPackages(String isdn, String language) {
        return getFilteredPackagesV2(isdn, language, VasGroupCode.class, VasGroupCode::getType);
    }

    @Override
    public SelfcareResponse<List<ServiceGroup>> getAllPromotionPackages(String isdn, String language) {
        return getFilteredPackages(isdn, language, PromotionGroupCode.class, PromotionGroupCode::getType);
    }

    @Override
    public SelfcareResponse<List<ServiceGroup>> getAllSmsPackages(String isdn, String language) {
        return getFilteredPackages(isdn, language, SmsGroupCode.class, SmsGroupCode::getType);
    }

    @Override
    public boolean ftthSendARequestToRegister(String language, FtthRegister form) {
        String isdn = jwtService.getUsernameFromJwt();
        form.setIsdner(isdn);

        form.setOrderCode(Helper.generateRandomString(8));
        boolean rs = ftthDao.store(form);
        if(rs)
            otpService.sendOTP(form.getIsdnee(), localeFactory.getMessage("sms.ftth.register", language).replace("{{CODE}}", form.getOrderCode()));

        ftthDao.clearCacheHistoryFtthRegisterByIsdner(isdn);
        return rs;
    }

    @Override
    public List<FtthRegister> getHistoryFtthRegisters(FtthRegisterStatus status, String startDate, String endDate) {
        String isdn = jwtService.getUsernameFromJwt();
        startDate = Helper.processStringSearch(startDate);
        endDate = Helper.processStringSearch(endDate);

        return ftthDao.getHistoryFtthRegisters(isdn, handleStatusFtth(status), startDate, endDate);
    }

    private List<Integer> handleStatusFtth(FtthRegisterStatus status) {
        if(status == null)
            return List.of(0, 1, 2, 3, 4, 5, 6, 7);
        else if(status.equals(FtthRegisterStatus.IN_PROCESS))
            return List.of(0, 1, 2, 3, 4, 5);
        else if (status.equals(FtthRegisterStatus.SUCCESS))
            return List.of(6);
        else
            return List.of(7);
    }

    @Override
    public boolean scheduleCall(ScheduleCall scheduleCall) {
        scheduleCall.setIsdn(jwtService.getUsernameFromJwt());
        return scheduleCallDao.store(scheduleCall);
    }

    @Override
    public List<ServiceTeam> wsGetServices(String isdn, String language) {
        List<ServiceTeam> serviceTeams = new ArrayList<>();

        dataTeamHandle(isdn, language, serviceTeams);
        SelfcareResponse<List<ServiceGroup>> sr = natcomSelfcareService.wsGetServices(isdn, language);
        if (sr != null) {
            List<ServiceGroup> dataGroups = sr.getWsResponse();
            if(dataGroups != null && !dataGroups.isEmpty()) {
                List<Package> packages = new ArrayList<>();
                for (ServiceGroup dataGroup : dataGroups)
                    packages.addAll(dataGroup.getServices());

                if(!packages.isEmpty()) {
                    promotionTeamHandle(packages, serviceTeams);
                    xchangeTeamHandle(packages, serviceTeams);
                    voiceTeamHandle(packages, serviceTeams);
                    smsTeamHandle(packages, serviceTeams);
                }
            }
        }

        handleAddSubsevices(serviceTeams);

        return serviceTeams;
    }

    @Override
    public List<ServiceGroup> getAllPackages(String isdn, String language, MobileServices type) {
        List<ServiceTeam> serviceTeams = new ArrayList<>();

        if(type.equals(MobileServices.DATA_PLANS))
            dataTeamHandle(isdn, language, serviceTeams);
        else {
            SelfcareResponse<List<ServiceGroup>> sr = natcomSelfcareService.wsGetServices(isdn, language);
            if (sr != null) {
                List<ServiceGroup> dataGroups = sr.getWsResponse();
                if(dataGroups != null && !dataGroups.isEmpty()) {
                    List<Package> packages = new ArrayList<>();
                    for (ServiceGroup dataGroup : dataGroups)
                        packages.addAll(dataGroup.getServices());

                    switch (type) {
                        case SMS_PLANS -> smsTeamHandle(packages, serviceTeams);
                        case PROMOTION -> promotionTeamHandle(packages, serviceTeams);
                        case XCHANGE_PLANS -> xchangeTeamHandle(packages, serviceTeams);
                        case VOICE_PLANS -> voiceTeamHandle(packages, serviceTeams);
                        default -> log.warn("Unknown service group type: {}", type.getType());
                    }
                }
            }
        }

        handleAddSubsevices(serviceTeams);

        if(!serviceTeams.isEmpty())
            return serviceTeams.getFirst().getServices();

        return new ArrayList<>();
    }

    private void handleAddSubsevices(List<ServiceTeam> serviceTeams) {
        if(serviceTeams == null || serviceTeams.isEmpty())
            return;

        for (ServiceTeam serviceTeam : serviceTeams) {
            List<ServiceGroup> services = serviceTeam.getServices();
            if(services == null || services.isEmpty())
                continue;

            for (ServiceGroup serviceGroup : services) {
                List<Package> packages = serviceGroup.getServices();
                if(packages == null || packages.isEmpty())
                    continue;

                for (Package p : packages)
                    handleAddSubPackage(p);
            }
        }
    }

    private void handleAddSubPackage(Package p) {
        List<SubPackage> subPackages = subServiceDao.getSubPackageByServiceId(p.getServiceId());
        p.setSubServices(subPackages);
        if(subPackages != null && !subPackages.isEmpty() && subPackages.getFirst() != null)
            p.setPrice(subPackages.getFirst().getPrice());
    }

    private void handleAddSubPackageVas(Package p) {
        List<SubPackage> subPackages = subServiceDao.getSubPackageByServiceId(p.getServiceId());
        if(subPackages != null && !subPackages.isEmpty())
            for(SubPackage subPackage : subPackages)
                subPackage.setPaymentMINatcash(false);

        p.setSubServices(subPackages);
        if(subPackages != null && !subPackages.isEmpty() && subPackages.getFirst() != null)
            p.setPrice(subPackages.getFirst().getPrice());
    }

    @Override
    public AccountInfoDetail getAccountInfoDetail(String isdn, String language, SubType subType, String name) {
        SelfcareResponse<List<AccountsDetail>> accountsDetails = natcomSelfcareService.wsGetAccountsDetail(isdn, subType, language);

        return new AccountInfoDetail(accountsDetails, name);
    }

    @Override
    public Response doRecharge(String language, int desIsdn, String serial, String captcha) {
        try {
            String isdn = jwtService.getUsernameFromJwt();

            if (!captchaService.validateCaptcha(CaptchaKey.TOPUP_AIRTIME, captcha))
                return new Response(56, localeFactory.getMessage("captcha.invalid", language), new SelfcareResponse<>(localeFactory.getMessage("captcha.invalid", language)));

            SelfcareResponse<Object> response = natcomSelfcareService.wsDoRecharge(isdn, language, desIsdn, serial.trim());

            String errorCode = response.getErrorCode();
            if (errorCode == null) {
                tkgPaymentHistoryDao.store(new TkgPaymentHistory(LogCdr.CARD_RECHARGE.getType(), String.valueOf(desIsdn), isdn, serial, null, 1));
                return new Response(123, "Null error", new SelfcareResponse<>("Null error"));
            } else if (Helper.checkErrorCode(errorCode)) {
                Common.logCdr(LogCdr.CARD_RECHARGE.getType(), isdn, serial, "", String.valueOf(desIsdn));
                tkgPaymentHistoryDao.store(new TkgPaymentHistory(LogCdr.CARD_RECHARGE.getType(), String.valueOf(desIsdn), isdn, serial, null, 0));
                return new Response(200, localeFactory.getMessage("success", language), response);
            } else if (errorCode.equals("1")) {
                tkgPaymentHistoryDao.store(new TkgPaymentHistory(LogCdr.CARD_RECHARGE.getType(), String.valueOf(desIsdn), isdn, serial, null, 1));
                return new Response(400, localeFactory.getMessage("phone-card-invalid", language), response);
            }
            tkgPaymentHistoryDao.store(new TkgPaymentHistory(LogCdr.CARD_RECHARGE.getType(), String.valueOf(desIsdn), isdn, serial, null, 1));
            return new Response(999, "Unknow error", response);
        } catch (Exception e) {
            log.error("System error| language: {}, desIsdn: {}, serial: {}, captcha: {}", language, desIsdn, serial, captcha, e);
        }

        return new Response(777, "System error", new SelfcareResponse<>("System error"));
    }

    @Override
    public Response ishare(String receiveIsdn, String amount, String language) {
        String isdn = jwtService.getUsernameFromJwt();
        try {
            if (isdn.equals(receiveIsdn))
                return new Response(1, "You cannot perform an ishare for yourself");

            SelfcareResponse<Object> response = natcomSelfcareService.wsIshare(isdn, receiveIsdn, amount, language);

            String errorCode = response.getErrorCode();
            if(errorCode == null)
                return new Response(123, "Null error");
            else if (Helper.checkErrorCode(errorCode)) {
                return new Response(200, response.getUserMsg(), response);
            } else if (errorCode.equals("1"))
                return new Response(400, localeFactory.getMessage("transfer-money-unsuccessfully"), response);

            return new Response(999, "Unknow error", response);
        } catch (Exception e) {
            log.error("System error| isdn: {}, receiveIsdn: {}, amount: {}, language: {}", isdn, receiveIsdn, amount, language, e);
        }

        return new Response(777, "System error");
    }

    @Override
    public Response doActionService(String language, String serviceCode, ActionType actionType) {
        String isdn = jwtService.getUsernameFromJwt();
        try {
            SelfcareResponse<Object> response = natcomSelfcareService.wsDoActionService(isdn, language, serviceCode, actionType);

            String typePay;
            if(actionType.equals(ActionType.REGISTER))
                typePay = isVasPackage(serviceCode) ? LogCdr.TKG_BUY_VAS.getType() : LogCdr.TKG_BUY_MOBILE_SERVICE.getType();
            else
                typePay = isVasPackage(serviceCode) ? "TKG_CANCEL_VAS" : "TKG_CANCEL_MOBILE_SERVICE";

            String errorCode = response.getErrorCode();
            if(errorCode == null) {
                if(actionType.equals(ActionType.REGISTER))
                    tkgPaymentHistoryDao.store(new TkgPaymentHistory(typePay, isdn, null, serviceCode, getAmountBySubServiceCodeV2(serviceCode), 1));
                else
                    tkgPaymentHistoryDao.store(new TkgPaymentHistory(typePay, isdn, null, serviceCode, getAmountBySubServiceCodeV2(serviceCode), 1));
                return new Response(123, "Null error");
            } else if (Helper.checkErrorCode(errorCode)) {
                String message = response.getUserMsg();
                String userMessage = response.getUserMsg();
                if(userMessage != null && !userMessage.isEmpty())
                    message = userMessage;

                if(actionType.equals(ActionType.REGISTER)) {
                    tkgPaymentHistoryDao.store(new TkgPaymentHistory(typePay, isdn, null, serviceCode, getAmountBySubServiceCodeV2(serviceCode), 0));
                    Common.logCdr(typePay, isdn, serviceCode, getAmountBySubServiceCode(serviceCode), "");
                } else
                    tkgPaymentHistoryDao.store(new TkgPaymentHistory(typePay, isdn, null, serviceCode, getAmountBySubServiceCodeV2(serviceCode), 0));

                return new Response(200, message, response);
            } else if (errorCode.equals("1")) {
                if(actionType.equals(ActionType.REGISTER))
                    tkgPaymentHistoryDao.store(new TkgPaymentHistory(typePay, isdn, null, serviceCode, getAmountBySubServiceCodeV2(serviceCode), 1));
                else
                    tkgPaymentHistoryDao.store(new TkgPaymentHistory(typePay, isdn, null, serviceCode, getAmountBySubServiceCodeV2(serviceCode), 1));
                return new Response(400, response.getUserMsg(), response);
            }

            if(actionType.equals(ActionType.REGISTER))
                tkgPaymentHistoryDao.store(new TkgPaymentHistory(typePay, isdn, null, serviceCode, getAmountBySubServiceCodeV2(serviceCode), 1));
            else
                tkgPaymentHistoryDao.store(new TkgPaymentHistory(typePay, isdn, null, serviceCode, getAmountBySubServiceCodeV2(serviceCode), 1));
            return new Response(999, "Unknow error", response);
        } catch (Exception e) {
            log.error("System error| isdn: {}, language: {}, serviceCode: {}, actionType: {}", isdn, language, serviceCode, actionType, e);
        }

        return new Response(777, "System error");
    }

    private String getAmountBySubServiceCode(String code) {
        return String.valueOf(subServiceDao.getPackageAmountByLanguageAndCode(code).divide(new BigDecimal("1.1"), 2, RoundingMode.HALF_UP));
    }

    private BigDecimal getAmountBySubServiceCodeV2(String code) {
        return subServiceDao.getPackageAmountByLanguageAndCode(code).divide(new BigDecimal("1.1"), 2, RoundingMode.HALF_UP);
    }

    @Override
    public Response giftDataToFriend(String language, String command, String msisdnRecy) {
        String isdn = jwtService.getUsernameFromJwt();

        try {
            SelfcareResponse<Object> response = natcomSelfcareService.wsGiftDataToFriend(isdn, language, isdn, command, msisdnRecy);

            String errorCode = response.getErrorCode();
            if(errorCode == null) {
                tkgPaymentHistoryDao.store(new TkgPaymentHistory(LogCdr.TKG_SHARE_MOBILE_SERVICE.getType(), msisdnRecy, isdn, command, getAmountBySubServiceCodeV2(command), 1));
                return new Response(123, "Null error");
            } else if (Helper.checkErrorCode(errorCode)) {
                Common.logCdr(LogCdr.TKG_SHARE_MOBILE_SERVICE.getType(), isdn, command, getAmountBySubServiceCode(command), msisdnRecy);
                tkgPaymentHistoryDao.store(new TkgPaymentHistory(LogCdr.TKG_SHARE_MOBILE_SERVICE.getType(), msisdnRecy, isdn, command, getAmountBySubServiceCodeV2(command), 0));
                return new Response(200, localeFactory.getMessage("success", language), response);
            } else if (errorCode.equals("1")) {
                tkgPaymentHistoryDao.store(new TkgPaymentHistory(LogCdr.TKG_SHARE_MOBILE_SERVICE.getType(), msisdnRecy, isdn, command, getAmountBySubServiceCodeV2(command), 1));
                return new Response(400, response.getUserMsg(), response);
            }

            tkgPaymentHistoryDao.store(new TkgPaymentHistory(LogCdr.TKG_SHARE_MOBILE_SERVICE.getType(), msisdnRecy, isdn, command, getAmountBySubServiceCodeV2(command), 1));
            return new Response(999, "Unknow error", response);
        } catch (Exception e) {
            log.error("System error| isdn: {}, language: {}, command: {}, msisdnRecy: {}", isdn, language, command, msisdnRecy, e);
        }

        return new Response(777, "System error");
    }

    @Override
    public Response giftXchangeToFriend(String language, String command, String msisdnRecy) {
        String isdn = jwtService.getUsernameFromJwt();
        try {
            SelfcareResponse<Object> response = natcomSelfcareService.wsGiftXchangeToFriend(isdn, language, isdn, command, msisdnRecy);

            String errorCode = response.getErrorCode();
            if(errorCode == null)
                return new Response(123, "Null error");
            else if (Helper.checkErrorCode(errorCode))
                return new Response(200, localeFactory.getMessage("success", language), response);
            else if (errorCode.equals("1"))
                return new Response(400, response.getUserMsg(), response);

            return new Response(999, "Unknow error", response);
        } catch (Exception e) {
            log.error("System error| isdn: {}, language: {}, command: {}, msisdnRecy: {}", isdn, language, command, msisdnRecy, e);
        }

        return new Response(777, "System error");
    }

    @Override
    public Response doBuyData(String language, String packageCode, String price, int volume) {
        String isdn = jwtService.getUsernameFromJwt();
        try {
            SelfcareResponse<Object> response = natcomSelfcareService.wsDoBuyData(isdn, packageCode, price, volume);

            String errorCode = response.getErrorCode();
            if(errorCode == null) {
                tkgPaymentHistoryDao.store(new TkgPaymentHistory(LogCdr.TKG_DATA_PLUS.getType(), isdn, null, packageCode + "-" + price + "-" + volume, Helper.roundingMode(Double.parseDouble(price)), 1));
                return new Response(123, "Null error");
            } else if (Helper.checkErrorCode(errorCode)) {
                Common.logCdr(LogCdr.TKG_DATA_PLUS.getType(), isdn, packageCode + "-" + price + "-" + volume, String.valueOf(Helper.roundingMode(Double.parseDouble(price))), "");
                tkgPaymentHistoryDao.store(new TkgPaymentHistory(LogCdr.TKG_DATA_PLUS.getType(), isdn, null, packageCode + "-" + price + "-" + volume, Helper.roundingMode(Double.parseDouble(price)), 0));
                return new Response(200, localeFactory.getMessage("success", language), response);
            } else if (errorCode.equals("1")) {
                tkgPaymentHistoryDao.store(new TkgPaymentHistory(LogCdr.TKG_DATA_PLUS.getType(), isdn, null, packageCode + "-" + price + "-" + volume, Helper.roundingMode(Double.parseDouble(price)), 1));
                return new Response(400, response.getUserMsg(), response);
            }

            tkgPaymentHistoryDao.store(new TkgPaymentHistory(LogCdr.TKG_DATA_PLUS.getType(), isdn, null, packageCode + "-" + price + "-" + volume, Helper.roundingMode(Double.parseDouble(price)), 1));
            return new Response(999, "Unknow error", response);
        } catch (Exception e) {
            log.error("System error| isdn: {}, language: {}, packageCode: {}, price: {}, volume: {}", isdn, language, packageCode, price, volume, e);
        }

        return new Response(777, "System error");
    }

    @Override
    public Response getDataPackageInfo(String isdn, String language, String packageCode) {
        SelfcareResponse<Package> selfcareResponse = natcomSelfcareService.wsGetDataPackageInfo(isdn, language, packageCode);
// todo bật đăng ký gói bằng ví
//        if(response != null) {
//            PackageInfo packageInfo = response.getWsResponse();
//            if(packageInfo != null)
//                packageInfo.setPaymentMINatcash(true);
//        }

        if(selfcareResponse != null) {
            Package p = selfcareResponse.getWsResponse();
            handleAddSubPackage(p);
        }

        return new Response(200, "Success", selfcareResponse);
    }

    @Override
    public boolean isVasPackage(String packageCode) {
        try {
            List<String> vasCodes = subServiceDao.getAllVasPackageCode();
            return vasCodes != null && vasCodes.contains(packageCode);
        } catch (Exception e) {
            log.error("packageCode: {}", packageCode, e);
        }

        return false;
    }

    private void smsTeamHandle(List<Package> packages, List<ServiceTeam> serviceTeams) {
        ServiceTeam serviceTeam = new ServiceTeam();
        serviceTeam.setTeamName(MobileServices.SMS_PLANS.getType());
        List<ServiceGroup> serviceGroups = new ArrayList<>();

        groupHandle(serviceGroups, SmsPlans.SMS_ON_NET.getType(), packages, List.of("SMS"));
//        groupHandle(serviceGroups, SmsPlans.SMS_OFF_NET.getType(), packages, List.of());

        if(!serviceGroups.isEmpty()) {
            serviceTeam.setServices(serviceGroups);
            serviceTeams.add(serviceTeam);
        }
    }

    private void xchangeTeamHandle(List<Package> packages, List<ServiceTeam> serviceTeams) {
        ServiceTeam serviceTeam = new ServiceTeam();
        serviceTeam.setTeamName(MobileServices.XCHANGE_PLANS.getType());
        List<ServiceGroup> serviceGroups = new ArrayList<>();

        groupHandleV2(serviceGroups, Xchange.XCHANGE.getType(), packages, List.of("Xchange25", "Xchange18", "Xchange25_A", "Xchange18_A"));
        groupHandle(serviceGroups, Xchange.SUPER_XCHANGE.getType(), packages, List.of("SUPERXCHANGE"));

        if(!serviceGroups.isEmpty()) {
            serviceTeam.setServices(serviceGroups);
            serviceTeams.add(serviceTeam);
        }
    }

    private void voiceTeamHandle(List<Package> packages, List<ServiceTeam> serviceTeams) {
        ServiceTeam serviceTeam = new ServiceTeam();
        serviceTeam.setTeamName(MobileServices.VOICE_PLANS.getType());
        List<ServiceGroup> serviceGroups = new ArrayList<>();

        groupHandle(serviceGroups, VoicePlans.VOICE_ON_NET.getType(), packages, List.of("VU", "VL"));
        groupHandle(serviceGroups, VoicePlans.VOICE_OFF_NET.getType(), packages, List.of("PALE LOKAL"));
        groupHandle(serviceGroups, VoicePlans.VOICE_INTERNATIONAL.getType(), packages, List.of("PaleUS", "PaleDR"));

        if(!serviceGroups.isEmpty()) {
            serviceTeam.setServices(serviceGroups);
            serviceTeams.add(serviceTeam);
        }
    }

    private void promotionTeamHandle(List<Package> packages, List<ServiceTeam> serviceTeams) {
        ServiceTeam serviceTeam = new ServiceTeam();
        serviceTeam.setTeamName(MobileServices.PROMOTION.getType());
        List<ServiceGroup> serviceGroups = new ArrayList<>();

        groupHandle(serviceGroups, Promotion.DATA.getType(), packages, List.of("New_DL9", "DL3"));
        groupHandle(serviceGroups, Promotion.XCHANGE.getType(), packages, List.of("Xchange50_Discount", "Xchange25_Discount", "Xchange50_Plus", "Xchange25_Plus", "New-Xchange6"));
        groupHandle(serviceGroups, Promotion.VOICE.getType(), packages, List.of("aaaaaaaaaaaaaaaa"));

        String msisdn = "509" + jwtService.getUsernameFromJwt();
        for(int i=0; i < serviceGroups.size(); i++) {
            ServiceGroup serviceGroup = serviceGroups.get(i);
            List<Package> filteredList = serviceGroup.getServices().stream()
                    .filter(pkg -> vasDao.checkPromotionPackage(msisdn, pkg.getCode()))
                    .collect(Collectors.toList());

            if(!filteredList.isEmpty())
                serviceGroup.setServices(filteredList);
            else {
                serviceGroups.remove(serviceGroup);
                i--;
            }
        }

        if(!serviceGroups.isEmpty()) {
            serviceTeam.setServices(serviceGroups);
            serviceTeams.add(serviceTeam);
        }
    }

    private void dataTeamHandle(String isdn, String language, List<ServiceTeam> serviceTeams) {
        SelfcareResponse<List<Package>> sr = natcomSelfcareService.wsGetAllDataPackages(isdn, language);
        if(sr == null)
            return;

        List<Package> dataPackages = sr.getWsResponse();
        if(dataPackages == null || dataPackages.isEmpty())
            return;

        ServiceTeam serviceTeam = new ServiceTeam();
        serviceTeam.setTeamName(MobileServices.DATA_PLANS.getType());
        List<ServiceGroup> serviceGroups = new ArrayList<>();

        groupHandle(serviceGroups, DataPlans.BUNDLE.getType(), dataPackages, List.of("VIP", "ULTIMATE", "Premium", "Plus", "Basic"));
        groupHandle(serviceGroups, DataPlans.DATA_ONLY_PLANS.getType(), dataPackages, List.of("DL", "WU", "MU"));
        groupHandle(serviceGroups, DataPlans.SUPERDATA_PLANS.getType(), dataPackages, List.of("SUPERDATA"));
        groupHandle(serviceGroups, DataPlans.DCOM_ROUTER_PLANS.getType(), dataPackages, List.of("FTTH_DC", "Silver"));
        groupHandle(serviceGroups, DataPlans.FACEBOOK_YOUTUBE_PLANS.getType(), dataPackages, List.of("YouTube", "Facebook"));
        groupHandle(serviceGroups, DataPlans.CRAZYDATA_PLANS.getType(), dataPackages, List.of("Crazy"));

        if(!serviceGroups.isEmpty())
            serviceTeam.setServices(serviceGroups);

        serviceTeams.add(serviceTeam);
    }

    private void groupHandle(List<ServiceGroup> serviceGroups, String groupName, List<Package> dataPackages, List<String> patterns) {
        ServiceGroup serviceGroup = new ServiceGroup();

        List<Package> packagePlans = dataPackages.stream()
                .filter(pkg -> {
                    String name = pkg.getName();
                    return name != null && patterns.stream().anyMatch(pattern -> name.toLowerCase().startsWith(pattern.toLowerCase()));
                })
                .toList();

        if(!packagePlans.isEmpty()) {
            serviceGroup.setServices(packagePlans);
            serviceGroup.setGroupName(groupName);
            serviceGroups.add(serviceGroup);
        }
    }

    private void groupHandleV2(List<ServiceGroup> serviceGroups, String groupName, List<Package> dataPackages, List<String> patterns) {
        ServiceGroup serviceGroup = new ServiceGroup();

        List<Package> packagePlans = dataPackages.stream()
                .filter(pkg -> {
                    String name = pkg.getCode();
                    return name != null && patterns.stream().anyMatch(pattern -> name.toLowerCase().equals(pattern.toLowerCase()));
                })
                .toList();

        if(!packagePlans.isEmpty()) {
            serviceGroup.setServices(packagePlans);
            serviceGroup.setGroupName(groupName);
            serviceGroups.add(serviceGroup);
        }
    }

    private <T extends Enum<T>> SelfcareResponse<List<ServiceGroup>> getFilteredPackagesV2(
            String isdn, String language, Class<T> enumClass, Function<T, String> typeExtractor) {

        SelfcareResponse<List<ServiceGroup>> selfcareResponse = natcomSelfcareService.wsGetServices(isdn, language);
        if (selfcareResponse == null || selfcareResponse.getWsResponse() == null || selfcareResponse.getWsResponse().isEmpty())
            return selfcareResponse;

        List<ServiceGroup> filteredGroups = getFilteredPackagesHandle(enumClass, typeExtractor, selfcareResponse);

        if(filteredGroups != null && !filteredGroups.isEmpty())
            for(ServiceGroup serviceGroup : filteredGroups) {
                List<Package> packagePlans = serviceGroup.getServices();
                if(packagePlans != null && !packagePlans.isEmpty())
                    for(Package p : packagePlans)
                        handleAddSubPackageVas(p);
            }

        selfcareResponse.setWsResponse(filteredGroups);
        return selfcareResponse;
    }

    private <T extends Enum<T>> SelfcareResponse<List<ServiceGroup>> getFilteredPackages(
            String isdn, String language, Class<T> enumClass, Function<T, String> typeExtractor) {

        SelfcareResponse<List<ServiceGroup>> selfcareResponse = natcomSelfcareService.wsGetServices(isdn, language);
        if (selfcareResponse == null || selfcareResponse.getWsResponse() == null || selfcareResponse.getWsResponse().isEmpty())
            return selfcareResponse;

        List<ServiceGroup> filteredGroups = getFilteredPackagesHandle(enumClass, typeExtractor, selfcareResponse);
        selfcareResponse.setWsResponse(filteredGroups);
        return selfcareResponse;
    }

    private <T extends Enum<T>> List<ServiceGroup> getFilteredPackagesHandle(Class<T> enumClass, Function<T, String> typeExtractor, SelfcareResponse<List<ServiceGroup>> selfcareResponse) {
        List<String> validGroupCodes = Arrays.stream(enumClass.getEnumConstants())
                .map(typeExtractor)
                .toList();

        return selfcareResponse.getWsResponse().stream()
                .filter(group -> validGroupCodes.contains(group.getGroupCode()))
                .toList();
    }
}
