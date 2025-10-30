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
public enum LogCdr implements CodeEnum {
    NATCASH_FTTH("NATCASH_FTTH"),
    NATCASH_BUY_MOBILE_SERVICE("NATCASH_BUY_MOBILE_SERVICE"),
    NATCASH_SHARE_MOBILE_SERVICE("NATCASH_SHARE_MOBILE_SERVICE"),
    NATCASH_BUY_VAS("NATCASH_BUY_VAS"),
    NATCASH_SHARE_VAS("NATCASH_SHARE_VAS"),
    NATCASH_RECHARGE("NATCASH_RECHARGE"),
    CARD_RECHARGE("CARD_RECHARGE"),
    TKG_BUY_MOBILE_SERVICE("TKG_BUY_MOBILE_SERVICE"),
    TKG_SHARE_MOBILE_SERVICE("TKG_SHARE_MOBILE_SERVICE"),
    TKG_BUY_VAS("TKG_BUY_VAS"),
    TKG_SHARE_VAS("TKG_SHARE_VAS"),
    TKG_DATA_PLUS("TKG_DATA_PLUS"),

    CMS_REFUND("CMS_REFUND"),
    ROTATE_LOG("ROTATE_LOG"),
    ;

    private final String type;

    private static final Map<String, LogCdr> CODE_MAP = new HashMap<>();

    static {
        for (LogCdr value : LogCdr.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static LogCdr fromCode(String type) {
        LogCdr result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
