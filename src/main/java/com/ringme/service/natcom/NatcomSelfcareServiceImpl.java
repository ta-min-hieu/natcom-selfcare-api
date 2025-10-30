package com.ringme.service.natcom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ringme.config.AppConfig;
import com.ringme.dto.natcom.selfcare.request.SelfcareRequest;
import com.ringme.dto.natcom.selfcare.request.WsRequest;
import com.ringme.dto.natcom.selfcare.response.*;
import com.ringme.dto.natcom.selfcare.response.accounts.detail.AccountsDetail;
import com.ringme.dto.natcom.selfcare.response.packages.DataPlus;
import com.ringme.dto.natcom.selfcare.response.packages.FtthPackage;
import com.ringme.dto.natcom.selfcare.response.packages.Package;
import com.ringme.dto.natcom.selfcare.response.packages.PackageInfo;
import com.ringme.dto.natcom.selfcare.response.service.detail.ServiceDetail;
import com.ringme.dto.natcom.selfcare.response.service.detail.ServiceGroup;
import com.ringme.enums.selfcare.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Log4j2
@Service
public final class NatcomSelfcareServiceImpl implements NatcomSelfcareService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    AppConfig appConfig;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public <T> T callApiSelfcare(String url, Object body, TypeReference<T> responseType) {
        ResponseEntity<String> response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", appConfig.getSelfcareBearerToken());

            HttpEntity<?> entity = new HttpEntity<>(body, headers);

