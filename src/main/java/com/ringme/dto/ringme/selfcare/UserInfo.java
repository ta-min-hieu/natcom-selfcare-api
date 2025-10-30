package com.ringme.dto.ringme.selfcare;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ringme.dto.natcom.loyalty.LoyaltyResponse;
import com.ringme.dto.natcom.loyalty.ViettelAccountDTO;
import com.ringme.dto.natcom.selfcare.response.SelfcareResponse;
import com.ringme.dto.natcom.selfcare.response.SubMainInfo;
import com.ringme.enums.selfcare.SubType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String isdn;
    private String name;
    private String avatar;
    private SubType subType;
    private String language;

    private long subId;
    private Long vtAccId;

    public UserInfo(String isdn, SelfcareResponse<SubMainInfo> mainInfoR, LoyaltyResponse<List<ViettelAccountDTO>> viettelAccountR) {
        this.isdn = isdn;
        if(mainInfoR != null) {
            SubMainInfo subMainInfo = mainInfoR.getWsResponse();
            if(subMainInfo != null) {
                name = subMainInfo.getName();
                avatar = subMainInfo.getAvatarUrl();
                subType = subMainInfo.getSubType();
                subId = subMainInfo.getSubId();
                language = subMainInfo.getLanguage();
            }
        }

        if(viettelAccountR != null) {
            log.info("viettelAccountRrrrrr: {}", viettelAccountR);
            List<ViettelAccountDTO> list = viettelAccountR.getData(new TypeReference<>() {});
            if(list != null && !list.isEmpty())
                vtAccId = list.getFirst().getVtAccId();
        }
    }
}
