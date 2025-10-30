package com.ringme.enums.loyalty.souvenir;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ringme.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum SouvenirStatus implements CodeEnum {
    ACTIVE(1),
    INACTIVE(0);

    private final int type;

    private static final Map<Integer, SouvenirStatus> CODE_MAP = new HashMap<>();

    static {
        for (SouvenirStatus value : SouvenirStatus.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static SouvenirStatus fromCode(Integer type) {
        SouvenirStatus result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public Integer getType() {
        return type;
    }
}
