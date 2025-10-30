package com.ringme.dto.natcom.loyalty;

import lombok.Data;

import java.util.Date;

@Data
public class AccountRankDTO {
    private Long accountRankId;
    private Long vtAccId;
    private Integer rankId;
    private String rankName;
    private Date startDate;
    private Date endDate;
    private Double pointValue;
    private Date pointExpireDate;
}
