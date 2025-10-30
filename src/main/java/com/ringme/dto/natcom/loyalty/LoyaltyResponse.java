package com.ringme.dto.natcom.loyalty;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Log4j2
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoyaltyResponse<MinHieu> {
    private String code;
    private String message;

    private Map<String, MinHieu> dynamicData = new HashMap<>();

    private String pointId;
    private Long vtAccId;
    private Long totalRankPoint;
    private Long totalUsePoint;
    private Long totalUsePointExp;

    @JsonAnyGetter
    public Map<String, MinHieu> getDynamicData() {
        return dynamicData;
    }

    public <T> T getData(TypeReference<T> typeReference) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        return dynamicData.values().stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(value -> mapper.convertValue(value, typeReference))
                .orElse(null);
    }

    @JsonAnySetter
    public void set(String key, MinHieu value) {
        dynamicData.put(key, value);
    }
}
