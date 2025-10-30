package com.ringme.service.ringme;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.ringme.enums.CaptchaKey;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class CaptchaService {
    @Autowired
    private DefaultKaptcha captchaProducer;
    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    JwtService jwtService;

    public String generateCaptcha(String isdn, CaptchaKey captchaKey) {
        try {

            SpecCaptcha captcha = new SpecCaptcha(160, 48);
            captcha.setFont(Captcha.FONT_10, 40);
            captcha.setCharType(Captcha.TYPE_DEFAULT);
            captcha.setLen(6);
            String captchaText = captcha.text();
            log.info("{} captchaText: {}", isdn, captchaText);
            String base64Image = captcha.toBase64();
            log.debug("base64Image: {}", base64Image);
//            String captchaText = captchaProducer.createText().trim().toUpperCase();
            redisTemplate.opsForValue().set(getCaptchaRedisKey(captchaKey), captchaText, 5, TimeUnit.MINUTES);

//            BufferedImage captchaImage = captchaProducer.createImage(captchaText);
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(captchaImage, "png", baos);
//            byte[] imageBytes = baos.toByteArray();
//
//            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
//            return "data:image/png;base64," + base64Image;
            return  base64Image;

        } catch (Exception e) {
            log.error("Failed to generate captcha image", e);
            throw new RuntimeException("Failed to generate captcha image", e);
        }
    }

    public void removeCaptcha(CaptchaKey captchaKey) {
        redisTemplate.delete(getCaptchaRedisKey(captchaKey));
    }

    public boolean validateCaptcha(CaptchaKey captchaKey, String captchaText) {
        Object value = redisTemplate.opsForValue().get(getCaptchaRedisKey(captchaKey));
        if(value == null || !value.toString().equalsIgnoreCase(captchaText.trim())) {
            removeCaptcha(captchaKey);
            return false;
        }

        removeCaptcha(captchaKey);
        return true;
    }

    private String getCaptchaRedisKey(CaptchaKey captchaKey) {
        return captchaKey.getType() + ":" + jwtService.getUsernameFromJwt();
    }
}
