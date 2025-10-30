package com.ringme.dto.natcom.selfcare.request;

import com.ringme.enums.selfcare.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WsRequest {
    private String isdn;
    private SubType subType; // 1: trả trước, 2: trả sau
    private String language;

    private String serviceCode;
    private ActionType actionType;     // 0 DK, 1 Huy
    private String channel;

    private Long startDate;
    private Long endDate;
    private String type;

    private PostType postType;   // 0: Call, 1: SMS, 2: Others
    private Integer pageSize;
    private Integer pageNum;
    private SortType sort;

    private Integer desIsdn; // Số được nạp thẻ
    private String serial; // Mã thẻ cào

    private String subId;

    private String longitude;
    private String latitude;

    private String packageCode;

    private ServiceGroupType serviceGroupType; // (3): Xchange, (4): Data

    // gift a package to other
    private String msisdnSend;
    private String command;
    private String msisdnRecv;

    // Is share
    private String receiveIsdn;
    private String amount;

    private String provinceId;
    private String districtId;

    private String serviceGroupId;

    private int price;
    private int volume;

    private long id;

    private String sharer; // số điện thoại người share gói cước qua ví

    public WsRequest(String isdn, SubType subType, String language) {
        this.isdn = isdn;
        this.subType = subType;
        this.language = language;
    }
}
