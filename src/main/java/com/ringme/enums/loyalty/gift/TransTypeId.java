package com.ringme.enums.loyalty.gift;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ringme.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum TransTypeId implements CodeEnum {
    VOUCHER(1000008), // Đổi điểm voucher
    SOUVENIR(1000039), // Đổi điểm souvenir
    TEST(1000024); // Đổi điểm test

    private final int type;

    private static final Map<Integer, TransTypeId> CODE_MAP = new HashMap<>();

    static {
        for (TransTypeId value : TransTypeId.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static TransTypeId fromCode(Integer type) {
        TransTypeId result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public Integer getType() {
        return type;
    }
}
