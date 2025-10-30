package com.ringme.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum OTPKey implements CodeEnum {
    PAYMENT_DATA_VAS("PaymentDataVas"),
    PAYMENT_DATA_PLUS("PaymentDataPlus"),
    I_SHARE("IShare"),
    GIFT_DATA_TO_FRIEND("GiftDataToFriend"),
    NAT_FUND("NatFund"),
    ;

    private final String type;

    private static final Map<String, OTPKey> CODE_MAP = new HashMap<>();

    static {
        for (OTPKey value : OTPKey.values())
            CODE_MAP.put(value.type, value);
    }

    @JsonCreator
    public static OTPKey fromCode(String type) {
        OTPKey result = CODE_MAP.get(type);
        if (result == null)
            throw new IllegalArgumentException("Invalid code: " + type);

        return result;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
