package com.ringme.common;

import com.ringme.config.AppConfig;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Log4j2
@Component
public class Common {
    private static final Marker CDR = MarkerManager.getMarker("CDR");

    @Autowired
    AppConfig appConfig;

    public static final List<String> whiteList = List.of("43713230", "43713224", "43955556", "42133878", "42416176"); // , "42416176"

    public String urlHandler(String url) {
        if (url == null || url.isEmpty())
            return null;
        else if(url.startsWith("http"))
            return url;
        else
            return appConfig.getCdnDomainUrl() + url;
    }

    public String getTime() {
        long timestampMillis = System.currentTimeMillis() + appConfig.getServerTimeAdd(); // ví dụ: 1726450800000L

        LocalDateTime dateTime = Instant.ofEpochMilli(timestampMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        String formatted = dateTime.format(formatter);

        System.out.println(formatted);
        return formatted;
    }

    public static void logCdr(String type, String msisdner1, String namePackage, String amount, String msisdner2) {
        if(msisdner1 != null && !msisdner1.isEmpty() && !msisdner1.startsWith("509"))
            msisdner1 = "509" + msisdner1;

        if(msisdner2 != null && !msisdner2.isEmpty() && !msisdner2.startsWith("509"))
            msisdner2 = "509" + msisdner2;

        log.info(CDR, "{}|{}|{}|{}|{}", type, msisdner1, namePackage, amount, msisdner2);
    }
}
