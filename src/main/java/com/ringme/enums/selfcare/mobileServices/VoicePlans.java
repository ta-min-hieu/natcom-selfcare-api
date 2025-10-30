package com.ringme.enums.selfcare.mobileServices;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ringme.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum VoicePlans implements CodeEnum {
    VOICE_ON_NET("Voice On-Net"),
    VOICE_OFF_NET("Voice Off-Net"),
    VOICE_INTERNATIONAL("Voice International"),
    ;

    private final String type;

    private static final Map<String, VoicePlans> CODE_MAP = new HashMap<>();

    static {
        for (VoicePlans value : VoicePlans.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static VoicePlans fromCode(String type) {
        VoicePlans result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
