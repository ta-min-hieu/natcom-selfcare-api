package com.ringme.dto.natcom.selfcare.request;

import com.ringme.enums.selfcare.ActionType;
import com.ringme.enums.selfcare.PostType;
import com.ringme.enums.selfcare.SortType;
import com.ringme.enums.selfcare.SubType;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelfcareRequest {
    private String wsCode;
    private WsRequest wsRequest;

    private String token; // Ai5GTS/Zs8mihUZLK8PI9w==

    public SelfcareRequest(String wsCode, String isdn, SubType subType, String language) {
        WsRequest wsRequest = new WsRequest(isdn, subType, language);

        setDefaultSelfcareRequest(wsCode, wsRequest);
    }

    public SelfcareRequest(String wsCode, String isdn, String language, String serviceCode, ActionType actionType, String channel) {
        WsRequest wsRequest = new WsRequest(isdn, null, language);
        wsRequest.setServiceCode(serviceCode);
        wsRequest.setActionType(actionType);
        wsRequest.setChannel(channel);

        setDefaultSelfcareRequest(wsCode, wsRequest);
    }

    public SelfcareRequest(String wsCode, String isdn, SubType subType, String language, long startDate, long endDate, String type) {
        WsRequest wsRequest = new WsRequest(isdn, subType, language);
        wsRequest.setStartDate(startDate);
        wsRequest.setEndDate(endDate);
        wsRequest.setType(type);

        setDefaultSelfcareRequest(wsCode, wsRequest);
    }

    public SelfcareRequest(String wsCode, String isdn, SubType subType, String language, PostType postType, int pageSize, int pageNum, SortType sort, long startDate, long endDate) {
        WsRequest wsRequest = new WsRequest(isdn, subType, language);
        wsRequest.setPostType(postType);

        setDatePageInRequest(wsRequest, startDate, endDate, pageSize, pageNum, sort);

        setDefaultSelfcareRequest(wsCode, wsRequest);
    }

    public SelfcareRequest(String wsCode, String isdn, String language, int desIsdn, String serial) {
        WsRequest wsRequest = new WsRequest(isdn, null, language);
        wsRequest.setDesIsdn(desIsdn);
        wsRequest.setSerial(serial);

        setDefaultSelfcareRequest(wsCode, wsRequest);
    }

    public SelfcareRequest(String wsCode, String subId, String language, int pageSize, int pageNum, long startDate, long endDate, SortType sort) {
        WsRequest wsRequest = new WsRequest();
        wsRequest.setSubId(subId);
        wsRequest.setLanguage(language);

        setDatePageInRequest(wsRequest, startDate, endDate, pageSize, pageNum, sort);

        setDefaultSelfcareRequest(wsCode, wsRequest);
    }

    public SelfcareRequest(String wsCode, String longitude, String latitude, String provinceId, String districtId) {
        WsRequest wsRequest = new WsRequest();
        wsRequest.setLongitude(longitude);
        wsRequest.setLatitude(latitude);
        wsRequest.setProvinceId(provinceId);
        wsRequest.setDistrictId(districtId);

        setDefaultSelfcareRequest(wsCode, wsRequest);
    }

    private void setDefaultSelfcareRequest(String wsCode, WsRequest wsRequest) {
        this.wsCode = wsCode;
        this.wsRequest = wsRequest;
    }

    private void setDatePageInRequest(WsRequest wsRequest, long startDate, long endDate, int pageSize, int pageNum, SortType sort) {
        wsRequest.setStartDate(startDate);
        wsRequest.setEndDate(endDate);
        wsRequest.setPageSize(pageSize);
        wsRequest.setPageNum(pageNum);
        wsRequest.setSort(sort);
    }
}
