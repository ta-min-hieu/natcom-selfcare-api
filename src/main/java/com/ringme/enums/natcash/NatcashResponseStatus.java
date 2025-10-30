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
public enum NatcashResponseStatus implements CodeEnum {
    MSG_SUCCESS(0),
    ERR_COMMON(1), // Unknow error
    ERR_MERCHANT_NOT_FOUND(100), // Partner not found
    ERR_TRANSACTION_NOT_FOUND(101),
    ERR_DUPLICATE_REQUEST_ID(102),
    ERR_TRANSACTION_EXPIRED(2),
    ERR_PARAMETERS_INVALID(3),
    ERR_MISSING_PARAMETERS(4),
    PARTNER_CODE_OR_SERVICE_CODE_NOT_FOUND(10112);

    private final int type;

    private static final Map<Integer, NatcashResponseStatus> CODE_MAP = new HashMap<>();

    static {
        for (NatcashResponseStatus value : NatcashResponseStatus.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static NatcashResponseStatus fromCode(Integer type) {
        NatcashResponseStatus result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public Integer getType() {
        return type;
    }
}
