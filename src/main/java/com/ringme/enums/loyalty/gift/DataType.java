package com.ringme.enums.loyalty.gift;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ringme.enums.CodeEnum;
import com.ringme.enums.selfcare.SubType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum DataType implements CodeEnum {
    AIRTIME("AIRTIME"), // Đổi điểm ra tài khoản airtime
    DATA("DATA"), // Đổi điểm ra tài khoản data
    SMS("SMS"), // Đổi điểm ra tài khoản khác
    COMBO("COMBO"), // Đổi điểm ra combo(Voice+SMS)
    YOUTUBE("YOUTUBE"), // Đổi điểm ra thời gian dùng youtube
    VAS("VAS"), // Đổi điểm ra gói vas
    LUCKYCODE("LUCKYCODE"), // Đổi điểm ra luckycode
    DIFFERENT("DIFFERENT"); // Đổi điểm ra loại khác

    private final String type;

    private static final Map<String, DataType> CODE_MAP = new HashMap<>();

    static {
        for (DataType value : DataType.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static DataType fromCode(String type) {
        DataType result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
