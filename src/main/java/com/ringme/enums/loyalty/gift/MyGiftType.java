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
public enum MyGiftType implements CodeEnum {
    VOUCHER("voucher"),
    SOUVENIR("souvenir");

    private final String type;

    private static final Map<String, MyGiftType> CODE_MAP = new HashMap<>();

    static {
        for (MyGiftType value : MyGiftType.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static MyGiftType fromCode(String type) {
        MyGiftType result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
