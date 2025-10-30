package com.ringme.enums.loyalty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ringme.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum VoucherStatus implements CodeEnum {
    EXCHANGED(0),
    USED(1);

    private final int type;

    private static final Map<Integer, VoucherStatus> CODE_MAP = new HashMap<>();

    static {
        for (VoucherStatus value : VoucherStatus.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static VoucherStatus fromCode(Integer type) {
        VoucherStatus result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public Integer getType() {
        return type;
    }
}
