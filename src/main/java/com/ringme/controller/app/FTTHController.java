package com.ringme.controller.app;

import com.ringme.config.LocaleFactory;
import com.ringme.dto.record.Response;
import com.ringme.enums.CaptchaKey;
import com.ringme.service.natcom.FtthService;
import com.ringme.service.ringme.CaptchaService;
import com.ringme.service.ringme.JwtService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/ftth")
public class FTTHController {
    @Autowired
    FtthService service;
    @Autowired
    CaptchaService captchaService;
    @Autowired
    JwtService jwtService;
    @Autowired
    LocaleFactory localeFactory;

    @PostMapping("/find-account-info")
    public ResponseEntity<?> credentialTopupAirtime(@RequestParam String ftthAccount,
                                                    @RequestParam String captcha,
                                                    @RequestParam(required = false) String language
    ) {
        if(!captchaService.validateCaptcha(CaptchaKey.FTTH_ACCOUNT, captcha))
            return ResponseEntity.ok(new Response(56, localeFactory.getMessage("captcha.invalid", language)));

        return ResponseEntity.ok(service.findAccountInfo(ftthAccount));
    }

    @PostMapping("/get-ftth-package-by-id")
    public ResponseEntity<?> getFtthPackages(
            @RequestParam(required = false, defaultValue = "en") String language,
            @RequestParam long id) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("REQ: {}, id: {}, lang: {}", isdn, id, language);
        return ResponseEntity.ok(new Response(200, "Success", service.getFtthPackageById(language, id)));
    }
}
