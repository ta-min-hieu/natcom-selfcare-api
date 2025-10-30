package com.ringme.dto.ringme.selfcare;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ringme.dto.natcom.loyalty.LoyaltyResponse;
import com.ringme.dto.natcom.selfcare.response.SelfcareResponse;
import com.ringme.dto.natcom.selfcare.response.SubAccountInfo;
import com.ringme.dto.natcom.selfcare.response.SubMainInfo;
import com.ringme.enums.selfcare.SubType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {
    private String name;
    private String isdn;
    private String avatarUrl;
    private Double mainAcc;
    private Double proAcc;
    private String dataVolume;
    private Double loyaltyPoints = 0d;
    private SubType subType;

    private String dataPackageCode;
    private Long subId;

    private String surveyFormId = "1001";
    private double topupTax = 10.00;
    private boolean natcashRecharge = true;
    private boolean natfund = false;

    public AccountInfo(String isdn, SelfcareResponse<SubMainInfo> mainInfo, SelfcareResponse<SubAccountInfo> accountInfo, LoyaltyResponse<Object> totalAccountPointR) {
        this.isdn = isdn;

        if(mainInfo != null) {
            SubMainInfo subMainInfo = mainInfo.getWsResponse();
            if(subMainInfo != null) {
                name = subMainInfo.getName();
                avatarUrl = subMainInfo.getAvatarUrl();
                subId = subMainInfo.getSubId();
                subType = subMainInfo.getSubType();
            }
        }

        if(accountInfo != null) {
            SubAccountInfo subAccountInfo = accountInfo.getWsResponse();
            if(subAccountInfo != null) {
                mainAcc = subAccountInfo.getMainAcc();
                proAcc = subAccountInfo.getProAcc();
                dataVolume = String.valueOf(subAccountInfo.getDataVolume());
                dataPackageCode = subAccountInfo.getDataPkgName();
                if(dataPackageCode != null && dataPackageCode.contains(","))
                    dataPackageCode = dataPackageCode.split(",")[0].trim();

            }
        }

        if(totalAccountPointR != null) {
            Double totalPointDto = totalAccountPointR.getData(new TypeReference<Double>() {});
            if(totalPointDto != null)
                loyaltyPoints = totalPointDto;
        }
    }
}
