package com.ringme.controller.nologin;

import com.ringme.config.LocaleFactory;
import com.ringme.dto.record.Response;
import com.ringme.dto.ringme.selfcare.UserInfo;
import com.ringme.service.ringme.JwtService;
import com.ringme.service.ringme.OTPService;
import com.ringme.service.ringme.UserInfoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequestMapping("/without-bearer")
public class WithoutBearerController {
    @Autowired
    JwtService jwtService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    OTPService otpSender;
    @Autowired
    LocaleFactory languageFactory;

    @PostMapping(value = "/get-selfcare-jwt")
    public ResponseEntity<?> getSeftcareJWT(
            @RequestParam String isdn,
            @RequestParam long timestamp,
            @RequestParam String cert
    ) {
        if(isdn.startsWith("+509"))
            isdn = isdn.replace("+509", "");

        UserInfo userInfo = userInfoService.validateUserByVTM(isdn, timestamp, cert);
        if (userInfo == null)
            return ResponseEntity.status(403).body(new Response(400, "Superapp token is invalid"));

        String jwtToken = jwtService.generateToken(userInfo);
        log.info("userInfo {} request-jwt success", userInfo);
        return ResponseEntity.ok(new Response(200, jwtToken));
    }

    @PostMapping(value = "/get-selfcare-jwt-test")
    public ResponseEntity<?> getSeftcareJwtTest(
            @RequestParam String msisdn,
            @RequestParam long timestamp,
            @RequestParam String security
    ) {
        return ResponseEntity.ok(userInfoService.callApiValidateUserByVTMTest(msisdn, timestamp, security));
    }

    @PostMapping(value = "/test-sms")
    public ResponseEntity<?> testSMS(
            @RequestParam String msisdn,
            @RequestParam String content
    ) {
        otpSender.sendOTP(msisdn, content);
        return ResponseEntity.ok(new Response(200, "success"));
    }
    @PostMapping(value = "/test-sms2")
    public ResponseEntity<?> testSMSV2(
            @RequestParam String msisdn
    ) {
        String content = languageFactory.getMessage("send-otp-content", "ht");
        //otpSender.sendOTP(msisdn, content.replace("{{otp}}", "version-01"));
        otpSender.sendOTP(msisdn, content.replace("{{otp}}", "TEST-ht"));

        content = languageFactory.getMessage("send-otp-content", "en");
        otpSender.sendOTP(msisdn, content.replace("{{otp}}", "TEST-en"));

        return ResponseEntity.ok(new Response(200, "success"));
    }
}
