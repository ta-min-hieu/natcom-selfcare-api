package com.ringme.enums.selfcare.mobileServices;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ringme.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum SmsPlans implements CodeEnum {
    SMS_ON_NET("SMS On-Net"),
    SMS_OFF_NET("SMS Off-Net"),
    ;

    private final String type;

    private static final Map<String, SmsPlans> CODE_MAP = new HashMap<>();

    static {
        for (SmsPlans value : SmsPlans.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static SmsPlans fromCode(String type) {
        SmsPlans result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
