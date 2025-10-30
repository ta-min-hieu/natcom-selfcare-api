package com.ringme.service.ringme;

import com.ringme.config.LocaleFactory;
import com.ringme.dto.record.Response;
import com.ringme.dto.ringme.natfund.NatFundRequest;
import com.ringme.dto.ringme.natfund.NatFundResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
public class NatFundService {
    @Value("${nat-fund.url}")
    private String url;
    @Value("${nat-fund.username}")
    private String username;
    @Value("${nat-fund.password}")
    private String password;
    @Value("${nat-fund.source}")
    private String source;

    @Autowired
    OTPService otpService;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    LocaleFactory localeFactory;

    public Response handleDonate(String language, String isdn, double amount) {
        if(amount > 0)
            amount = -1 * amount;

        if(amount > -1) {
            log.error("amount invalid| language: {}, isdn: {}, amount: {}", language, isdn, amount);
            return new Response(1, localeFactory.getMessage("amount.invalid"));
        }

        NatFundResponse response = callApiDonate(isdn, amount);
        if(response == null) {
            log.error("System error| language: {}, isdn: {}, amount: {}", language, isdn, amount);
            return new Response(2, localeFactory.getMessage("system.error"));
        }

        if(!response.getErrorCode().equals("0")) {
            log.error("Donate error| language: {}, isdn: {}, amount: {}, response: {}", language, isdn, amount, response);
            otpService.sendOTP(isdn, localeFactory.getMessage("donate.content.fail", language));
            return new Response(3, localeFactory.getMessage("donate.fail", language));
//            return new Response(3, response.getDescription());
        }

        log.info("success| language: {}, isdn: {}, amount: {}", language, isdn, amount);
        otpService.sendOTP(isdn, localeFactory.getMessage("donate.content.success", language));
        return new Response(200, localeFactory.getMessage("donate.success", language));
    }

    private NatFundResponse callApiDonate(String isdn, double amount) {
        ResponseEntity<NatFundResponse> response = null;
        NatFundRequest request = new NatFundRequest();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            request.setUserName(username);
            request.setPassword(password);
            request.setSource(source);
            request.setMsisdn(isdn);
            request.setAddBalance(String.format("%.2f", amount));

            HttpEntity<?> entity = new HttpEntity<>(request, headers);

            response = restTemplate.postForEntity(url, entity, NatFundResponse.class);

            log.info("Success url: {}, request: {}, response: {}", url, request, response);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error url: {}, request: {}, response: {}, error: {}",  url, request, response, e.getMessage(), e);
        }
        return null;
    }
}
