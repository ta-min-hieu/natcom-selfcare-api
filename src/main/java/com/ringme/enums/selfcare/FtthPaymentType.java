package com.ringme.enums.selfcare;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ringme.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum FtthPaymentType implements CodeEnum {
    PAY_OFF_OLD_DEBT(1),
    SETTLEMENT_BEFORE_SERVICE_CANCELLATION(0),
    PAY_3_MONTHS(3),
    PAY_6_MONTHS(6),
    PAY_12_MONTHS(12),
    ;

    private final int type;

    private static final Map<Integer, FtthPaymentType> CODE_MAP = new HashMap<>();

    static {
        for (FtthPaymentType value : FtthPaymentType.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static FtthPaymentType fromCode(Integer type) {
        FtthPaymentType result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public Integer getType() {
        return type;
    }
}
