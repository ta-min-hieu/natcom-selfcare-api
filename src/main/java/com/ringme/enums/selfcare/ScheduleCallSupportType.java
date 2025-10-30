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
public enum ScheduleCallSupportType implements CodeEnum {
    MOBILE("Mobile"),
    NATCASH("Natcash"),
    FTTH_IPLL("FTTH/IPLL");

    private final String type;

    private static final Map<String, ScheduleCallSupportType> CODE_MAP = new HashMap<>();

    static {
        for (ScheduleCallSupportType value : ScheduleCallSupportType.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static ScheduleCallSupportType fromCode(String type) {
        ScheduleCallSupportType result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
