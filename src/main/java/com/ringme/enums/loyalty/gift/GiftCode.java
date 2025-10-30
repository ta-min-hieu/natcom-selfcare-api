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
public enum GiftCode implements CodeEnum {
    CHANGE_DATA("CHANGE_DATA"),
    CHANGE_SMS("CHANGE_SMS"),
    CHANGE_VOICE("CHANGE_VOICE"),
    CHANGE_CHARGE("CHANGE_CHARGE");

    private final String type;

    private static final Map<String, GiftCode> CODE_MAP = new HashMap<>();

    static {
        for (GiftCode value : GiftCode.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static GiftCode fromCode(String type) {
        GiftCode result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
