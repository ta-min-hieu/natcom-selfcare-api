package com.ringme.dto.natcom.loyalty;

import lombok.Data;

import java.util.Date;

@Data
public class RankDefineDTO {
    private Long rankId;
    private String rankName;
    private Long minPoint;
    private Long maxPoint;
    private String userUpdate;
    private Date updateDate;
    private Long tenant;
    private String description;
    private String usedTimeDescription;
}
