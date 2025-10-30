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
public enum SubType implements CodeEnum {
    PREPAID(1),     // Trả trước
    POSTPAID(2);    // Trả sau

    private final int type;

    private static final Map<Integer, SubType> CODE_MAP = new HashMap<>();

    static {
        for (SubType value : SubType.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static SubType fromCode(int type) {
        SubType result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public Integer getType() {
        return type;
    }
}
