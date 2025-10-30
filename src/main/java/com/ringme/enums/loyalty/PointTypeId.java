package com.ringme.enums.loyalty;

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
public enum PointTypeId implements CodeEnum {
    RANKING(1000000), // Điểm xét hạng
    CONSUMPTION(1000001); // Điểm tiêu dùng

    private final int type;

    private static final Map<Integer, PointTypeId> CODE_MAP = new HashMap<>();

    static {
        for (PointTypeId value : PointTypeId.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static PointTypeId fromCode(Integer type) {
        PointTypeId result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public Integer getType() {
        return type;
    }
}
