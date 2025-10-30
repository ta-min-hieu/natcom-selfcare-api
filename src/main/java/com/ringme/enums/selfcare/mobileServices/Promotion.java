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
public enum Promotion implements CodeEnum {
    DATA("Data"),
    XCHANGE("Xchange"),
    VOICE("Voice");

    private final String type;

    private static final Map<String, Promotion> CODE_MAP = new HashMap<>();

    static {
        for (Promotion value : Promotion.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static Promotion fromCode(String type) {
        Promotion result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
