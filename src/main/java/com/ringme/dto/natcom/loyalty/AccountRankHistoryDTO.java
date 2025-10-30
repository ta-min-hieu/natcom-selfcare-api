package com.ringme.dto.natcom.loyalty;

import lombok.Data;

import java.util.Date;

@Data
public class AccountRankHistoryDTO {
    private Long hisId;
    private Long vtAccId;
    private Date createDate;
    private Long rankIdOld;
    private Long rankIdNew;
    private String userCreate;
    private String rankNameOld;
    private String rankNameNew;
}
