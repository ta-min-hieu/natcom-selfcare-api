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
public enum TransferType implements CodeEnum {
    POINT("POINT"), // Đổi theo điểm
    BALANCE("BALANCE"); // Đổi theo dung lượng

    private final String type;

    private static final Map<String, TransferType> CODE_MAP = new HashMap<>();

    static {
        for (TransferType value : TransferType.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static TransferType fromCode(String type) {
        TransferType result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
