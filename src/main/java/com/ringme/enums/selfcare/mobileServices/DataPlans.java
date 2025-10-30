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
public enum DataPlans implements CodeEnum {
    BUNDLE("Bundle Plans"),
    SUPERDATA_PLANS("SuperData Plans"),
    DATA_ONLY_PLANS("Data Only Plans"),
    DCOM_ROUTER_PLANS("Dcom/Router Plans"),
    FACEBOOK_YOUTUBE_PLANS("Facebook/Youtube Plans"),
    CRAZYDATA_PLANS("CrazyData Plans"),
    ;

    private final String type;

    private static final Map<String, DataPlans> CODE_MAP = new HashMap<>();

    static {
        for (DataPlans value : DataPlans.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static DataPlans fromCode(String type) {
        DataPlans result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
