package com.ringme.enums.natcash;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ringme.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum NatcashCallbackType implements CodeEnum {
    TOPUP_AIRTIME(1),
    FTTH(2),
    MOBILE_SERVICE_VAS(3),
    SHARE_PLAN(4),
    NAT_FUND(5),
    ;

    private final int type;

    private static final Map<Integer, NatcashCallbackType> CODE_MAP = new HashMap<>();

    static {
        for (NatcashCallbackType value : NatcashCallbackType.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static NatcashCallbackType fromCode(Integer type) {
        NatcashCallbackType result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public Integer getType() {
        return type;
    }
}
