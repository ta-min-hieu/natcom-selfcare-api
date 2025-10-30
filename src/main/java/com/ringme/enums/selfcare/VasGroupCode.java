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
public enum VasGroupCode implements CodeEnum {
    INFORMATION("VAS: Information"),
    ENTERTAINMENT("VAS: Entertainment"),
    UTILITIES("VAS: Utilities"),
    GAMES("VAS: Games"),
    MUSIC("VAS: Music"),
    SOCIAL("VAS: Social"),
    EDUCATION("VAS: Education");

    private final String type;

    private static final Map<String, VasGroupCode> CODE_MAP = new HashMap<>();

    static {
        for (VasGroupCode value : VasGroupCode.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static VasGroupCode fromCode(String type) {
        VasGroupCode result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
