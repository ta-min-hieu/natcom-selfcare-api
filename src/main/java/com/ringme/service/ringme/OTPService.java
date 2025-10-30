package com.ringme.service.ringme;

import com.ringme.common.Common;
import com.ringme.common.Helper;
import com.ringme.common.MTStub;
import com.ringme.config.LocaleFactory;
import com.ringme.enums.OTPKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class OTPService {
    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    JwtService jwtService;

    @Autowired
    LocaleFactory languageFactory;
    @Value("${sms.gateway.url}")
    private String url;
    @Value("${sms.gateway.username}")
    private String username;
    @Value("${sms.gateway.password}")
    String password;
    @Value("${sms.gateway.service-id}")
    String serviceId;
    @Value("${sms.gateway.short-code}")
    String from;

    String xmlns = "http://tempuri.com/";
    public boolean generateOTP(OTPKey otpKey, String language) {
        String otp;

        if(Common.whiteList.contains(jwtService.getUsernameFromJwt()))
            otp = "111111";
        else
            otp = Helper.generateRandomOTP();

        redisTemplate.opsForValue().set(getOTPRedisKey(otpKey), otp, 1, TimeUnit.MINUTES);

        try {
            sendOTP(jwtService.getUsernameFromJwt(), languageFactory.getMessage("send-otp-content", language).replace("{{otp}}", otp));
        } catch (Exception e) {
            log.error("otpKey: {}, error: {}", otpKey, e.getMessage(), e);
            return false;
        }

        return true;
    }
    public void sendOTP(String to, String content) {
        String charset = MTStub.detectCharsetStr(content);
        log.info("Charset: {}", charset);
        MTStub stub = new MTStub(url, xmlns, username, password);
        int result = -1;

        String sessionId = "ringme-" + System.currentTimeMillis();

        result = stub.send(sessionId, serviceId, from, to, "1", content.getBytes(StandardCharsets.UTF_16BE), "0");

        log.info("From ws={}|{} send to: {} Content: [{}] with the result: {}", url, from, to, content, result);

    }
    public void removeOTP(OTPKey otpKey) {
        redisTemplate.delete(getOTPRedisKey(otpKey));
        redisTemplate.delete(getCountOTPRedisKey(otpKey));
    }

    public Map<String, String> validateOTP(OTPKey otpKey, String otp) {
        Map<String, String> map = new HashMap<>();
        int count = countEnterOTPRedisKey(otpKey);
        if(count > 5) {
            removeOTP(otpKey);
            map.put("code", "2");
            map.put("message", "You have entered the OTP incorrectly more than 5 times");
            return map;
        }
        Object value = redisTemplate.opsForValue().get(getOTPRedisKey(otpKey));
        if(value == null || !value.toString().equalsIgnoreCase(otp.trim())) {
            map.put("code", "1");
            map.put("message", "OTP invalid");
            return map;
        }

        removeOTP(otpKey);
        map.put("code", "0");
        map.put("message", "Success");
        return map;
    }

    private String getOTPRedisKey(OTPKey otpKey) {
        return otpKey.getType() + ":" + jwtService.getUsernameFromJwt();
    }

    private String getCountOTPRedisKey(OTPKey otpKey) {
        return "count:" + getOTPRedisKey(otpKey);
    }

    private int countEnterOTPRedisKey(OTPKey otpKey) {
        int quantity = 0;

        Object value = redisTemplate.opsForValue().get(getCountOTPRedisKey(otpKey));
        if(value != null)
            quantity = Integer.parseInt(value.toString());

        redisTemplate.opsForValue().set(getCountOTPRedisKey(otpKey), ++quantity, 1, TimeUnit.MINUTES);

        return quantity;
    }
}
