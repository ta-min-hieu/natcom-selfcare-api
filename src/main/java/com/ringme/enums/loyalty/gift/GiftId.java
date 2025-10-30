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
public enum GiftId implements CodeEnum {
    CHANGE_DATA(1000000000),
    CHANGE_SMS(1000000001),
    CHANGE_VOICE(1000000002),
    CHANGE_CHARGE(1000000003);

    private final int type;

    private static final Map<Integer, GiftId> CODE_MAP = new HashMap<>();

    static {
        for (GiftId value : GiftId.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static GiftId fromCode(Integer type) {
        GiftId result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public Integer getType() {
        return type;
    }
}
