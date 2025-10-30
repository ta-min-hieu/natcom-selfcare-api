package com.ringme.enums.loyalty.gift;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ringme.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum GiftName implements CodeEnum {
    CHANGE_DATA("Change point to data"),
    CHANGE_SMS("Change point to sms"),
    CHANGE_VOICE("Change point to voice"),
    CHANGE_CHARGE("Change point to charge");

    private final String type;

    private static final Map<String, GiftName> CODE_MAP = new HashMap<>();

    static {
        for (GiftName value : GiftName.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static GiftName fromCode(String type) {
        GiftName result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
