package com.ringme.controller.app;

import com.ringme.dto.record.Response;
import com.ringme.enums.CaptchaKey;
import com.ringme.service.ringme.CaptchaService;
import com.ringme.service.ringme.JwtService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/captcha")
@Log4j2
public class CaptchaController {
    @Autowired
    CaptchaService captchaService;
    @Autowired
    JwtService jwtService;

    @PostMapping(value = "/generate-captcha")
    public ResponseEntity<?> generateCaptcha(
            @RequestParam CaptchaKey type
    ) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ Captcha: {}", isdn);
        String base64Image = captchaService.generateCaptcha(isdn, type);
        return ResponseEntity.ok(new Response(200, "Success", base64Image));
    }
}
