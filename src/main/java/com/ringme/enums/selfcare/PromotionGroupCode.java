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
public enum PromotionGroupCode implements CodeEnum {
    PROMOTION("0. Promotion");

    private final String type;

    private static final Map<String, PromotionGroupCode> CODE_MAP = new HashMap<>();

    static {
        for (PromotionGroupCode value : PromotionGroupCode.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static PromotionGroupCode fromCode(String type) {
        PromotionGroupCode result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
