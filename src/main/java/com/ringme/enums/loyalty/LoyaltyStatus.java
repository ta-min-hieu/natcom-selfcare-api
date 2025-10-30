package com.ringme.enums.loyalty;

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
public enum LoyaltyStatus implements CodeEnum {
    ACTIVE(1),
    NO_ACTIVE(0);

    private final int type;

    private static final Map<Integer, LoyaltyStatus> CODE_MAP = new HashMap<>();

    static {
        for (LoyaltyStatus value : LoyaltyStatus.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static LoyaltyStatus fromCode(Integer type) {
        LoyaltyStatus result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public Integer getType() {
        return type;
    }
}
