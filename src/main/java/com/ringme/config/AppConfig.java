package com.ringme.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AppConfig {
    @Value("${server.base-url}")
    private String baseUrl;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${server.time-add}")
    private long serverTimeAdd;

    @Value("${cdn.domain.url}")
    private String cdnDomainUrl;

    @Value("${isdn.coutry-code}")
    private String isdnCoutryCode;

    @Value("${selfcare.bearer-token}")
    private String selfcareBearerToken;

    @Value("${selfcare.gift.token}")
    private String selfcareGiftToken;

    @Value("${api.validate-superapp-token-by-vtm}")
    private String apiValidateSuperappTokenByVTM;

    @Value("${selfcare.url.wsGetSubMainInfo}")
    private String selfcareGetSubMainInfo;
    @Value("${selfcare.url.wsGetSubAccountInfo}")
    private String selfcareSubAccountInfo;
    @Value("${selfcare.url.wsGetAccountsDetail}")
    private String selfcareAccountsDetail;
    @Value("${selfcare.url.wsDoActionService}")
    private String selfcareDoActionService;
    @Value("${selfcare.url.wsGetPostageInfo}")
    private String selfcarePostageInfo;
    @Value("${selfcare.url.wsGetPostageDetailInfo}")
    private String selfcarePostageDetailInfo;
    @Value("${selfcare.url.wsDoRecharge}")
    private String selfcareDoRecharge;
    @Value("${selfcare.url.wsGetRefillHistoryInfo}")
    private String selfcareRefillHistoryInfo;
    @Value("${selfcare.url.wsGetNearestStores}")
    private String selfcareNearestStores;
    @Value("${selfcare.url.wsGetAllDataPackages}")
    private String selfcareAllDataPackages;
    @Value("${selfcare.url.wsGetDataPackageInfo}")
    private String selfcareDataPackageInfo;
    @Value("${selfcare.url.wsGetServicesByGroupType}")
    private String selfcareServicesByGroupType;
    @Value("${selfcare.url.wsGiftDataToFriend}")
    private String selfcareGiftDataToFriend;
    @Value("${selfcare.url.wsGiftXchangeToFriend}")
    private String selfcareGiftXchangeToFriend;
    @Value("${selfcare.url.wsGetServicesByGroupExchange}")
    private String selfcareServicesByGroupExchange;
    @Value("${selfcare.url.wsIshare}")
    private String selfcareIshare;
    @Value("${selfcare.url.wsGetDataVolumeLevelToBuy}")
    private String selfcareDataVolumeLevelToBuy;
    @Value("${selfcare.url.wsDoBuyData}")
    private String selfcareDoBuyData;
    @Value("${selfcare.url.wsGetProvinces}")
    private String selfcareProvinces;
    @Value("${selfcare.url.wsGetDistricts}")
    private String selfcareDistricts;
    @Value("${selfcare.url.wsFindStoreByAddr}")
    private String selfcareFindStoreByAddr;
    @Value("${selfcare.url.wsGetServicesV1}")
    private String selfcareServicesV1;
    @Value("${selfcare.url.wsGetServices}")
    private String selfcareServices;
    @Value("${selfcare.url.wsGetServicesByGroup}")
    private String selfcareServicesByGroup;
    @Value("${selfcare.url.wsGetServiceDetail}")
    private String selfcareServiceDetail;
    @Value("${selfcare.url.wsGetCurrentUsedServices}")
    private String selfcareCurrentUsedServices;
    @Value("${selfcare.url.wsGetCurrentUsedVasServices}")
    private String selfcareCurrentUsedVasServices;
    @Value("${selfcare.url.wsGetAllFtthPackages}")
    private String selfcareAllFtthPackages;
    @Value("${selfcare.url.wsGetFtthPackageById}")
    private String selfcareFtthPackageById;
    @Value("${selfcare.url.wsRegData}")
    private String selfcareRegDataFree;

    @Value("${loyalty.url.loyaltyAccountService.getCustomerInfo}")
    private String loyaltyCustomerInfo;
    @Value("${loyalty.url.loyaltyAccountService.referProductBccsCustomer}")
    private String loyaltyReferProductBccsCustomer;
    @Value("${loyalty.url.loyaltyPointService.getPointTransferHistory}")
    private String loyaltyPointTransferHistory;
    @Value("${loyalty.url.loyaltyPointService.getAccountPointInfo}")
    private String loyaltyAccountPointInfo;
    @Value("${loyalty.url.loyaltyPointService.getTotalPoint}")
    private String loyaltyTotalPoint;
    @Value("${loyalty.url.loyaltyPointService.redeemPoint}")
    private String loyaltyRedeemPoint;
    @Value("${loyalty.url.loyaltyPointService.adjustAccountPoint}")
    private String loyaltyAdjustAccountPoint;
    @Value("${loyalty.url.loyaltyPointService.accountRankHis}")
    private String loyaltyAccountRankHis;
    @Value("${loyalty.url.loyaltyPointService.getListPointRankByMonth}")
    private String loyaltyListPointRankByMonth;
    @Value("${loyalty.url.loyaltyPointService.getListPointProductByMonth}")
    private String loyaltyListPointProductByMonth;
    @Value("${loyalty.url.loyaltyPointService.redeemPointLotteryCode}")
    private String loyaltyRedeemPointLotteryCode;
    @Value("${loyalty.url.loyaltyRankService.getRankDefineInfo}")
    private String loyaltyRankDefineInfo;
    @Value("${loyalty.url.loyaltyRewardService.getGiftInfo}")
    private String loyaltyGiftInfo;
    @Value("${loyalty.url.loyaltyRewardService.getAccountRankInfo}")
    private String loyaltyAccountRankInfo;

    //natcash
    @Value("${natcash.url.credential}")
    private String natcashCredential;
    @Value("${natcash.url.checkTransaction}")
    private String natcashCheckTransaction;
    @Value("${natcash.url.cancel-trans}")
    private String natcashCancelTransaction;

    @Value("${natcash.url.top-up.airtime}")
    private String natcashTopUpAirtime;
    @Value("${natcash.top-up.wscode}")
    private String natcashTopUpWscode;
    @Value("${natcash.top-up.role-id}")
    private String natcashTopUpRoleId;
    @Value("${natcash.top-up.username}")
    private String natcashTopUpUsername;
    @Value("${natcash.top-up.password}")
    private String natcashTopUpPassword;

    @Value("${ftth.base-url}")
    private String ftthBaseUrl;
    @Value("${ftth.username}")
    private String ftthUsername;
    @Value("${ftth.password}")
    private String ftthPassword;

    @Value("${survey.url}")
    private String surveyUrl;
}