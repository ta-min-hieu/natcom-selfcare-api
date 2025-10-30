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
public enum MobileServices implements CodeEnum {
    PROMOTION("Promotion"),
    DATA_PLANS("Data Plans"),
    XCHANGE_PLANS("Xchange Plans"),
    VOICE_PLANS("Voice Plans"),
    SMS_PLANS("SMS Plans"),
    ;

    private final String type;

    private static final Map<String, MobileServices> CODE_MAP = new HashMap<>();

    static {
        for (MobileServices value : MobileServices.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static MobileServices fromCode(String type) {
        MobileServices result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
