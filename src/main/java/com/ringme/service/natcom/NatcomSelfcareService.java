package com.ringme.service.natcom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ringme.dto.natcom.selfcare.response.*;
import com.ringme.dto.natcom.selfcare.response.accounts.detail.AccountsDetail;
import com.ringme.dto.natcom.selfcare.response.packages.DataPlus;
import com.ringme.dto.natcom.selfcare.response.packages.FtthPackage;
import com.ringme.dto.natcom.selfcare.response.packages.Package;
import com.ringme.dto.natcom.selfcare.response.packages.PackageInfo;
import com.ringme.dto.natcom.selfcare.response.service.detail.ServiceDetail;
import com.ringme.dto.natcom.selfcare.response.service.detail.ServiceGroup;
import com.ringme.enums.selfcare.*;

import java.util.List;

public sealed interface NatcomSelfcareService permits NatcomSelfcareServiceImpl {
    <T> T callApiSelfcare(String url, Object body, TypeReference<T> responseType);

    SelfcareResponse<SubMainInfo> wsGetSubMainInfo(String isdn, String language);

    SelfcareResponse<SubAccountInfo> wsGetSubAccountInfo(String isdn, SubType subType, String language);

    SelfcareResponse<List<AccountsDetail>> wsGetAccountsDetail(String isdn, SubType subType, String language);

    SelfcareResponse<Object> wsDoActionService(String isdn, String language, String serviceCode, ActionType actionType);

    SelfcareResponse<PostageInfo> wsGetPostageInfo(String isdn, SubType subType, String language, long startDate, long endDate, String type);

    SelfcareResponse<List<PostageDetailInfo>> wsGetPostageDetailInfo(String isdn, PostType postType, int pageSize, int pageNum, long startDate, long endDate);

    SelfcareResponse<Object> wsDoRecharge(String isdn, String language, int desIsdn, String serial);

    SelfcareResponse<Object> wsGetRefillHistoryInfo(String subId, String language, int pageSize, int pageNum, long startDate, long endDate, SortType sort); // chưa có dữ liệu test

    SelfcareResponse<List<NearestStore>> wsGetNearestStores(String longitude, String latitude, String provinceId, String districtId);

    SelfcareResponse<List<Package>> wsGetAllDataPackages(String isdn, String language);

    SelfcareResponse<List<FtthPackage>> wsGetAllFtthPackages();

    SelfcareResponse<FtthPackage> wsGetFtthPackageById(long id);

    SelfcareResponse<Package> wsGetDataPackageInfo(String isdn, String language, String packageCode);

    SelfcareResponse<List<Package>> wsGetServicesByGroupType(String isdn, ServiceGroupType serviceGroupType, String language, String token); // 3.12, 3.14

    SelfcareResponse<Object> wsGiftDataToFriend(String isdn, String language, String msisdnSend, String command, String msisdnRecy);

    SelfcareResponse<Object> wsGiftXchangeToFriend(String isdn, String language, String msisdnSend, String command, String msisdnRecy);

    SelfcareResponse<List<Package>> wsGetServicesByGroupExchange(String isdn, ServiceGroupType serviceGroupType, String language, String token);

    SelfcareResponse<Object> wsIshare(String isdn, String receiveIsdn, String amount, String language);

    SelfcareResponse<List<DataPlus>> wsGetDataVolumeLevelToBuy(String isdn, String packageCode, String language);

    SelfcareResponse<Object> wsDoBuyData(String isdn, String packageCode, String price, int volume);

    SelfcareResponse<List<ProvinceDistrict>> wsGetProvinces();

    SelfcareResponse<List<ProvinceDistrict>> wsGetDistricts(String provinceId);

    SelfcareResponse<List<StoreInfo>> wsFindStoreByAddr(String longitude, String latitude, String provinceId, String districtId);

    SelfcareResponse<List<ServiceGroup>> wsGetServicesV1(String isdn, String language);

    SelfcareResponse<List<ServiceGroup>> wsGetServices(String isdn, String language);

    SelfcareResponse<List<Package>> wsGetServicesByGroup(String isdn, String language, String serviceGroupId);

    SelfcareResponse<ServiceDetail> wsGetServiceDetail(String isdn, String language, String serviceCode);

    SelfcareResponse<List<Package>> wsGetCurrentUsedServices(String subId, String language, SubType subType, String isdn);

    SelfcareResponse<List<Package>> wsGetCurrentUsedVas(String isdn, String language);

    SelfcareResponse<Object> paymentMobileServiceAndVasFree(String language, String isdn, String packageCode);

    SelfcareResponse<Object> paymentMobileServiceAndVasFree(String language, String isdner, String isdnee, String packageCode);
}
