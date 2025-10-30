package com.ringme.model.selfcare;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ringme.enums.selfcare.ScheduleCallSupportType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ScheduleCall {
    private Long id;
    private ScheduleCallSupportType supportType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    private String isdn;
    private String language;
    private Date createdAt;
}