            response = restTemplate.postForEntity(url, entity, String.class);

//            log.info("Success url: {}, body: {}, responseType: {}, response: {}", url, body, responseType, response);
            return objectMapper.readValue(response.getBody(), responseType);
        } catch (Exception e) {
            log.error("Error url: {}, body: {}, responseType: {}, response: {}, error: {}",  url, body, responseType, response, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public SelfcareResponse<SubMainInfo> wsGetSubMainInfo(String isdn, String language) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, language);

        return callApiSelfcare(appConfig.getSelfcareGetSubMainInfo(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<SubAccountInfo> wsGetSubAccountInfo(String isdn, SubType subType, String language) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, subType, language);

        return callApiSelfcare(appConfig.getSelfcareSubAccountInfo(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<AccountsDetail>> wsGetAccountsDetail(String isdn, SubType subType, String language) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, subType, language);

        return callApiSelfcare(appConfig.getSelfcareAccountsDetail(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<Object> wsDoActionService(String isdn, String language, String serviceCode, ActionType actionType) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, language, serviceCode, actionType, "SuperApp");
        String url = appConfig.getSelfcareDoActionService();
        log.info("request: {}, url: {}", request, url);
        SelfcareResponse<Object> response = callApiSelfcare(url, request, new TypeReference<>() {});
        log.info("response: {}, isdn: {}", response, isdn);
        return response;
    }

    @Override
    public SelfcareResponse<PostageInfo> wsGetPostageInfo(String isdn, SubType subType, String language, long startDate, long endDate, String type) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, subType, language, startDate, endDate, type);

        return callApiSelfcare(appConfig.getSelfcarePostageInfo(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<PostageDetailInfo>> wsGetPostageDetailInfo(String isdn, PostType postType, int pageSize, int pageNum, long startDate, long endDate) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, null, postType, pageSize, pageNum, null, startDate, endDate);

        return callApiSelfcare(appConfig.getSelfcarePostageDetailInfo(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<Object> wsDoRecharge(String isdn, String language, int desIsdn, String serial) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, language, desIsdn, serial);

        return callApiSelfcare(appConfig.getSelfcareDoRecharge(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<Object> wsGetRefillHistoryInfo(String subId, String language, int pageSize, int pageNum, long startDate, long endDate, SortType sort) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, subId, language, pageSize, pageNum, startDate, endDate, sort);

        return callApiSelfcare(appConfig.getSelfcareRefillHistoryInfo(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<NearestStore>> wsGetNearestStores(String longitude, String latitude, String provinceId, String districtId) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, longitude, latitude, provinceId, districtId);

        return callApiSelfcare(appConfig.getSelfcareNearestStores(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<Package>> wsGetAllDataPackages(String isdn, String language) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, language);

        return callApiSelfcare(appConfig.getSelfcareAllDataPackages(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<FtthPackage>> wsGetAllFtthPackages() {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest();
        request.setWsCode(wsCode);
        request.setWsRequest(new WsRequest());

        return callApiSelfcare(appConfig.getSelfcareAllFtthPackages(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<FtthPackage> wsGetFtthPackageById(long id) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest();
        request.setWsCode(wsCode);
        request.setWsRequest(new WsRequest());
        request.getWsRequest().setId(id);

        return callApiSelfcare(appConfig.getSelfcareFtthPackageById(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<Package> wsGetDataPackageInfo(String isdn, String language, String packageCode) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, language);
        request.getWsRequest().setPackageCode(packageCode);

        return callApiSelfcare(appConfig.getSelfcareDataPackageInfo(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<Package>> wsGetServicesByGroupType(String isdn, ServiceGroupType serviceGroupType, String language, String token) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, language);
        request.getWsRequest().setServiceGroupType(serviceGroupType);
        request.setToken(token);

        return callApiSelfcare(appConfig.getSelfcareServicesByGroupType(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<Object> wsGiftDataToFriend(String isdn, String language, String msisdnSend, String command, String msisdnRecy) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, language);
        request.getWsRequest().setMsisdnSend(msisdnSend);
        request.getWsRequest().setCommand(command);
        request.getWsRequest().setMsisdnRecv(msisdnRecy);

        String url = appConfig.getSelfcareGiftDataToFriend();
        log.info("request: {}, url: {}", request, url);
        SelfcareResponse<Object> response = callApiSelfcare(url, request, new TypeReference<>() {});
        log.info("response: {}, isdn: {}", response, isdn);
        return response;
    }

    @Override
    public SelfcareResponse<Object> wsGiftXchangeToFriend(String isdn, String language, String msisdnSend, String command, String msisdnRecy) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, language);
        request.getWsRequest().setMsisdnSend(msisdnSend);
        request.getWsRequest().setCommand(command);
        request.getWsRequest().setMsisdnRecv(msisdnRecy);

        return callApiSelfcare(appConfig.getSelfcareGiftXchangeToFriend(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<Package>> wsGetServicesByGroupExchange(String isdn, ServiceGroupType serviceGroupType, String language, String token) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, language);
        request.getWsRequest().setServiceGroupType(serviceGroupType);
        request.setToken(token);

        return callApiSelfcare(appConfig.getSelfcareServicesByGroupExchange(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<Object> wsIshare(String isdn, String receiveIsdn, String amount, String language) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, language);
        request.getWsRequest().setReceiveIsdn(receiveIsdn);
        request.getWsRequest().setAmount(amount);

        return callApiSelfcare(appConfig.getSelfcareIshare(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<DataPlus>> wsGetDataVolumeLevelToBuy(String isdn, String packageCode, String language) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, language);
        request.getWsRequest().setPackageCode(packageCode);

        return callApiSelfcare(appConfig.getSelfcareDataVolumeLevelToBuy(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<Object> wsDoBuyData(String isdn, String packageCode, String price, int volume) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, null);
        request.getWsRequest().setPrice((int) Double.parseDouble(price));
        request.getWsRequest().setVolume(volume);
        request.getWsRequest().setPackageCode(packageCode);

        return callApiSelfcare(appConfig.getSelfcareDoBuyData(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<ProvinceDistrict>> wsGetProvinces() {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest();
        request.setWsCode(wsCode);
        request.setWsRequest(new WsRequest());

        return callApiSelfcare(appConfig.getSelfcareProvinces(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<ProvinceDistrict>> wsGetDistricts(String provinceId) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest();
        request.setWsCode(wsCode);
        WsRequest wsRequest = new WsRequest();
        wsRequest.setProvinceId(provinceId);
        request.setWsRequest(wsRequest);

        return callApiSelfcare(appConfig.getSelfcareDistricts(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<StoreInfo>> wsFindStoreByAddr(String longitude, String latitude, String provinceId, String districtId) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest();
        request.setWsCode(wsCode);
        WsRequest wsRequest = new WsRequest();
        wsRequest.setProvinceId(provinceId);
        wsRequest.setDistrictId(districtId);
        wsRequest.setLongitude(longitude);
        wsRequest.setLatitude(latitude);
        request.setWsRequest(wsRequest);

        return callApiSelfcare(appConfig.getSelfcareFindStoreByAddr(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<ServiceGroup>> wsGetServicesV1(String isdn, String language) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, language);

        return callApiSelfcare(appConfig.getSelfcareServicesV1(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<ServiceGroup>> wsGetServices(String isdn, String language) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, language);

        return callApiSelfcare(appConfig.getSelfcareServices(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<Package>> wsGetServicesByGroup(String isdn, String language, String serviceGroupId) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, language);
        request.getWsRequest().setServiceGroupId(serviceGroupId);

        return callApiSelfcare(appConfig.getSelfcareServicesByGroup(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<ServiceDetail> wsGetServiceDetail(String isdn, String language, String serviceCode) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, language);
        request.getWsRequest().setServiceCode(serviceCode);

        return callApiSelfcare(appConfig.getSelfcareServiceDetail(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<Package>> wsGetCurrentUsedServices(String subId, String language, SubType subType, String isdn) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, "509" + isdn, subType, language);
        request.getWsRequest().setSubId(subId);

        return callApiSelfcare(appConfig.getSelfcareCurrentUsedServices(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<List<Package>> wsGetCurrentUsedVas(String isdn, String language) {
        String wsCode = Thread.currentThread().getStackTrace()[1].getMethodName();
        SelfcareRequest request = new SelfcareRequest(wsCode, isdn, null, language);

        return callApiSelfcare(appConfig.getSelfcareCurrentUsedVasServices(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<Object> paymentMobileServiceAndVasFree(String language, String isdn, String packageCode) {
        SelfcareRequest request = new SelfcareRequest();
        request.setWsCode("wsRegData");
        WsRequest wsRequest = new WsRequest();
        wsRequest.setIsdn(isdn);
        wsRequest.setLanguage(language);
        wsRequest.setPackageCode(packageCode);
        request.setWsRequest(wsRequest);

        return callApiSelfcare(appConfig.getSelfcareRegDataFree(), request, new TypeReference<>() {});
    }

    @Override
    public SelfcareResponse<Object> paymentMobileServiceAndVasFree(String language, String isdner, String isdnee, String packageCode) {
        SelfcareRequest request = new SelfcareRequest();
        request.setWsCode("wsRegData");
        WsRequest wsRequest = new WsRequest();
        wsRequest.setSharer(isdner);
        wsRequest.setIsdn(isdnee);
        wsRequest.setLanguage(language);
        wsRequest.setPackageCode(packageCode);
        request.setWsRequest(wsRequest);

        return callApiSelfcare(appConfig.getSelfcareRegDataFree(), request, new TypeReference<>() {});
    }
}
