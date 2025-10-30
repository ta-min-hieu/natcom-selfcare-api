package com.ringme.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum CaptchaKey implements CodeEnum {
    TOPUP_AIRTIME("TopupAirtime"),
    FTTH_ACCOUNT("FtthAccount"),;

    private final String type;

    private static final Map<String, CaptchaKey> CODE_MAP = new HashMap<>();

    static {
        for (CaptchaKey value : CaptchaKey.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static CaptchaKey fromCode(String type) {
        CaptchaKey result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
