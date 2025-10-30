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
public enum Xchange implements CodeEnum {
    SUPER_XCHANGE("Super Xchange"),
    XCHANGE("Xchange"),
    ;

    private final String type;

    private static final Map<String, Xchange> CODE_MAP = new HashMap<>();

    static {
        for (Xchange value : Xchange.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static Xchange fromCode(String type) {
        Xchange result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
