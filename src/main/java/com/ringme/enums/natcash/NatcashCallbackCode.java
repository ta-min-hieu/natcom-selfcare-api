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
public enum NatcashCallbackCode implements CodeEnum {
    SUCCESS(1),
    UNKNOW(-3),
    FAILED(-1);

    private final int type;

    private static final Map<Integer, NatcashCallbackCode> CODE_MAP = new HashMap<>();

    static {
        for (NatcashCallbackCode value : NatcashCallbackCode.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static NatcashCallbackCode fromCode(Integer type) {
        NatcashCallbackCode result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public Integer getType() {
        return type;
    }
}
