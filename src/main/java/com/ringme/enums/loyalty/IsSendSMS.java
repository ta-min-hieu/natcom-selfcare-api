package com.ringme.enums.loyalty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ringme.enums.CodeEnum;
import com.ringme.enums.loyalty.gift.TransTypeId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum IsSendSMS implements CodeEnum {
    SEND(1),
    NOT_SEND(0);

    private final int type;

    private static final Map<Integer, IsSendSMS> CODE_MAP = new HashMap<>();

    static {
        for (IsSendSMS value : IsSendSMS.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static IsSendSMS fromCode(Integer type) {
        IsSendSMS result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public Integer getType() {
        return type;
    }
}
