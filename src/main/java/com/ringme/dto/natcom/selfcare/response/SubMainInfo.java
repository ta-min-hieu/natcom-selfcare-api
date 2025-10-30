package com.ringme.dto.natcom.selfcare.response;

import com.google.gson.annotations.SerializedName;
import com.ringme.enums.selfcare.SubType;
import lombok.Data;

@Data
public class SubMainInfo {
    private String name;
    private SubType subType;
    private String language;
    @SerializedName("avatar_url")
    private String avatarUrl;
    private long subId;
}
