package com.ringme.controller.nologin;

import com.ringme.dto.record.Response;
import com.ringme.service.ringme.RingmeLoyaltyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Log4j2
@RestController
@RequestMapping("/clear-cache")
public class ClearCacheController {
    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    RingmeLoyaltyService ringmeLoyaltyService;

    @PostMapping(value = "/all")
    public ResponseEntity<?> all() {
        log.info("");
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty())
            redisTemplate.delete(keys);
        return ResponseEntity.ok(new Response(200, "success"));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> alll() {
        log.info("");
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty())
            redisTemplate.delete(keys);
        return ResponseEntity.ok(new Response(200, "success"));
    }

    @PostMapping(value = "/my-gift")
    public ResponseEntity<?> myGift(@RequestParam String isdn) {
        log.info("");
        ringmeLoyaltyService.clearCacheMyGift(isdn);
        return ResponseEntity.ok(new Response(200, "success"));
    }
}
