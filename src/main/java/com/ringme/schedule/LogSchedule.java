package com.ringme.schedule;

import com.ringme.common.Common;
import com.ringme.enums.selfcare.LogCdr;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class LogSchedule {
    @Scheduled(cron = "0 0 * * * *")
    public void runEveryHour() {
        Common.logCdr(LogCdr.ROTATE_LOG.getType(), "", "", "", "");
    }
}
