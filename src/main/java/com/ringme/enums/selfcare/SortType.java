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
public enum SortType implements CodeEnum {
    ASC("asc"),
    DESC("desc");

    private final String type;

    private static final Map<String, SortType> CODE_MAP = new HashMap<>();

    static {
        for (SortType value : SortType.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static SortType fromCode(String type) {
        SortType result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
