package com.ringme.enums.selfcare;

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
public enum ServiceGroupType implements CodeEnum {
    XCHANGE("(3)"),
    DATA("(4)");

    private final String type;

    private static final Map<String, ServiceGroupType> CODE_MAP = new HashMap<>();

    static {
        for (ServiceGroupType value : ServiceGroupType.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static ServiceGroupType fromCode(String type) {
        ServiceGroupType result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
