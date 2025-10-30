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
public enum PostType implements CodeEnum {
    CALL(0),
    SMS(1),
    OTHERS(2),
    DATA(3),
    VAS(4);

    private final int type;

    private static final Map<Integer, PostType> CODE_MAP = new HashMap<>();

    static {
        for (PostType value : PostType.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static PostType fromCode(Integer type) {
        PostType result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public Integer getType() {
        return type;
    }
}
