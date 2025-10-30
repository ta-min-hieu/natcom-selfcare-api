package com.ringme.service.ringme;

import com.ringme.dto.natcom.selfcare.response.SelfcareResponse;
import com.ringme.dto.natcom.selfcare.response.service.detail.ServiceGroup;
import com.ringme.dto.natcom.selfcare.response.service.detail.ServiceTeam;
import com.ringme.dto.record.Response;
import com.ringme.dto.ringme.selfcare.AccountInfo;
import com.ringme.dto.ringme.ftth.FtthRegister;
import com.ringme.dto.ringme.selfcare.AccountInfoDetail;
import com.ringme.dto.ringme.selfcare.BannerDto;
import com.ringme.enums.selfcare.ActionType;
import com.ringme.enums.selfcare.FtthRegisterStatus;
import com.ringme.enums.selfcare.SubType;
import com.ringme.enums.selfcare.mobileServices.MobileServices;
import com.ringme.model.selfcare.ScheduleCall;

import java.util.List;

public interface RingmeSelfcareService {
    List<BannerDto> getBanners();

    AccountInfo getAccountInfo(String isdn, String language, String vtAccId);

    SelfcareResponse<List<ServiceGroup>> getAllVasPackages(String isdn, String language);

    SelfcareResponse<List<ServiceGroup>> getAllPromotionPackages(String isdn, String language);

    SelfcareResponse<List<ServiceGroup>> getAllSmsPackages(String isdn, String language);

    boolean ftthSendARequestToRegister(String language, FtthRegister form);

    List<FtthRegister> getHistoryFtthRegisters(FtthRegisterStatus status, String startDate, String endDate);

    boolean scheduleCall(ScheduleCall scheduleCall);

    List<ServiceTeam> wsGetServices(String isdn, String language);

    List<ServiceGroup> getAllPackages(String isdn, String language, MobileServices type);

    AccountInfoDetail getAccountInfoDetail(String isdn, String language, SubType subType, String name);

    Response doRecharge(String language, int desIsdn, String serial, String captcha);

    Response ishare(String receiveIsdn, String amount, String language);

    Response doActionService(String language, String serviceCode, ActionType actionType);

    Response giftDataToFriend(String language, String command, String msisdnRecy);

    Response giftXchangeToFriend(String language, String command, String msisdnRecy);

    Response doBuyData(String language, String packageCode, String price, int volume);

    Response getDataPackageInfo(String isdn, String language, String packageCode);

    boolean isVasPackage(String packageCode);
}
