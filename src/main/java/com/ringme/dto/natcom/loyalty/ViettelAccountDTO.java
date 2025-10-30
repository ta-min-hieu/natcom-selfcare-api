package com.ringme.dto.natcom.loyalty;

import com.ringme.enums.loyalty.LoyaltyStatus;
import lombok.Data;

import java.util.Date;

@Data
public class ViettelAccountDTO {
    private Long vtAccId;
    private String accountName;
    private Date birthDate;
    private String gender;
    private String address;
    private Date createDate;
    private String provinceCode;
    private String districtCode;
    private String precinctCode;
    private String addressCode;
    private Integer countryCode;
    private LoyaltyStatus status;
    private String phoneNumber;
    private String idNo;
    private Date issueDate;
    private String issuePlace;
    private String accountType;
    private String email;
    private Long tenant;
    private Long accountRankId;
    private Date startDate;
    private Date endDate;
    private Long rankId;
    private String rankName;
}
